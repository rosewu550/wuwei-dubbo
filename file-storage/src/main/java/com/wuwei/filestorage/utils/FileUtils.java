package com.wuwei.filestorage.utils;


import com.wuwei.filestorage.config.FileProperties;
import com.wuwei.filestorage.constant.FileConstant;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
public class FileUtils {

    private static FileProperties fileProperties;

    public FileUtils(FileProperties tempFileProperties) {
        fileProperties = tempFileProperties;
    }

    /**
     * 获取host
     */
    public static String getHost() {
        return Optional.of(fileProperties)
                .map(FileProperties::getHost)
                .orElse("");
//        return "http://10.12.102.31:8138";
    }

    /**
     * 获取上传地址
     */
    public static String getUploadUrl() {
        return FileConstant.UPLOAD_ENDPOINT;
    }

    /**
     * 获取上传地址
     */
    public static String getChunkUpCheckUrl() {
        return FileConstant.CHUNK_UPLOAD_CHECK;
    }

    /**
     * 获取下载地址
     */
    public static String getDownloadUrl(Long fileId) {
        Long tempFileId = Optional.ofNullable(fileId).orElse(0L);
        return FileConstant.DOWNLOAD_ENDPOINT + tempFileId + "/auth/true";
    }

}
