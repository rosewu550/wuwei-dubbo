package com.wuwei.dubboconsumer.controller;

import com.wuwei.ffmpeg.stream.FileStreamConversion;
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
            String fileUrl = "/Volumes/storage/wuwei/video/transcode/video1.mov";
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
            String fileUrl = "/Volumes/storage/wuwei/video/transcode/video1.mov";
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.setHeader("Content-Disposition", "attachment;filename=\"" + "convert.mp4" + "\"");

            Path path = Paths.get(fileUrl);
            InputStream inputStream = Files.newInputStream(path);
            fileStreamConversion.videoToMp4ByChannel("wuwei.mp4", "mov", inputStream, response.getOutputStream());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
