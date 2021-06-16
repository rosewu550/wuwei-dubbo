package com.wuwei.dubboProvider.service.impl;

import com.wuwei.dubboApi.entity.Document;
import com.wuwei.dubboApi.service.UploadDemoService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;


@Service(protocol = "dubbo",group = "dubbo",timeout = 30000)
public class DubboUploadDemoServiceImpl implements UploadDemoService {

    @Override
    public void uploadDocument(Document document, InputStream inputStream) {

    }

    @Override
    public void uploadDocument(String filename, InputStream inputStream) {

    }

    @Override
    public void uploadDocument(String filename, MultipartFile multipartFile) {

    }

    @Override
    public void uploadDocument(String filename, File file) {

    }

    /**
     * 以字节数组的方式上传文件
     */
    @Override
    public void uploadDocument(String filename, byte[] fileByte) {
        try (
                FileOutputStream fileOutputStream =
                        new FileOutputStream(processUploadFile(filename));
                BufferedOutputStream bufferedOutputStream =
                        new BufferedOutputStream(fileOutputStream)
        ) {
            byte[] bytes = Optional.ofNullable(fileByte).orElse(new byte[]{});
            bufferedOutputStream.write(bytes);
            System.out.println("上传成功！");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("上传失败！");
        }
    }

    /**
     * 对上传的内容进行处理，并生成File对象
     */
    private File processUploadFile(String filename) {
        String uuid = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        filename = uuid + filename;
        File uploadFileDirectory = new File(System.getProperty("user.dir") + File.separator + "upload/demo");
        if (!uploadFileDirectory.exists() && !uploadFileDirectory.isDirectory()) {
            boolean isMkdirs = uploadFileDirectory.mkdirs();
            if (!isMkdirs) {
                throw new RuntimeException("文件夹创建失败！");
            }
        }

        return new File(uploadFileDirectory, filename);
    }


}
