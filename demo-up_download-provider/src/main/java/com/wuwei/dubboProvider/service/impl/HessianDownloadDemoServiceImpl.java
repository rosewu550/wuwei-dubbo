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
        FileInputStream fileInputStream;
        try {
             fileInputStream = new FileInputStream(path);
            logger.info("hessian下载成功！");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            logger.error("hessian下载失败！");
            fileInputStream = null;
        }
        return fileInputStream;
    }

    @Override
    public byte[] downloadDocumentByte(String path) {
        byte[] resultByteArray;
        try (FileInputStream fileInputStream = new FileInputStream(path)) {
            ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
            int bufferSize;
            byte[] bufferArray = new byte[4096];
            while ((bufferSize = fileInputStream.read(bufferArray, 0, bufferArray.length - 1)) != -1) {
                swapStream.write(bufferArray, 0, bufferSize);
            }
            resultByteArray = swapStream.toByteArray();
            logger.info("hessian下载成功！");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("hessian下载失败！");
            resultByteArray = new byte[0];
        }

        return resultByteArray;
    }
}
