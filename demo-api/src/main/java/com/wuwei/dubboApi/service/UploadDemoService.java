package com.wuwei.dubboApi.service;

import com.wuwei.dubboApi.entity.Document;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;

/**
 * @author wuwei
 * @since 2021
 */
public interface UploadDemoService {

    void uploadDocument(Document document, InputStream inputStream);

    void uploadDocument(String filename, MultipartFile multipartFile);

    void uploadDocument(String filename, File file);

    void uploadDocumentByBytes(String filename, byte[] fileBytes);

    void uploadDocumentByInputStream(String filename, InputStream inputStream);
}
