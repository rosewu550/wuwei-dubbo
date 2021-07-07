package com.wuwei.dubboProvider.service.impl;

import com.wuwei.dubboApi.service.DownloadDemoService;
import org.apache.dubbo.config.annotation.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Service(protocol = "dubbo", group = "dubbo", timeout = 30000)
public class DubboDownloadDemoServiceImpl implements DownloadDemoService {
    protected final Logger logger = LoggerFactory.getLogger(DubboDownloadDemoServiceImpl.class);

    @Override
    public InputStream downloadDocument() {
        InputStream downloadStream;
        try (FileInputStream fileInputStream = new FileInputStream("")) {
            downloadStream = new BufferedInputStream(fileInputStream);
            logger.info("dubbo下载成功！");
        } catch (IOException e) {
            downloadStream = null;
            logger.error("dubbo下载成功！");
        }
        return downloadStream;
    }
}
