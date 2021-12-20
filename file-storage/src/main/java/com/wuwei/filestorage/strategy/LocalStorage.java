package com.wuwei.filestorage.strategy;

import com.alibaba.fastjson.JSON;
import com.wuwei.filestorage.local.LocalStorageClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 本地存储实现
 *
 * @author wuwei
 * @since 2021/12/15
 */
@Service
public class LocalStorage implements LocalStorageStrategy {

    @Autowired
    private LocalStorageClient localStorageClient;

    private LocalStorage() {
    }

    public LocalStorage(LocalStorageClient localStorageClient) {
        this.localStorageClient = localStorageClient;
    }

    @Override
    public String putFile(File file, String tenantKey) throws Exception {
        return localStorageClient.putFile(new FileSystemResource(file), tenantKey);
    }

    @Override
    public String putFile(File file, String tenantKey, String url) throws Exception {
        localStorageClient.putFile(new FileSystemResource(file), tenantKey, url);
        return url;
    }

    @Override
    public String putFile(byte[] bytes, String tenantKey, String url) throws Exception {
        localStorageClient.putFile(new ByteArrayResource(bytes), tenantKey, url);
        return url;
    }

    @Override
    public String putFile(byte[] bytes, String tenantKey) throws Exception {
        return localStorageClient.putFile(new ByteArrayResource(bytes), tenantKey);
    }

    @Override
    public InputStream getFile(String fileId, String tenantKey) throws Exception {
        return localStorageClient.downloadFile(fileId, tenantKey);
    }

    @Override
    public int deleteFile(String fileId, String tenantKey) throws Exception {
        String filePathStr = localStorageClient.getFilePath(fileId, tenantKey);
        Path filePath = Paths.get(filePathStr);
        Files.delete(filePath);
        return 0;
    }

    @Override
    public String getDirectory(String fileId, String tenantKey) {
        return localStorageClient.getFolderPath(tenantKey);
    }

    @Override
    public boolean metadataExist(String tenantKey, String fileId, String bucketName) {
        String filePathStr = localStorageClient.getFilePath(tenantKey, fileId);
        Path filePath = Paths.get(filePathStr);
        return Files.exists(filePath, LinkOption.NOFOLLOW_LINKS);
    }

    @Override
    public String copyFile(String sourceFileKey, String fileTenantKey, String destinationTenantKey) {
        return localStorageClient.copyFile(fileTenantKey, sourceFileKey, destinationTenantKey);
    }

    /**
     * 初始化分片上传
     */
    @Override
    public Map<String, String> initiateMultipartUpload(String tenantKey, String fileName) {
        String fileId = UUID.randomUUID().toString().replace("-", "");
        String uploadId = localStorageClient.initMultipartUpload(tenantKey, fileId);
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("fileId", fileId);
        resultMap.put("uploadId", uploadId);
        resultMap.put("tenantKey", tenantKey);
        return resultMap;
    }

    @Override
    public Map<String, String> uploadPart(String tenantKey, String fileId, String uploadId, int partNumber, long partSize, InputStream input) {
        localStorageClient.uploadPart(tenantKey, fileId, uploadId, partNumber, partSize, input);
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("UploadPartResult", "success");
        return resultMap;
    }

    @Override
    public Map<String, String> completeMultipartUpload(String tenantKey, String fileId, String uploadId) {
        localStorageClient.mergePart(tenantKey, fileId, uploadId);
        Map<String, String> map = new HashMap<>();
        map.put("CompleteMultipartUploadResult", "success");
        return map;
    }

    @Override
    public void abortMultipartUpload(String tenantKey, String fileId, String uploadId) {
        localStorageClient.abortMultiPartUpload(tenantKey, fileId, uploadId);
    }

    @Override
    public String listParts(String tenantKey, String fileId, String uploadId) {
        Map<String, Map<String, String>> partsMap = localStorageClient.listParts(tenantKey, fileId, uploadId);
        return JSON.toJSONString(partsMap);
    }
}
