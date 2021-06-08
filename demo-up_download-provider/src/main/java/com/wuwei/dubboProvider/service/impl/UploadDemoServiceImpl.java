package com.wuwei.dubboProvider.service.impl;

import com.wuwei.dubboApi.entity.Document;
import com.wuwei.dubboApi.service.UploadDemoService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;

import java.io.*;
import java.util.UUID;

@Service
public class UploadDemoServiceImpl implements UploadDemoService {


    @Override
    public void uploadDocument(Document document, InputStream in) {
        String name = document.getName();
        String uuid = UUID.randomUUID().toString();

        BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
        String fileName = uuid + name;
        File uploadFile = new File(System.getProperty("user.dir") + File.separator + "upload/demo" + File.separator + fileName);
        try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(uploadFile));) {
            int i;
            while ((i = bufferedInputStream.read()) != -1) {
                bufferedOutputStream.write(i);
            }
            System.out.println("***************上传成功！****************");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("***************上传失败！****************");
        }
    }
}
