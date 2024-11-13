package com.wuwei.filestorage.common.partupload;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.util.DigestUtils;
import reactor.core.publisher.Mono;
import sun.misc.Cleaner;
import sun.nio.ch.DirectBuffer;

import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Map;
import java.util.Optional;

/**
 * 分片上传
 *
 * @author wuwei
 * @since 2021/12/10
 */
public class WebClientChunkUpload extends Upload {

    private File file;

    private int chunkSize = 4 * 1024 * 1024;

    private final MultipartBodyBuilder chunkUploadBuilder = new MultipartBodyBuilder();

    private final ParameterizedTypeReference<ResultDto<ChunkUpCheckDto>> typeReference =
            new ParameterizedTypeReference<ResultDto<ChunkUpCheckDto>>() {
            };

    private WebClientChunkUpload() {
    }

    public WebClientChunkUpload(File file) {
        this.file = file;
    }

    public int getChunkSize() {
        return chunkSize;
    }

    public WebClientChunkUpload addChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
        return this;
    }

    public WebClientChunkUpload init(String tenantKey, String name, String module, long size, String lastModified, String lastModifiedDate) {
        logger.info(">>>>>>WebClientChunkUpload is init<<<<<<");
        this.name = name;
        this.size = size;
        this.module = module;
        this.tenantKey = tenantKey;
        this.lastModified = lastModified;
        this.lastModifiedDate = lastModifiedDate;
        chunkUploadBuilder.part("size", size);
        chunkUploadBuilder.part("module", module);
        chunkUploadBuilder.part("fileName", name);
        chunkUploadBuilder.part("tenantKey", tenantKey);
        chunkUploadBuilder.part("lastModified", this.lastModified);
        chunkUploadBuilder.part("lastModifiedDate", this.lastModifiedDate);
        return this;
    }


    public ResultDto<UploadModuleDto> start() throws Exception {
        long filesize = file.length();
        // 计算md5值
        this.md5 = DigestUtils.md5DigestAsHex(new FileInputStream(file));
        // 计算分片总数
        int chunks = (int) filesize % chunkSize > 0 ? ((int) filesize / chunkSize) + 1 : (int) filesize / chunkSize;
        this.chunks = chunks;
        // 开始分片上传
        Cleaner inMLocalCleaner = null;
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
             FileChannel randomAccessFileChannel = randomAccessFile.getChannel()) {
            for (int chunkIndex = 0; chunkIndex < chunks; chunkIndex++) {
                logger.info(">>>>>>当前是第{}片", chunkIndex);

                int startPosition = chunkIndex * chunkSize;
                chunkSize = startPosition + chunkSize > filesize ? (int) filesize - startPosition : chunkSize;
                if (chunkSize <= 0) {
                    break;
                }

                MappedByteBuffer mappedByteBuffer = randomAccessFileChannel.map(FileChannel.MapMode.READ_ONLY, startPosition, chunkSize);
                byte[] partFileArr = new byte[(int) chunkSize];
                mappedByteBuffer.get(partFileArr);
                ResultDto<UploadModuleDto> resultDto = this.checkAndUpload(chunkIndex, partFileArr);
                inMLocalCleaner = ((DirectBuffer) mappedByteBuffer).cleaner();
                if (inMLocalCleaner != null) {
                    inMLocalCleaner.clean();
                }

                if (null != resultDto) {
                    return resultDto;
                }
            }
        } catch (Exception e) {
            logger.error("\n >>>>>> part upload failed: ", e);
        } finally {
            if (inMLocalCleaner != null) {
                inMLocalCleaner.clean();
            }
        }


        return null;
    }


    /**
     * 每个分片的检查与上传
     */
    private ResultDto<UploadModuleDto> checkAndUpload(int chunkNo, byte[] nowChunkArray) {
        ResultDto<ChunkUpCheckDto> chunkUpCheckDtoTestDto = Optional.ofNullable(chunkUploadCheck().block())
                .orElseThrow(() -> new RuntimeException("分片检查结果为空"));
        boolean status = chunkUpCheckDtoTestDto.isStatus();
        String message = chunkUpCheckDtoTestDto.getMessage();
        String detailErrorMessage = chunkUpCheckDtoTestDto.getDetailErrorMessage();
        message = StringUtils.isNotBlank(detailErrorMessage) ? detailErrorMessage : message;
        ChunkUpCheckDto chunkUpCheckData = chunkUpCheckDtoTestDto.getData();
        if (!status) {
            throw new RuntimeException(message);
        } else {
            String check = chunkUpCheckData.getCheck();
            String success = chunkUpCheckData.getSuccess();
            if (!"true".equalsIgnoreCase(check)) {
                throw new RuntimeException(">>>>>>chunkUpCheck检测不通过,返回值：ChunkUpCheckDto = " + chunkUpCheckData.toString());
            }
        }

        ResultDto<UploadModuleDto> resultDto = this.sendWebClientUpload(
                new ParameterizedTypeReference<ResultDto<UploadModuleDto>>() {}, nowChunkArray, chunkNo);
        int uploadCode = resultDto.getCode();
        boolean uploadResult = resultDto.isStatus();
        String uploadMessage = resultDto.getMessage();
        String uploadDetailErrorMessage = resultDto.getDetailErrorMessage();
        uploadMessage = StringUtils.isNotBlank(uploadDetailErrorMessage) ? uploadDetailErrorMessage : uploadMessage;
        if (!uploadResult) {
            throw new RuntimeException("文件片段上传失败，信息：" + uploadMessage);
        }

        if (uploadCode == 101) {
            logger.info(">>>>>>秒传成功<<<<<<");
        } else if (uploadCode != 200) {
            throw new RuntimeException(">>>>>>分片上传失败，信息：" + uploadMessage);
        }

        long fileId = Optional.of(resultDto)
                .map(ResultDto::getData)
                .map(UploadModuleDto::getFileid)
                .orElse(-1L);
        if (-1L == fileId) {
            resultDto = null;
        } else {
            logger.info(">>>>>>最终上传成功结果：ResultDto<UploadModuleDto> = {}", resultDto);
        }

        return resultDto;
    }

    /**
     * 分片检查
     */
    private Mono<ResultDto<ChunkUpCheckDto>> chunkUploadCheck() {
        chunkUploadBuilder.part("chunk", this.chunk);
        chunkUploadBuilder.part("chunks", this.chunks);
        return FileWebClient.postFormData(PAPI_CHUNK_UPLOAD_CHECK_ENDPOINT, chunkUploadBuilder, typeReference);
    }


    /**
     * 请求上传
     */
    private <T> ResultDto<T> sendWebClientUpload(ParameterizedTypeReference<ResultDto<T>> typeReference, byte[] chunkByteArray, int chunk) {

        return new WebClientUpload(chunkByteArray)
                .init(this.tenantKey, this.name, this.module, this.size, this.lastModified, this.lastModifiedDate)
                .addMd5(this.md5)
                .addChunk(chunk)
                .addChunks(this.chunks)
                .addFilterUpload(this::filterUploadBeanMap)
                .blockUpload(typeReference);
    }

    /**
     * 分片上传请求体特殊处理
     */
    private boolean filterUploadBeanMap(Map.Entry<Object, Object> entry) {
        boolean isSaveParam;
        // chunk分片的参数不过滤
        Object key = entry.getKey();
        Object value = entry.getValue();
        if ("chunk".equals(key) || "chunks".equals(key)) {
            return true;
        }

        if (null == value) {
            isSaveParam = false;
        } else if (value instanceof Integer) {
            isSaveParam = 0 != (int) value;
        } else if (value instanceof Long) {
            isSaveParam = 0 != (long) value;
        } else {
            isSaveParam = true;
        }
        return isSaveParam;
    }

}
