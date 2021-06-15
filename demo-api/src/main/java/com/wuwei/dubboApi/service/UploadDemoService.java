package com.wuwei.dubboApi.service;

import com.wuwei.dubboApi.entity.Document;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;

public interface UploadDemoService {

    void uploadDocument(Document document, InputStream inputStream);

    void uploadDocument(String filename, InputStream inputStream);

    void uploadDocument(String filename, MultipartFile multipartFile);

    void uploadDocument(String filename, File file);

    void uploadDocument(String filename, byte[] fileBytes);
}
