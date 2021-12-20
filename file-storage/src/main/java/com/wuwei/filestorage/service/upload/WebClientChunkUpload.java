package com.wuwei.filestorage.service.upload;

import com.wuwei.filestorage.entity.*;
import com.wuwei.filestorage.utils.FileUtils;
import com.wuwei.filestorage.utils.FileWebClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static java.util.stream.Collectors.toList;

public class WebClientChunkUpload extends Upload {
    private Resource resource;

    private int chunkSize = 4 * 1024 * 1024;

    private List<byte[]> chunkList = new LinkedList<>();

    private final MultipartBodyBuilder chunkUploadBuilder = new MultipartBodyBuilder();

    private final ParameterizedTypeReference<ResultDto<ChunkUpCheckDto>> chunkTypeReference =
            new ParameterizedTypeReference<ResultDto<ChunkUpCheckDto>>() {
            };

    private WebClientChunkUpload() {
    }

    public WebClientChunkUpload(InputStream inputStream) {
        this.resource = new InputStreamResource(inputStream);
    }

    public WebClientChunkUpload(MultipartFile file) {
        this.resource = file.getResource();
    }

    public WebClientChunkUpload(File file) {
        this.resource = new FileSystemResource(file);
    }

    public WebClientChunkUpload(byte[] fileByteArray) {
        this.resource = new ByteArrayResource(fileByteArray);
    }

    public int getChunkSize() {
        return chunkSize;
    }

    public WebClientChunkUpload addChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
        this.chunkList = new LinkedList<>();
        this.convertToChunk();
        return this;
    }

    public WebClientChunkUpload init(String eteamsId, String name, String module, long size, String lastModified, String lastModifiedDate) {
        logger.info(">>>>>>WebClientChunkUpload is init<<<<<<");
        this.name = name;
        this.size = size;
        this.module = module;
        this.eteamsId = eteamsId;
        this.lastModified = lastModified;
        this.lastModifiedDate = lastModifiedDate;
        chunkUploadBuilder.part("size", size);
        chunkUploadBuilder.part("fileName", name);
        chunkUploadBuilder.part("module", module);
        chunkUploadBuilder.part("lastModified", this.lastModified);
        chunkUploadBuilder.part("lastModifiedDate", this.lastModifiedDate);
        return this;
    }

    public ResultDto<UploadModuleDto> start() {
        // 计算md5
        this.calculateMD5();
        // 文件做切片处理
        this.convertToChunk();
        int totalSize = chunkList.size();
        // 最后一个分片的编号
        int lastIndex = totalSize - 1;
        List<byte[]> firstChunkList = chunkList.stream()
                .limit(totalSize - 1)
                .collect(toList());
        byte[] lastChunkByteArray = chunkList.get(lastIndex);

        logger.info(">>>>>>chunkUploadCheck start<<<<<<");
        logger.info(">>>>>>chunkUploadCheck current eteamsId:{}", this.eteamsId);
        logger.info(">>>>>>chunkUploadCheck current url:{}", FileUtils.getHost() + FileUtils.getChunkUpCheckUrl());
        // 各个分片的上传
        for (int index = 0; index < firstChunkList.size(); index++) {
            logger.info(">>>>>>当前是第{}片", index);
            ResultDto<UploadModuleDto> resultDto = this.checkAndUpload(index, firstChunkList.get(index));
            if (null != resultDto) {
                return resultDto;
            }
        }

        // 最后一片上传
        logger.info(">>>>>>当前是最后一片");
        return Optional.ofNullable(this.checkAndUpload(lastIndex, lastChunkByteArray))
                .orElseThrow(() -> new RuntimeException("最后一片上传失败！"));
    }

    private ResultDto<UploadModuleDto> checkAndUpload(int chunkNo, byte[] nowChunkArray) {
        ResultDto<ChunkUpCheckDto> chunkUpCheckDtoTestDto = Optional.ofNullable(chunkUploadCheck().block())
                .orElseThrow(() -> new RuntimeException("分片检查结果为空！"));
        boolean status = chunkUpCheckDtoTestDto.isStatus();
        String message = chunkUpCheckDtoTestDto.getMessage();
        ChunkUpCheckDto chunkUpCheckData = chunkUpCheckDtoTestDto.getData();
        if (!status) {
            throw new RuntimeException(message);
        } else {
            String check = chunkUpCheckData.getCheck();
            String success = chunkUpCheckData.getSuccess();
            if (!"true".equalsIgnoreCase(check) || !"true".equalsIgnoreCase(success)) {
                throw new RuntimeException(">>>>>>chunkUpCheck检测不通过,返回值：ChunkUpCheckDto = " + chunkUpCheckData.toString());
            }
        }

        ResultDto<UploadModuleDto> resultDto = this.sendWebClientUpload(
                new ParameterizedTypeReference<ResultDto<UploadModuleDto>>() {
                }, nowChunkArray, chunkNo
        );
        int uploadCode = resultDto.getCode();
        boolean uploadResult = resultDto.isStatus();
        String uploadMessage = resultDto.getMessage();
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

    private Mono<ResultDto<ChunkUpCheckDto>> chunkUploadCheck() {

        return FileWebClient.postFormData(eteamsId, chunkUploadBuilder, chunkTypeReference);
    }

    private void calculateMD5() {
        Optional.ofNullable(this.resource)
                .ifPresent(tempResource -> {
                    try {
                        InputStream inputStream = tempResource.getInputStream();
                        // 计算文件md5值
                        this.md5 = DigestUtils.md5DigestAsHex(inputStream);
                    } catch (IOException e) {
                        logger.error(">>>>>>获取inputStream失败<<<<<<");
                    }
                });
    }

    private void convertToChunk() {
        try {
            InputStream inputStream = this.resource.getInputStream();
            byte[] chunkByteArray = new byte[chunkSize];
            while (inputStream.read(chunkByteArray) != -1) {
                chunkList.add(chunkByteArray);
                chunkByteArray = new byte[chunkSize];
            }
            this.chunks = chunkList.size();
            chunkUploadBuilder.part("chunks", this.chunks);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private <T> ResultDto<T> sendWebClientUpload(ParameterizedTypeReference<ResultDto<T>> typeReference, byte[] chunkByteArray, int chunk) {

        return new WebClientUpload(chunkByteArray)
                .init(this.eteamsId, this.name, this.module, this.size, this.lastModified, this.lastModifiedDate)
                .addMd5(this.md5)
                .addChunk(chunk)
                .addChunks(this.chunks)
                .blockUpload(typeReference);
    }

}
