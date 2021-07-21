package com.wuwei.dubboProvider.service.impl;

import com.wuwei.dubboApi.service.DownloadDemoService;
import org.apache.dubbo.config.annotation.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

@Service(protocol = "dubbo", group = "dubbo", timeout = 30000)
public class DubboDownloadDemoServiceImpl implements DownloadDemoService {
    protected final Logger logger = LoggerFactory.getLogger(DubboDownloadDemoServiceImpl.class);

    @Override
    public InputStream downloadDocument(String path) {
        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            logger.info("dubbo下载成功！");
            return fileInputStream;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            logger.error("dubbo下载失败！");
        }
        return null;
    }
}
