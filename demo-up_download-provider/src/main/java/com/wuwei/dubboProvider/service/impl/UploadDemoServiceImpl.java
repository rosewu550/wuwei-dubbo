package com.wuwei.dubboProvider.service.impl;

import com.wuwei.dubboApi.entity.Document;
import com.wuwei.dubboApi.service.UploadDemoService;
import org.apache.dubbo.config.annotation.Service;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service(protocol = "hessian")
public class UploadDemoServiceImpl implements UploadDemoService {


    @Override
    public void uploadDocument(Document document, InputStream in) {
        String name = document.getName();
        String uuid = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        File uploadFileDirectory = new File(System.getProperty("user.dir") + File.separator + "upload/demo");
        if (!uploadFileDirectory.exists() && !uploadFileDirectory.mkdirs()) {
            System.out.println("创建失败");
            return;
        }

        String fileName = uuid + name;
        File uploadFile = new File(uploadFileDirectory, fileName);
        try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(uploadFile));) {
            int i;
            BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
            while ((i = bufferedInputStream.read()) != -1) {
                bufferedOutputStream.write(i);
            }
            System.out.println("***************上传成功！****************");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("***************上传失败！****************");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
