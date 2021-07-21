package com.wuwei.dubboConsumer.controller;


import com.wuwei.dubboApi.service.DownloadDemoService;
import org.apache.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

@RestController
public class DownloadController {
    private final Logger logger = LoggerFactory.getLogger(DownloadController.class);

    @Reference(protocol = "hessian", group = "hessian",timeout = 300000)
    private DownloadDemoService downloadDemoService;

    @Reference(protocol = "hessian", group = "dubbo")
    private DownloadDemoService dubboDownloadService;

    @GetMapping("/downloadInputStream")
    public void downloadFile(@RequestParam("path") String path, HttpServletResponse response) {
        try (ServletOutputStream servletOutputStream = response.getOutputStream()) {
            InputStream inputStream = downloadDemoService.downloadDocument(path);
            byte[] bytes = new byte[4096];
            int length;
            while ((length = inputStream.read(bytes)) > 0) {
                servletOutputStream.write(bytes, 0, length);
            }
            logger.info("成功！");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("失败，信息：{}", e.getMessage());
        }
    }

    @GetMapping("/dubbo/downloadInputStream")
    public void downloadFileByDubbo(@RequestParam("path") String path, HttpServletResponse response) {
        try (ServletOutputStream servletOutputStream = response.getOutputStream()) {
            InputStream inputStream = downloadDemoService.downloadDocument(path);
            byte[] bytes = new byte[4096];
            int length;
            while ((length = inputStream.read(bytes)) > 0) {
                servletOutputStream.write(bytes, 0, length);
            }
            logger.info("成功！");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("失败，信息：{}", e.getMessage());
        }
    }

}
