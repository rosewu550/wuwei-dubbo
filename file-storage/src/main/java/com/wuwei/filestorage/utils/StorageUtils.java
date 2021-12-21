package com.wuwei.filestorage.utils;

import org.springframework.util.DigestUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
            throw new RuntimeException("calculate inputStream md5 failed" + e);
        } finally {
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String calculateMD5(String pathStr) {
        Path path = Paths.get(pathStr);
        try (InputStream inputStream = Files.newInputStream(path)) {
            return calculateMD5(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("calculate inputStream md5 failed", e);
        }
    }

}
