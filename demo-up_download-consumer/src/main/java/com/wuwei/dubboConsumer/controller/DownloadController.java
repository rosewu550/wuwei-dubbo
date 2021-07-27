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

    @Reference( group = "hessian",timeout = 300000)
    private DownloadDemoService downloadDemoService;

    @Reference(group = "dubbo")
    private DownloadDemoService dubboDownloadService;

    @GetMapping("/downloadInputStream")
    public void downloadFile(@RequestParam("path") String path, HttpServletResponse response) {
        try (ServletOutputStream servletOutputStream = response.getOutputStream()) {
            InputStream inputStream = downloadDemoService.downloadDocument(path);
            int read = inputStream.read();// 阻塞方法，等待流全部返回
            logger.info(String.valueOf(read));
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

    @GetMapping("/downloadByte")
    public void hessianDownFileByte(@RequestParam("path") String path,HttpServletResponse response){
        try (ServletOutputStream servletOutputStream = response.getOutputStream()) {
            byte[] bytes = downloadDemoService.downloadDocumentByte(path);
            servletOutputStream.write(bytes,0,bytes.length - 1);
            logger.info("成功！");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("失败，信息：{}", e.getMessage());
        }

    }

    @GetMapping("/dubbo/downloadInputStream")
    public void downloadFileByDubbo(@RequestParam("path") String path, HttpServletResponse response) {
        try (ServletOutputStream servletOutputStream = response.getOutputStream()) {
            InputStream inputStream = dubboDownloadService.downloadDocument(path);
            logger.info(String.valueOf(inputStream.available()));
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


    @GetMapping("/dubbo/downloadByte")
    public void duboDownFileByte(@RequestParam("path") String path,HttpServletResponse response){
        try (ServletOutputStream servletOutputStream = response.getOutputStream()) {
            byte[] bytes = dubboDownloadService.downloadDocumentByte(path);
            servletOutputStream.write(bytes,0,bytes.length - 1);
            logger.info("成功！");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("失败，信息：{}", e.getMessage());
        }

    }

}
