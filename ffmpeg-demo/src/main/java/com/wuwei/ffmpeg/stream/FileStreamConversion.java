package com.wuwei.ffmpeg.stream;

import com.wuwei.ffmpeg.dto.MediaObjectInfo;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author ：liyongfeng
 * @version :
 * @date ：Created in 2021/7/30 15:17
 * @description : 文件流转换，并输出流
 */
public interface FileStreamConversion {


    /**
     * 将视频文件转换为 mp4
     *
     * @param videoFormat  视频文件后缀
     * @param inputStream  待转码视频文件流
     * @param outputStream 转码后文件流
     */
    void videoToMp4ByFfmpeg(String videoFormat, InputStream inputStream, OutputStream outputStream);

    /**
     * 将视频文件转换为 mp4
     *
     * @param videoFormat  视频文件后缀
     * @param fileUrl      文件链接(可以是本地文件路径，也可以是完整的http url)
     * @param outputStream 转换后的输出流
     */
    void videoToMp4ByFfmpeg(String videoFormat, String fileUrl, OutputStream outputStream);

    /**
     * 使用channel视频转码为mp4
     *
     */
    void videoToMp4ByChannel(String outputFileName, String videoFormat, InputStream inputStream, OutputStream outputStream);

    /**
     * 将文件流写入目标临时文件地址
     *
     * @param inputStream   源文件流
     * @param temporaryPath 临时文件路径
     */
    MediaObjectInfo writeTemporaryFile(InputStream inputStream, String temporaryPath);

    /**
     * 删除路径下的临时文件
     *
     * @param path 文件地址
     */
    void deleteTemporaryFile(String path);

}
