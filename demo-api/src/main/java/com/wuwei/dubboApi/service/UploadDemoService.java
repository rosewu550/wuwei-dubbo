package com.wuwei.dubboApi.service;

import com.wuwei.dubboApi.entity.Document;

import java.io.InputStream;

public interface UploadDemoService {

    public void uploadDocument(Document document, InputStream inputStream);
}
