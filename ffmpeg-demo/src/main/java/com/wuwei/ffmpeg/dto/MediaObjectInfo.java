package com.wuwei.ffmpeg.dto;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * @author ：liyongfeng
 * @version :
 * @date ：Created in 2021/8/3 19:11
 * @description : 音视频文件结果封装
 */
public class MediaObjectInfo {

    private final Logger logger = LoggerFactory.getLogger(MediaObjectInfo.class);

    /**
     * 文件临时地址
     */
    private String path;

    public MediaObjectInfo(String path) {
        this.path = path;
    }

    /**
     * 获取转码后的文件对象
     *
     * @return File
     */
    public File getMediaFile() {
        if (StringUtils.isBlank(path)) {
            throw new IllegalArgumentException("文件地址不存在");
        }
        return new File(path);
    }

    /**
     * 获取文件流
     *
     * @return InputStream
     */
    public InputStream getMediaInputStream() {
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(getMediaFile());
        } catch (Exception e) {
            logger.error("获取文件流失败：path={}", path);
            throw new IllegalArgumentException("文件处理失败，请重试");
        }
        return inputStream;
    }

    /**
     * 删除路径上的临时文件
     */
    public void deleteFile() {
        File tif = new File(path);
        if (tif.exists()) {
            boolean de = tif.delete();
            if (!de) {
                logger.error("删除临时文件失败，再次进行重试");
                boolean de2 = tif.delete();
                if (!de2) {
                    logger.error("删除临时文件失败!,暂不进行处理，sourcePath={}", path);
                }
            }
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "MediaObjectInfo{" +
                "path='" + path + '\'' +
                '}';
    }
}
