package com.wuwei.filestorage.strategy;

/**
 * 文件系统策略
 * 
 * @author Ricky
 */
public interface TeamsStorageStrategy extends StorageStrategy{

    byte[] getFileByte(String fileId, String tenantKey) throws Exception ;

}