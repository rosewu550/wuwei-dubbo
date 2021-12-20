package com.wuwei.filestorage.strategy;


import java.io.File;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;


/**
 * 文件系统策略
 * 
 * @author Ricky
 */
public interface StorageStrategy {

     String putFile(File file, String tenantKey) throws Exception;

     String putFile(File file, String tenantKey, String url) throws Exception;

     String putFile(byte[] bytes, String tenantKey, String url) throws Exception;

     String putFile(byte[] bytes, String tenantKey) throws Exception;

     InputStream getFile(String fileId, String tenantKey) throws Exception;


     int deleteFile(String fileId, String tenantKey) throws Exception;

    default int deleteFile(String fileId,String tenantKey,String bucketName) throws Exception{
        return 0;
    }

    /**
     * 初始化一个断点续传队列
     * @param key
     * @param fileName
     * @return
     */
     Map<String, String> initiateMultipartUpload(String key, String fileName);
    
    /**
     * 上传分片
     * @param tenantKey
     * @param fileId
     * @param uploadId
     * @param partNumber
     * @param partSize
     * @param input
     * @return UploadPartResult
     */
     Map<String, String> uploadPart(String tenantKey, String fileId, String uploadId, int partNumber, long partSize,
    		//String md5Digest, 
    		InputStream input);
    

    /**
     * 合并生成文件
     * @param tenantKey
     * @param fileId
     * @param uploadId
     * @return CompleteMultipartUploadResult
     */
     Map<String, String> completeMultipartUpload(String tenantKey, String fileId, String uploadId);
    
    /**
     * 终止分片上传
     * @param tenantKey
     * @param fileId
     * @param uploadId
     */
     void abortMultipartUpload(String tenantKey, String fileId, String uploadId);

    /**
     * 罗列出已经上传成功的片
     * @param tenantKey
     * @param fileId
     * @param uploadId
     * @return
     */
     String listParts(String tenantKey, String fileId, String uploadId);

    /**
     * 获取下载地址
     * @param tenantKey
     * @param fileId
     * @param expiration
     * @param fileName
     * @return
     */
    String generatePresignedUrl(String tenantKey, String fileId, Date expiration, String fileName);


    /**
     * 获取下载地址，可以指定桶
     * @param tenantKey 租户id
     * @param fileId 存储对应的key
     * @param expiration 过期时间
     * @param bucketName 桶名
     * @return 下载链接
     */
    default String generatePresignedUrl(String tenantKey, String fileId, String bucketName,String fileName,Date expiration){
        return null;
    }

    /**
     * 获取本地上传的地址
     * @param fileId
     * @param tenantKey
     * @return
     */
     default String getDirectory(String fileId, String tenantKey){
         return null;
     }

    /**
     * 复制文件对象
     * @param sourceFileKey 源文件的对象键
     * @param fileTenantKey 源文件租户id
     * @return 目标文件的对象键
     */
    default String copyFile(String sourceFileKey,String fileTenantKey,String destinationTenantKey){
        return null;
    }


    /**
     * 判断云上对象是否存在
     * @param bucketName 桶名
     * @param fileId 键对象
     * @return true 存在，false 不存在
     */
    default boolean metadataExist(String tenantKey, String fileId, String bucketName){
        return false;
    }
}