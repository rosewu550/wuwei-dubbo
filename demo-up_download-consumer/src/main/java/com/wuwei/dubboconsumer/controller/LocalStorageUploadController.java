package com.wuwei.dubboconsumer.controller;


import com.wuwei.filestorage.strategy.StorageStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/local")
public class LocalStorageUploadController {
    @Autowired
    private StorageStrategy storageStrategy;

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file,
                             @RequestParam("tenantKey") String tenantKey,
                             @RequestParam("fileId") String fileId) {
        String msg;
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        try {
            if (fileName.contains("..")) {
                throw new Exception("文件名称无效：" + fileName);
            }
            byte[] bytes = file.getBytes();
            msg = storageStrategy.putFile(bytes, tenantKey);
        } catch (Exception e) {
            e.printStackTrace();
            msg = "上传失败！";
        }
        return msg;
    }

    @PostMapping("/part/upload")
    public String uploadPartFile(@RequestParam("file") MultipartFile file,
                                 @RequestParam("tenantKey") String tenantKey,
                                 @RequestParam("fileId") String fileId
    ) {
        String msg;
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        try {
            if (fileName.contains("..")) {
                throw new Exception("文件名称无效：" + fileName);
            }
            Map<String, String> uploadPartResultMap = storageStrategy.initiateMultipartUpload(tenantKey, fileId);
            String uploadId = uploadPartResultMap.get("uploadId");
            String currentFileId = uploadPartResultMap.get("fileId");

            List<byte[]> partList = this.convertToChunk(file.getInputStream());
            int index = 0;
            for (byte[] partArray : partList) {
                storageStrategy.uploadPart(tenantKey, currentFileId, uploadId, ++index, partArray.length, new ByteArrayInputStream(partArray));
            }
            Map<String, String> stringStringMap = storageStrategy.completeMultipartUpload(tenantKey, currentFileId, uploadId);
            System.out.println(stringStringMap);
            msg = "上传成功！";
        } catch (Exception e) {
            e.printStackTrace();
            msg = "上传失败！";
        }
        return msg;
    }

    private List<byte[]> convertToChunk(InputStream inputStream) {
        List<byte[]> chunkList = new ArrayList<>();
        try {
            byte[] chunkByteArray = new byte[1024 * 1024];
            while (inputStream.read(chunkByteArray) != -1) {
                chunkList.add(chunkByteArray);
                chunkByteArray = new byte[1024 * 1024];
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return chunkList;
    }


}
