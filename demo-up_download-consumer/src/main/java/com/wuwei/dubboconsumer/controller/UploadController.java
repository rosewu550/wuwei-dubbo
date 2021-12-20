package com.wuwei.dubboconsumer.controller;


import com.google.common.collect.Maps;
import com.wuwei.dubboApi.entity.Document;
import com.wuwei.dubboApi.service.DownloadDemoService;
import com.wuwei.dubboApi.service.UploadDemoService;
import com.wuwei.filestorage.entity.ResultDto;
import com.wuwei.filestorage.service.upload.WebClientChunkUpload;
import com.wuwei.filestorage.service.upload.WebClientUpload;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.wuwei.filestorage.service.upload.RestTemplateUpload;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@RestController
public class UploadController {

    @Reference(protocol = "hessian", group = "hessian")
    private UploadDemoService uploadDemoService;

    @Reference(protocol = "dubbo", group = "dubbo")
    private UploadDemoService dubboUploadDemoService;

    @Reference
    private DownloadDemoService downloadDemoService;

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        String msg;
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        try {
            if (fileName.contains("..")) {
                throw new Exception("文件名称无效：" + fileName);
            }
            Document document = new Document();
            document.setName(fileName);
            InputStream inputStream = file.getInputStream();
            uploadDemoService.uploadDocument(document, inputStream);
            msg = "上传成功！";
        } catch (Exception e) {
            e.printStackTrace();
            msg = "上传失败！";
        }
        return msg;
    }

    @PostMapping("/uploadByOrigin")
    public String uploadFileByOrigin(@RequestParam("file") MultipartFile file) {

        String msg;
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        try {
            if (fileName.contains("..")) {
                throw new Exception("文件名称无效：" + fileName);
            }
            uploadDemoService.uploadDocument(fileName, file);
            msg = "uploadByOrigin上传成功！";
        } catch (Exception e) {
            e.printStackTrace();
            msg = "uploadByOrigin上传失败！";
        }
        return msg;
    }


    @PostMapping("/uploadByFiles")
    public String uploadFileByFile(@RequestParam("file") File file) {
        String msg;
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getName()));
        try {
            if (fileName.contains("..")) {
                throw new Exception("文件名称无效：" + fileName);
            }
            uploadDemoService.uploadDocument(fileName, file);
            msg = "uploadByFiles上传成功！";
        } catch (Exception e) {
            e.printStackTrace();
            msg = "uploadByFiles上传失败！";
        }
        return msg;
    }

    @PostMapping("/uploadByBytes")
    public String uploadFileByBytes(@RequestParam("file") MultipartFile file) {
        String msg;
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        try {
            if (fileName.contains("..")) {
                throw new Exception("文件名称无效：" + fileName);
            }
            uploadDemoService.uploadDocumentByBytes(fileName, file.getBytes());
            msg = "uploadByBytes上传成功！";
        } catch (Exception e) {
            e.printStackTrace();
            msg = "uploadByBytes上传失败！";
        }
        return msg;
    }

    @PostMapping("/dubbo/uploadByBytes")
    public String uploadFileByBytesDubbo(@RequestParam("file") MultipartFile file) {
        String msg;
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        try {
            if (fileName.contains("..")) {
                throw new Exception("文件名称无效：" + fileName);
            }
            dubboUploadDemoService.uploadDocumentByBytes(fileName, file.getBytes());
            msg = "uploadByBytes上传成功！";
        } catch (Exception e) {
            e.printStackTrace();
            msg = "uploadByBytes上传失败！";
        }
        return msg;
    }

    @PostMapping("/dubbo/uploadByInputStream")
    public String uploadFileByInputStreamDubbo(@RequestParam("file") MultipartFile file) {
        String msg;
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        try {
            if (fileName.contains("..")) {
                throw new Exception("文件名称无效：" + fileName);
            }
            dubboUploadDemoService.uploadDocumentByInputStream(fileName, file.getInputStream());
            msg = "uploadByInputStream上传成功！";
        } catch (Exception e) {
            e.printStackTrace();
            msg = "uploadByInputStream上传失败！";
        }
        return msg;
    }

    @PostMapping("/uploadByInputStream")
    public String uploadByInputStream(@RequestParam("file") MultipartFile file) {
        String msg;
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        try {
            if (fileName.contains("..")) {
                throw new Exception("文件名称无效：" + fileName);
            }
            uploadDemoService.uploadDocumentByInputStream(fileName, file.getInputStream());
            msg = "uploadByInputStream上传成功！";
        } catch (Exception e) {
            e.printStackTrace();
            msg = "uploadByInputStream上传失败！";
        }
        return msg;
    }

    @PostMapping("/test")
    public void test(@RequestParam("file") MultipartFile file) {
        file.getContentType();

        ResultDto upload2 = new RestTemplateUpload(file)
                .init("b76c79d488e6ee77c56b7b44e6a54091", "wuwei", "im", 2131, "123123", "")
                .upload2();

        if (null != upload2 && upload2.isStatus()) {
            System.out.println(upload2.getData());
        }

        ResultDto upload = new RestTemplateUpload(file)
                .init("b76c79d488e6ee77c56b7b44e6a54091", "wuwei", "im", 2131, "123123", "")
                .upload();
        if (null != upload && upload.isStatus()) {
            System.out.println(upload.getData());
        }


        System.out.println(upload);
    }

    @PostMapping("/testUpload")
    @ResponseBody
    public Map testUpload(@RequestParam("file") MultipartFile file) {
        Map<String, Object> map = Maps.newHashMapWithExpectedSize(2);

        long startTime = System.currentTimeMillis();
        ResultDto resultDto = null;
        WebClientUpload webClientUpload = new WebClientUpload(file);
        resultDto = webClientUpload.init("8c7f716f4dbf8cb0fded9d560669b11b",
                        file.getOriginalFilename(), "document", file.getSize(), new Date().getTime() + "", "")
                .blockUpload();
        long endTime = System.currentTimeMillis();
        map.put("code", resultDto.getCode());
        map.put("time", endTime - startTime);
        map.put("message", resultDto.getMessage());

        return map;
    }

    @PostMapping("/testChunkUpload")
    @ResponseBody
    public Map testChunkUpload(@RequestParam("file") MultipartFile file) {
        Map<String, Object> map = Maps.newHashMapWithExpectedSize(2);

        long startTime = System.currentTimeMillis();
        ResultDto resultDto = null;
        try {
            WebClientChunkUpload webClientChunkUpload = new WebClientChunkUpload(file);
            resultDto = webClientChunkUpload.init("8b5f690aa946575664ec8c54f8ae5e26",
                            file.getOriginalFilename(), "document", file.getSize(), new Date().getTime() + "", "")
                    .start();
            long endTime = System.currentTimeMillis();
            map.put("code", resultDto.getCode());
            map.put("time", endTime - startTime);
        } catch (Exception e) {
            map.put("error", e);
        }

        return map;
    }


}
