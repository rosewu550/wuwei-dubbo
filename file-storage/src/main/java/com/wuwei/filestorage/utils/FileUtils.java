package com.wuwei.filestorage.utils;



import com.wuwei.filestorage.config.FileProperties;
import com.wuwei.filestorage.constant.FileConstant;
import java.util.Optional;

public class FileUtils {

    /**
     * 获取上传地址
     */
    public static String getUploadUrl(){
        FileProperties fileProperties = SpringContextUtil.getBean(FileProperties.class);
        String host = Optional.of(fileProperties)
                .map(FileProperties::getHost)
                .orElse("");
        return host + FileConstant.UPLOAD_ENDPOINT;
    }
}
