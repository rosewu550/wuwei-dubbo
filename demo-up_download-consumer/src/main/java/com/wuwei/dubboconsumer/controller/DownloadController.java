package com.wuwei.dubboconsumer.controller;


import com.wuwei.dubboApi.service.DownloadDemoService;
import com.wuwei.dubboconsumer.utils.WebClientUtils;
import com.wuwei.filestorage.service.download.WebClientDownload;
import com.wuwei.watermark.entity.WatermarkContentParam;
import com.wuwei.watermark.watermarkstream.WatermarkStream;
import com.wuwei.watermark.watermarkstream.WatermarkStreamManager;
import org.apache.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.DefaultUriBuilderFactory;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class DownloadController {
    private static final Logger logger = LoggerFactory.getLogger(DownloadController.class);

    @Reference(group = "hessian", timeout = 300000)
    private DownloadDemoService downloadDemoService;

    @Reference(group = "dubbo")
    private DownloadDemoService dubboDownloadService;

    @Autowired
    private WatermarkStreamManager watermarkStreamManager;

    private static final ParameterizedTypeReference<InputStreamResource> typeReference =
            new ParameterizedTypeReference<InputStreamResource>() {
            };

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


    @GetMapping("/webclient/test")
    public void webClientDownloadTest() {
        String url = "https://release-1301503941.cos.ap-shanghai.myqcloud.com/tam3vutq78/39b1a23f-af00-4d74-b1fa-750c03507a42?sign=q-sign-algorithm%3Dsha1%26q-ak%3DAKIDqcLvPmX4XO8iMjIHoSG3CDXiiXkn8EJD%26q-sign-time%3D1647448689%3B1647452289%26q-key-time%3D1647448689%3B1647452289%26q-header-list%3Dhost%26q-url-param-list%3Dresponse-content-disposition%26q-signature%3Dd11d56593320b822b26c4150a4c2cc1c328b58a5&response-content-disposition=attachment%3Bfilename%3D%220409c9202de36d64a9c8e6a549b3f45e.jpeg%22";
        try {
            download(url);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void download(String downloadUrl) throws UnsupportedEncodingException {
        WebClientUtils.getWebClient("https://release-1301503941.cos.ap-shanghai.myqcloud.com/tam3vutq78/39b1a23f-af00-4d74-b1fa-750c03507a42", "sign=q-sign-algorithm%3Dsha1%26q-ak%3DAKIDqcLvPmX4XO8iMjIHoSG3CDXiiXkn8EJD%26q-sign-time%3D1647448689%3B1647452289%26q-key-time%3D1647448689%3B1647452289%26q-header-list%3Dhost%26q-url-param-list%3Dresponse-content-disposition%26q-signature%3Dd11d56593320b822b26c4150a4c2cc1c328b58a5&response-content-disposition=attachment%3Bfilename%3D%220409c9202de36d64a9c8e6a549b3f45e.jpeg%22")
                .get()
                .retrieve()
                .bodyToMono(byte[].class)
                .doOnError(error -> {
                    if (error instanceof WebClientResponseException.InternalServerError) {
                        String responseBodyAsString = ((WebClientResponseException.InternalServerError) error).getResponseBodyAsString();
                        logger.error(">>>>>>download failed ->{}", responseBodyAsString);
                    } else {
                        logger.error(">>>>>>download failed ->", error);
                    }
                })
                .doFinally(signalType -> {
                    String name = signalType.name();
                })
                .checkpoint()
                .block();
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
            WatermarkStream textStream = watermarkStreamManager.getWatermark(watermarkContentParam.getType());
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
