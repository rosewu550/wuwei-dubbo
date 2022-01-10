package com.wuwei.filestorage.utils;

import org.apache.commons.io.IOUtils;
import org.springframework.util.DigestUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class StorageUtils {
    private StorageUtils() {
    }

    /**
     * 计算文件md5值
     */
    public static String calculateMD5(byte[] byteArray) {
        // 计算文件md5值
        return DigestUtils.md5DigestAsHex(byteArray);
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

    /**
     * 复制inputStream
     */
    public static byte[] temporaryCopyInputStream(InputStream inputStream) {
        try {
            return IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("copy inputStream to byte[] failed", e);
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

}
