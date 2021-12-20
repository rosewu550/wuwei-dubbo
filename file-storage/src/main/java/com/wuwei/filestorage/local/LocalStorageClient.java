package com.wuwei.filestorage.local;


import com.wuwei.filestorage.constant.StorageConstant;
import com.wuwei.filestorage.utils.StorageUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;


@Configuration
public class LocalStorageClient implements InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${storage.server.appKey}")
    private String appKey;

    @Value("${storage.server.module}")
    private String module;

    private final String rootPath = System.getProperty("user.dir") + File.separator + "upload/storage";

    private static final LinkedBlockingQueue<Map<Path, String>> uploadPartMetaStrQueue = new LinkedBlockingQueue<>();

    public String getAppKey() {
        return appKey;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getRootPath() {
        return rootPath;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        try {
            // 在根目录下生成一个文件夹用于存放分片上传事件
            Path multipartEventPath = Paths.get(this.getUploadPartEventPathStr());
            Files.createDirectories(multipartEventPath);
        } catch (FileAlreadyExistsException e) {
            logger.info(">>>>>>MULTIPART_EVENT folder is already exist<<<<<<");
        } catch (Exception ex) {
            throw new RuntimeException(">>>>>>MULTIPART_EVENT folder create or check is failed<<<<<<", ex);
        }
    }

    /**
     * 获取文件全路径
     */
    public String getFilePath(String tenantKey, String filename) {
        return File.separator + this.rootPath
                + File.separator + tenantKey
                + File.separator + this.module
                + File.separator + filename;
    }

    /**
     * 获取文件所在文件夹路径
     */
    public String getFolderPath(String tenantKey) {
        return File.separator + this.rootPath
                + File.separator + tenantKey
                + File.separator + this.module;
    }

    /**
     * 向指定目标上传文件
     */
    private void uploadFile(String fileName, String storagePathStr, Resource fileResource) {
        Path storagePath = Paths.get(storagePathStr);
        OutputStream targetFileOutputStream = Optional.of(storagePath)
                .map(this::createMultiDirectory)
                .filter(Files::exists)
                .map(path -> path.resolve(fileName))
                .map(this::getOutputStream)
                .orElseThrow(() -> new RuntimeException("目标文件不存在"));

        try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(targetFileOutputStream)) {
            InputStream inputStream = fileResource.getInputStream();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

            int len;
            byte[] byteArray = new byte[4096];
            while ((len = bufferedInputStream.read(byteArray)) != -1) {
                bufferedOutputStream.write(byteArray, 0, len);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public InputStream downloadFile(String tenantKey, String fileId) {
        String filePath = this.getFilePath(tenantKey, fileId);
        try (FileInputStream fileInputStream = new FileInputStream(filePath)) {
            return new BufferedInputStream(fileInputStream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 复制文件
     */
    public String copyFile(String tenantKey, String sourceFileId, String destinationFolderPath) {
        String sourceFilePath = this.getFilePath(tenantKey, sourceFileId);
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String destFilePath = destinationFolderPath + File.separator + uuid;
        try (FileOutputStream destOutputStream = new FileOutputStream(new File(destFilePath))) {
            FileUtils.copyFile(new File(sourceFilePath), destOutputStream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return uuid;
    }

    /**
     * 初始化分片
     */
    public String initMultipartUpload(String tenantKey, String fileId) {
        // 保存分片上传事件信息
        String uploadId = UUID.randomUUID().toString().replace("-", "");
        String storagePath = this.rootPath + File.separator + "MULTIPART_EVENT" + File.separator + uploadId;
        try {
            Files.createFile(Paths.get(storagePath));
            // 生成分片上传临时存放点
            String filePath = this.getTempPartFolderPathStr(tenantKey, fileId);
            Path partFolderPath = Paths.get(filePath);
            Files.createDirectories(partFolderPath);
        } catch (Exception e) {
            throw new RuntimeException(">>>>>>create temporary file part upload folder failed<<<<<< ", e);
        }
        return uploadId;
    }

    /**
     * 上传分片
     */
    public void uploadPart(String tenantKey, String fileId, String uploadId, int partNumber, long partSize, InputStream input) {
        argNotNull(input == null, "inputStream is null");
        argNotNull(partNumber <= 0, "part number can not less than or equal to zero");
        String tempUploadPartFolderPath = this.getTempPartFolderPathStr(tenantKey, fileId);
        // 分片临时存放文件夹是否存在，不存在则创建
        this.createDirectory(tempUploadPartFolderPath);
        // 计算当前分片md5
        String currentPartMD5 = StorageUtils.calculateMD5(input);
        // 查询当前分片上传事件的信息
        Map<String, Map<String, String>> uploadPartMap = getPartUploadEvent(uploadId);
        Map<String, String> uploadPartMetaMap = uploadPartMap.get(String.valueOf(partNumber));
        if (null != uploadPartMetaMap) {
            String partMD5 = uploadPartMetaMap.get(StorageConstant.MD5);
            if (currentPartMD5.equals(partMD5)) {
                // 该分片已存在，不用上传
                return;
            }
        }

        // 上传分片
        this.uploadFile(partNumber + "", tempUploadPartFolderPath, new InputStreamResource(input));
        // 更新当前分片上传事件信息
        putPartUploadEvent(uploadId, partNumber + "", partSize + "", currentPartMD5);
    }

    /**
     * 合并分片成一个文件
     */
    public void mergePart(String tenantKey, String fileId, String uploadId) {
        String targetFolderPathStr = this.getFilePath(tenantKey, fileId);
        String tempPartFolderPathStr = getTempPartFolderPathStr(tenantKey, fileId);

        // 获取当前分片上传的所有分片信息
        Map<String, Map<String, String>> uploadPartMap = this.getPartUploadEvent(uploadId);
        uploadPartMap = uploadPartMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> newValue));
        // 校验记录的分片上传事件中序列号是否连续递增
        List<String> partNumberAscList = new ArrayList<>(uploadPartMap.keySet());
        validatePartNum(partNumberAscList);
        // 开始合并
        this.mergePartFile(tempPartFolderPathStr, targetFolderPathStr, uploadPartMap);
    }

    /**
     * 中止分片上传
     */
    public void abortMultiPartUpload(String tenantKey, String fileId, String uploadId) {
        String currentPartEventFilePathStr = this.getUploadPartEventFilePathStr(uploadId);
        Path currentPartEventFilePath = Paths.get(currentPartEventFilePathStr);
        String tempUploadPartFilePath = this.getTempPartFolderPathStr(tenantKey, fileId);
        Path allPartPath = Paths.get(tempUploadPartFilePath);
        try (FileChannel fileChannel = FileChannel.open(allPartPath);
             Stream<Path> walk = Files.walk(allPartPath)) {
            fileChannel.lock();
            boolean isDelete = Files.deleteIfExists(currentPartEventFilePath);
            if (isDelete && Files.isDirectory(allPartPath)) {
                walk.map(Path::toFile).forEach(File::deleteOnExit);
            }
        } catch (Exception e) {
            throw new RuntimeException("abort multipart upload is failed", e);
        }
    }

    /**
     * 获取所有上传成功的分片
     */
    public Map<String, Map<String, String>> listParts(String tenantKey, String fileId, String uploadId) {
        String uploadPartFolderPathStr = this.getTempPartFolderPathStr(tenantKey, fileId);
        Map<String, Map<String, String>> uploadPartMap = this.getPartUploadEvent(uploadId);
        uploadPartMap = uploadPartMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> newValue));
        return uploadPartMap.entrySet()
                .stream()
                .filter(entry -> {
                    String partNumber = entry.getKey();
                    Map<String, String> partMetaMap = entry.getValue();
                    String partMD5 = partMetaMap.get(StorageConstant.MD5);
                    String partFilePathStr = uploadPartFolderPathStr + File.separator + partNumber;
                    return validatePart(partFilePathStr, partMD5);
                }).collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> newValue));

    }

    /**
     * 校验分片序号
     */
    private void validatePartNum(List<String> partNumberAscList) {
        for (int index = 0; index < partNumberAscList.size(); index++) {
            String partNumber = partNumberAscList.get(index);
            if (!String.valueOf(index + 1).equals(partNumber)) {
                throw new RuntimeException("part number is discontinuous");
            }
        }
    }

    private boolean validatePart(String partFilePathStr, String currentPartMD5) {
        Path path = Paths.get(partFilePathStr);
        try (InputStream inputStream = Files.newInputStream(path)) {
            String md5 = StorageUtils.calculateMD5(inputStream);
            if (!md5.equals(currentPartMD5)) {
                throw new RuntimeException("part md5 is mismatch");
            }
        } catch (Exception e) {
            throw new RuntimeException("validate part is failed", e);
        }
        return true;
    }

    /**
     * 合并分片
     */
    private void mergePartFile(String tempPartFolderPathStr, String targetFolderPathStr, Map<String, Map<String, String>> uploadPartMap) {
        argNotNull(null == targetFolderPathStr, "upload part merge folder is not exist");
        argNotNull(null == tempPartFolderPathStr, "upload part temp folder is not exist");
        Path targetFolderPath = Paths.get(targetFolderPathStr);
        Path tempPartFolderPath = Paths.get(tempPartFolderPathStr);
        File tempPartFolder = tempPartFolderPath.toFile();
        String[] uploadPartNameArray = tempPartFolder.list();
        argNotNull(null == uploadPartNameArray, "upload part is not exist");
        List<String> ascUploadPartNameList = Arrays.stream(uploadPartNameArray)
                .sorted()
                .collect(toList());
        // 校验磁盘中物理分片文件序列号是否连续递增
        this.validatePartNum(ascUploadPartNameList);
        // 校验分片md5值
        uploadPartMap.forEach((partNumber, partMetaMap) -> {
            String partMD5 = partMetaMap.get(StorageConstant.MD5);
            String partFilePathStr = tempPartFolderPathStr + File.separator + partNumber;
            validatePart(partFilePathStr, partMD5);
        });

        int position = 0;
        try (FileChannel targetFileOpenChannel
                     = FileChannel.open(Files.createFile(targetFolderPath), StandardOpenOption.WRITE, StandardOpenOption.READ)) {
            targetFileOpenChannel.lock();
            for (String uploadPartName : ascUploadPartNameList) {
                Path currentUploadPartPath = tempPartFolderPath.resolve(uploadPartName);
                byte[] uploadPartByteArray = Files.readAllBytes(currentUploadPartPath);
                int len = uploadPartByteArray.length;
                targetFileOpenChannel
                        .position(position)
                        .write(ByteBuffer.wrap(uploadPartByteArray));
                position = len <= 0 ? 0 : len - 1;
            }
        } catch (Exception e) {
            throw new RuntimeException("merge part failed", e);
        }

    }


    private File getFile(Resource resource) {
        try {
            return resource.getFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private OutputStream getOutputStream(File file) {
        try {
            return FileUtils.openOutputStream(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private OutputStream getOutputStream(Path path) {
        try {
            return Files.newOutputStream(Files.createFile(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Path createMultiDirectory(Path path) {
        try {
            return Files.createDirectories(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private InputStream getInputStream(Resource resource) {
        try {
            return resource.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void putFileToPath(Resource fileResource, String storagePath, String fileName) {
        this.uploadFile(fileName, storagePath, fileResource);
    }

    public void putFile(Resource fileResource, String tenantKey, String url) {
        String filePath = this.getFolderPath(tenantKey);
        this.putFileToPath(fileResource, filePath, url);
    }

    public String putFile(Resource fileResource, String tenantKey) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        this.putFile(fileResource, tenantKey, uuid);
        return uuid;
    }

    /**
     * 在指定路径下创建文件夹,不存在则创建
     */
    private void createDirectory(String folderPath) {
        try {
            Path path = Paths.get(folderPath);
            Files.createDirectories(path);
        } catch (FileAlreadyExistsException e) {
            logger.info(">>>>>>folderPath is already exist<<<<<<");
        } catch (Exception ex) {
            throw new RuntimeException(folderPath + " create or check is failed<<<<<<", ex);
        }
    }

    /**
     * 仅检测文件夹是否存在
     */
    private void checkDirectory(String folderPath) {
        Path path = Paths.get(folderPath);
        boolean isExist = Files.isDirectory(path);
        if (!isExist) {
            throw new FileSystemNotFoundException(folderPath + "not found");
        }
    }

    /**
     * 获取分片上传事件信息 （有并发场景）
     */
    private Map<String, Map<String, String>> getPartUploadEvent(String uploadId) {
        Path path = Paths.get(this.getUploadPartEventPathStr() + File.separator + uploadId);
        try (BufferedReader bufferedReader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            return bufferedReader.lines()
                    .filter(line -> line.endsWith(";"))
                    .map(line -> {
                        int length = line.length();
                        return line.substring(0, length - 1);
                    }).map(line -> line.split(","))
                    .filter(strArray -> strArray.length == 3)
                    .collect(toMap(
                            strArray -> strArray[0],
                            strArray -> {
                                Map<String, String> uploadPartMetaMap = new HashMap<>();
                                uploadPartMetaMap.put("partSize", strArray[1]);
                                uploadPartMetaMap.put("md5", strArray[2]);
                                return uploadPartMetaMap;
                            }, (oldPartNum, newPartNum) -> newPartNum
                    ));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 写入分片上传信息 （并发场景）
     */
    private void putPartUploadEvent(String uploadId, String partNumber, String partSize, String md5) {
        Path uploadPartEventFilePath = Paths.get(this.getUploadPartEventFilePathStr(uploadId));
        String uploadPartMetaLine = String.join(",", Arrays.asList(partNumber, partSize, md5)) + ";";
        try {
            Map<Path, String> uploadPartMetaMap = new HashMap<>();
            uploadPartMetaMap.put(uploadPartEventFilePath, uploadPartMetaLine);
            uploadPartMetaStrQueue.put(uploadPartMetaMap);
        } catch (InterruptedException e) {
            // 抛出InterruptedException后，线程中断状态被清除(改为false)
            // 手动将线程中断状态再改为true,可以让其他线程捕获到异常作出正确的处理
            // 当前线程并未终止
            Thread.currentThread().interrupt();
        }
        List<Map<Path, String>> uploadPartMetaStrList = new ArrayList<>();
        uploadPartMetaStrQueue.drainTo(uploadPartMetaStrList);
        uploadPartMetaStrList.stream()
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .forEach(uploadPartEntry -> {
                    Path currentPath = uploadPartEntry.getKey();
                    String currentLineStr = uploadPartEntry.getValue();
                    try (FileChannel openFileChannel
                                 = FileChannel.open(currentPath, StandardOpenOption.WRITE)) {
                        openFileChannel.lock();
                        byte[] lineStrByteArray = currentLineStr.getBytes(StandardCharsets.UTF_8);
                        byte[] lineSeparatorByteArray = System.lineSeparator().getBytes(StandardCharsets.UTF_8);
                        FileChannel newPositionChannel = openFileChannel.position(openFileChannel.size());
                        newPositionChannel.write(ByteBuffer.wrap(lineStrByteArray));
                        newPositionChannel.write(ByteBuffer.wrap(lineSeparatorByteArray));
                    } catch (Exception e) {
                        throw new RuntimeException("write upload part meta failed:" + e);
                    }
                });
    }

    private void argNotNull(boolean isNull, String message) {
        if (isNull) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 分片未合并前存放的临时文件夹路径
     */
    private String getTempPartFolderPathStr(String tenantKey, String fileId) {
        return this.getFilePath(tenantKey, fileId) + "-folder";
    }

    /**
     * 获取存放每次分片上传事件信息的文件夹路径
     */
    private String getUploadPartEventPathStr() {
        return this.rootPath + File.separator + "MULTIPART_EVENT";
    }

    private String getUploadPartEventFilePathStr(String uploadId) {
        return this.rootPath + File.separator + "MULTIPART_EVENT" + File.separator + uploadId;
    }
}
