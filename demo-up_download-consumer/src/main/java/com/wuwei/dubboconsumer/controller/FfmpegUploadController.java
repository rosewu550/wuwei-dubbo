package com.wuwei.dubboconsumer.controller;

import com.wuwei.ffmpeg.stream.FileStreamConversion;
import com.wuwei.ffmpeg.util.FfmpegUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@RestController
public class FfmpegUploadController {

    private final Logger logger = LoggerFactory.getLogger(LocalDownloadController.class);


    @Autowired
    private FileStreamConversion fileStreamConversion;

    @GetMapping("/video/convert/download")
    public void downloadVideoFile(HttpServletResponse response) {

        try {
            String fileUrl = "/Volumes/storage/wuwei/video/transcode/AGPMInjector.mp4";
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.setHeader("Content-Disposition", "attachment;filename=\"" + "convert.mp4" + "\"");

            fileStreamConversion.videoToMp4ByFfmpeg("mov", fileUrl, response.getOutputStream());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @GetMapping("/video/convertByChannel/download")
    public void downloadVideoFileByChannel(HttpServletResponse response) {

        try {
            String fileUrl = "/Volumes/storage/wuwei/aliyun/old.MP4";
//            String fileUrl = "/Volumes/other/下载/video/new/video1.flv";
//            String fileUrl = "/Users/wuwei/Downloads/屏幕录制2022-01-12 下午3.38.51(2).mp4";
//            String fileUrl = "/Volumes/storage/wuwei/video/transcode/video1.mov";
//            String fileUrl = "/Volumes/other/下载/88bf6ddaad68ed274cb6047d9abb82e3.avi";
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.setHeader("Content-Disposition", "attachment;filename=\"" + "convert.mp4" + "\"");

            Path path = Paths.get(fileUrl);
            InputStream inputStream = Files.newInputStream(path);
            fileStreamConversion.videoToMp4ByChannel("wuwei.mp4", "mp4", inputStream, response.getOutputStream());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    //    getVideoMessage
    @GetMapping("/video/message")
    public void getVideoMessage() {

        try {
            String fileUrl1 = "/Volumes/storage/wuwei/aliyun/old.MP4";
            String fileUrl2 = "/Volumes/storage/wuwei/aliyun/new.MP4";

            Path path = Paths.get(fileUrl1);
            InputStream inputStream = Files.newInputStream(path);
            FfmpegUtils.getVideoMessage(inputStream);

            Path path2 = Paths.get(fileUrl2);
            InputStream inputStream2 = Files.newInputStream(path2);
            FfmpegUtils.getVideoMessage(inputStream2);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
