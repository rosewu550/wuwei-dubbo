package com.wuwei.dubboApi.service;

import java.io.InputStream;

public interface DownloadDemoService {
    public InputStream downloadDocument(String path);

    public byte[] downloadDocumentByte(String path);
}
