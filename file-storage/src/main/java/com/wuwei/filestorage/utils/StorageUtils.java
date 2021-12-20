package com.wuwei.filestorage.utils;

import org.springframework.util.DigestUtils;

import java.io.IOException;
import java.io.InputStream;

public class StorageUtils {
    private StorageUtils() {
    }

    /**
     * 计算文件md5值
     */
    public static String calculateMD5(InputStream inputStream) {
        try {
            // 计算文件md5值
            return DigestUtils.md5DigestAsHex(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
