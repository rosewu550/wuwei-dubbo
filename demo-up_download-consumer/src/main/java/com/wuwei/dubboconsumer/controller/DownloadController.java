package com.wuwei.dubboconsumer.controller;


import com.wuwei.dubboApi.service.DownloadDemoService;
import com.wuwei.filestorage.service.download.WebClientDownload;
import com.wuwei.watermark.entity.WatermarkContentParam;
import com.wuwei.watermark.watermarkstream.WatermarkStream;
import com.wuwei.watermark.watermarkstream.WatermarkStreamManager;
import org.apache.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class DownloadController {
    private final Logger logger = LoggerFactory.getLogger(DownloadController.class);

    @Reference(group = "hessian", timeout = 300000)
    private DownloadDemoService downloadDemoService;

    @Reference(group = "dubbo")
    private DownloadDemoService dubboDownloadService;

    @Autowired
    private WatermarkStreamManager watermarkStreamManager;

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
    public void hessianDownFileByte(@RequestParam("path") String path, HttpServletResponse response) {
        try (ServletOutputStream servletOutputStream = response.getOutputStream()) {
            byte[] bytes = downloadDemoService.downloadDocumentByte(path);
            servletOutputStream.write(bytes, 0, bytes.length - 1);
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
    public void duboDownFileByte(@RequestParam("path") String path, HttpServletResponse response) {
        try (ServletOutputStream servletOutputStream = response.getOutputStream()) {
            byte[] bytes = dubboDownloadService.downloadDocumentByte(path);
            servletOutputStream.write(bytes, 0, bytes.length - 1);
            logger.info("成功！");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("失败，信息：{}", e.getMessage());
        }
    }

    @GetMapping("/webClient/download")
    public void webClientDownload() {
        WebClientDownload webClientDownload = new WebClientDownload(7834310948597176316L);
        webClientDownload.init("ae8122ace787a4d8b8b82b1fa578be67", "document");
        InputStream inputStream = webClientDownload.blockDownload();
        System.out.println(inputStream);
    }

    @GetMapping("/watermark/download")
    public void getWatermark(@RequestBody WatermarkContentParam watermarkContentParam, HttpServletResponse response) {
        Path path = Paths.get("/Volumes/other/下载/picture/d7ae9905003e5b33c8d4bbd382f677dd.jpeg");
        try (InputStream inputStream = Files.newInputStream(path);
             ServletOutputStream servletOutputStream = response.getOutputStream()) {
            WatermarkStream textStream = watermarkStreamManager.getWatermark("text");
            InputStream watermarkInputStream = textStream.watermarkImage(inputStream, watermarkContentParam);
            byte[] bytes = new byte[4096];
            int length;
            while ((length = watermarkInputStream.read(bytes)) > 0) {
                servletOutputStream.write(bytes, 0, length);
            }
        } catch (Exception e) {
            logger.error("", e);
        }


//        System.out.println(inputStream);
    }


//    @GetMapping("/http/downloadInputStream")
//    public void testHttpDownload(@RequestParam("fileId") Long fileId, @RequestParam("eteamsId") String eteamsId, @RequestParam("module") String module) {
//        WebClientDownload webClientDownload = new WebClientDownload(fileId);
//        InputStream download = webClientDownload.init(eteamsId, module)
//                .download();
//        try {
//            int available = download.available();
//            System.out.println(available);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

}
