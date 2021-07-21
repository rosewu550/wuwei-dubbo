package com.wuwei.dubboProvider.service.impl;

import com.wuwei.dubboApi.service.DownloadDemoService;
import org.apache.dubbo.config.annotation.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

@Service(protocol = "hessian", group = "hessian",timeout = 100000)
public class HessianDownloadDemoServiceImpl implements DownloadDemoService {
    protected final Logger logger = LoggerFactory.getLogger(HessianDownloadDemoServiceImpl.class);

    @Override
    public InputStream downloadDocument(String path) {
        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            logger.info("hessian下载成功！");
            return fileInputStream;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            logger.error("hessian下载失败！");
        }
        return null;
    }
}
