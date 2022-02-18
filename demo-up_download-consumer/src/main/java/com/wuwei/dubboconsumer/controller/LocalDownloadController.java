package com.wuwei.dubboconsumer.controller;


import com.wuwei.ffmpeg.constant.FfmpegConstant;
import com.wuwei.ffmpeg.stream.FileStreamConversion;
import com.wuwei.ffmpeg.util.FfmpegUtil;
import com.wuwei.filestorage.common.ResponseHeaderSettings;
import com.wuwei.filestorage.strategy.StorageStrategy;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class LocalDownloadController {
    private final Logger logger = LoggerFactory.getLogger(LocalDownloadController.class);

    @Autowired
    private StorageStrategy storageStrategy;


    @GetMapping("/local/download")
    public void downloadFile(@RequestParam("fileId") String fileId,
                             @RequestParam("tenantKey") String tenantKey,
                             HttpServletResponse response) {
        InputStream inputStream = null;
        try (ServletOutputStream servletOutputStream = response.getOutputStream()) {
            // 得到JVM中的空闲内存量（单位是m）
            System.out.println("空闲内存："+Runtime.getRuntime().freeMemory()/1024/1024);
            // 的JVM内存总量（单位是m）
            System.out.println("jvm内存总量："+Runtime.getRuntime().totalMemory()/1024/1024);
            inputStream = storageStrategy.getFile(fileId, tenantKey);
            // 得到JVM中的空闲内存量（单位是m）
            System.out.println("空闲内存："+Runtime.getRuntime().freeMemory()/1024/1024);
            // 的JVM内存总量（单位是m）
            System.out.println("jvm内存总量："+Runtime.getRuntime().totalMemory()/1024/1024);
            byte[] bytes = new byte[4096];
            int length;
            while ((length = inputStream.read(bytes)) > 0) {
                servletOutputStream.write(bytes, 0, length);
            }
            logger.info("成功！");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("失败，信息：{}", e.getMessage());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }



    /**
     * 本地存储流下载
     */
    @GetMapping(value = "/local/download/{url}")
    public void downloadLocalFile(@PathVariable(value = "url", required = true) String url,
                                  @RequestParam("tenantKey") String tenantKey,
                                  @RequestParam(value = ResponseHeaderSettings.RESPONSE_HEADER_CONTENT_DISPOSITION, required = false) String contentDisposition,
                                  @RequestParam(value = ResponseHeaderSettings.RESPONSE_HEADER_EXPIRES, required = false) String expiresTime,
                                  @RequestParam(value = ResponseHeaderSettings.RESPONSE_HEADER_CONTENT_TYPE, required = false) String contentType,
                                  @RequestParam(value = ResponseHeaderSettings.RESPONSE_HEADER_CONTENT_LANGUAGE, required = false) String contentLanguage,
                                  @RequestParam(value = ResponseHeaderSettings.RESPONSE_HEADER_CONTENT_ENCODING, required = false) String contentEncoding,
                                  HttpServletResponse response, HttpServletRequest request) {
        logger.info("----------》 本地下载接口请求参数url:{},tenantKey:{}", url, tenantKey);

        if (StringUtils.isNotBlank(contentDisposition)) {
            try {
                response.setHeader("Content-Disposition", URLDecoder.decode(contentDisposition, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return;
            }
        }
    }

}
