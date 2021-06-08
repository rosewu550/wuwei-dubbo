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

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

@RestController("/file")
public class UploadController {

    @Reference
    private UploadDemoService uploadDemoService;

    @Reference
    private DownloadDemoService downloadDemoService;

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file){
        String msg;
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        try {
            if(fileName.contains("..")) {
                throw new Exception("文件名称无效：" + fileName);
            }
            Document document = new Document();
            document.setName(file.getName());
            InputStream inputStream = file.getInputStream();
            uploadDemoService.uploadDocument(document,inputStream);
            msg = "上传成功！";
        } catch (Exception e) {
            e.printStackTrace();
            msg = "上传失败！";
        }
        return msg;
    }


}
