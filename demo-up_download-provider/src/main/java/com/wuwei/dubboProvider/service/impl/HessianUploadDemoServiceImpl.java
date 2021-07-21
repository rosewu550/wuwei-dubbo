package com.wuwei.dubboProvider.service.impl;

import com.wuwei.dubboApi.entity.Document;
import com.wuwei.dubboApi.service.UploadDemoService;
import org.apache.dubbo.config.annotation.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Optional;
import java.util.UUID;

@Service(protocol = "hessian", group = "hessian")
public class HessianUploadDemoServiceImpl implements UploadDemoService {
    protected final Logger logger = LoggerFactory.getLogger(HessianUploadDemoServiceImpl.class);

    @Override
    public void uploadDocument(Document document, InputStream in) {
        String name = document.getName();
        try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(processUploadFile(name)))) {
            int i;
            BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
            while ((i = bufferedInputStream.read()) != -1) {
                bufferedOutputStream.write(i);
            }
            logger.info("***************上传成功！****************");
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("***************上传失败！****************");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void uploadDocumentByInputStream(String filename, InputStream in) {
        try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(processUploadFile(filename)))) {
            int i;
            byte[] bytes = new byte[4 * 1024];
            BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
            while ((i = bufferedInputStream.read(bytes)) != -1) {
                bufferedOutputStream.write(bytes, 0, i);
            }
            logger.info("***************InputStream上传成功！****************");
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("***************InputStream上传失败！****************");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 直接以File对象上传
     */
    @Override
    public void uploadDocument(String filename, File file) {
        try (
                FileInputStream uploadFileIn =
                        new FileInputStream(file);
                BufferedInputStream bufferedInputStream =
                        new BufferedInputStream(uploadFileIn);
                FileOutputStream fileOutputStream =
                        new FileOutputStream(processUploadFile(filename));
                BufferedOutputStream bufferedOutputStream =
                        new BufferedOutputStream(fileOutputStream)
        ) {
            int i;
            byte[] bytes = new byte[4 * 1024];
            while ((i = bufferedInputStream.read(bytes)) != -1) {
                bufferedOutputStream.write(bytes, 0, i);
            }
            logger.info("File上传成功！");
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("File上传失败！");
        }
    }

    /**
     * 以字节数组的方式上传文件
     */
    @Override
    public void uploadDocumentByBytes(String filename, byte[] fileByte) {
        try (
                FileOutputStream fileOutputStream =
                        new FileOutputStream(processUploadFile(filename));
                BufferedOutputStream bufferedOutputStream =
                        new BufferedOutputStream(fileOutputStream)
        ) {
            byte[] bytes = Optional.ofNullable(fileByte).orElse(new byte[]{});
            bufferedOutputStream.write(bytes);
            logger.info("byte[]上传成功！");
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("byte[]上传失败！");
        }
    }

    @Override
    public void uploadDocument(String filename, MultipartFile multipartFile) {
//        try (InputStream inputStream = multipartFile.getInputStream()) {
//            this.uploadDocument(filename, inputStream);
//            System.out.println("multipartFile上传成功！");
//        } catch (IOException e) {
//            e.printStackTrace();
//            System.out.println("multipartFile上传失败！");
//        }
    }

    /**
     * 对上传的内容进行处理，并生成File对象
     */
    private File processUploadFile(String filename) {
        String uuid = UUID.randomUUID().toString();
        filename = uuid;
        File uploadFileDirectory = new File("G:\\jmeter_test_download");
        if (!uploadFileDirectory.exists() && !uploadFileDirectory.isDirectory()) {
            boolean isMkdirs = uploadFileDirectory.mkdirs();
            if (!isMkdirs) {
                throw new RuntimeException("文件夹创建失败！");
            }
        }

        return new File(uploadFileDirectory, filename);
    }


}
