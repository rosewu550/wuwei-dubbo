package com.wuwei.dubboApi.service;

import com.wuwei.dubboApi.entity.Document;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;

/**
 * @author wuwei
 * @since 2021
 * @dubbo
 */
public interface UploadDemoService {

    /**
     * 测试1
     * @param document 字段1
     * @param inputStream 字段11
     */
    void uploadDocument(Document document, InputStream inputStream);

    /**
     * 测试2
     * @param filename 字段 2
     * @param multipartFile 字段22
     */
    void uploadDocument(String filename, MultipartFile multipartFile);

    /**
     * 测试3
     * @param filename 字段 3
     * @param file 字段32
     */
    void uploadDocument(String filename, File file);

    /**
     * 测试4
     * @param filename 字段4
     * @param fileBytes 字段42
     */
    void uploadDocument(String filename, byte[] fileBytes);

    /**
     * 测试5
     * @param filename 字段5
     * @param inputStream 字段52
     */
    void uploadDocumentByInputStream(String filename, InputStream inputStream);
}
