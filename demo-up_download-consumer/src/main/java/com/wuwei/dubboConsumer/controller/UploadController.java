package com.wuwei.dubboConsumer.controller;

import com.wuwei.dubboApi.entity.Document;
import com.wuwei.dubboApi.service.DownloadDemoService;
import com.wuwei.dubboApi.service.UploadDemoService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.util.Objects;

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


}
