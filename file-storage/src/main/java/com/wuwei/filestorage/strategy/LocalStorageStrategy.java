package com.wuwei.filestorage.strategy;


import java.io.File;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;


/**
 * 本地存储策略
 *
 * @author wuwei
 * @since 2021/12/15
 */
public interface LocalStorageStrategy extends StorageStrategy {
    @Override
    String putFile(File file, String tenantKey) throws Exception;

    @Override
    String putFile(File file, String tenantKey, String url) throws Exception;

    @Override
    String putFile(byte[] bytes, String tenantKey, String url) throws Exception;

    @Override
    String putFile(byte[] bytes, String tenantKey) throws Exception;

    @Override
    InputStream getFile(String fileId, String tenantKey) throws Exception;


    @Override
    int deleteFile(String fileId, String tenantKey) throws Exception;

    @Override
    default int deleteFile(String fileId, String tenantKey, String bucketName) throws Exception {
        return this.deleteFile(fileId, tenantKey);
    }

    @Override
    Map<String, String> initiateMultipartUpload(String tenantKey, String fileName);

    @Override
    Map<String, String> uploadPart(String tenantKey, String fileId, String uploadId, int partNumber, long partSize, InputStream input);

    @Override
    Map<String, String> completeMultipartUpload(String tenantKey, String fileId, String uploadId);

    @Override
    void abortMultipartUpload(String tenantKey, String fileId, String uploadId);

    @Override
    String listParts(String tenantKey, String fileId, String uploadId);

    /**
     * （本地存储暂且不实现）
     */
    @Override
    default String generatePresignedUrl(String tenantKey, String fileId, Date expiration, String fileName) {
        return null;
    }



    /**
     * （本地存储暂且不实现）
     */
    @Override
    default String generatePresignedUrl(String tenantKey, String fileId, String bucketName, String fileName, Date expiration) {
        return StorageStrategy.super.generatePresignedUrl(tenantKey, fileId, bucketName, fileName, expiration);
    }

    @Override
    default String getDirectory(String fileId, String tenantKey) {
        return null;
    }

    @Override
    default String copyFile(String sourceFileKey, String fileTenantKey, String destinationTenantKey) {
        return null;
    }

    @Override
    default boolean metadataExist(String tenantKey, String fileId, String bucketName) {
        return false;
    }
}
