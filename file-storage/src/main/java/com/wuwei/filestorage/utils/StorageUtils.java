package com.wuwei.filestorage.utils;

import org.springframework.util.DigestUtils;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

public class StorageUtils {
    private StorageUtils() {
    }

    /**
     * 计算文件md5值
     */
    public static String calculateMD5(InputStream inputStream) {
        try {
            MessageDigest md5Digest = MessageDigest.getInstance("md5");
            int len;
            byte[] buffer = new byte[1024 * 4];
            while ((len = inputStream.read(buffer)) != -1) {
                // 更新散列值
                md5Digest.update(buffer, 0, len);
            }
            // 散列值数组
            byte[] digest = md5Digest.digest();
            // 1表明这是无符号整数
            BigInteger bigInteger = new BigInteger(1, digest);
            // 以16进制的形式输出
            return bigInteger.toString(16);
        } catch (Exception e) {
            throw new RuntimeException("caculate md5 failed" + e);
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
