package com.wuwei.dubboconsumer.controller;


import com.wuwei.filestorage.strategy.StorageStrategy;
import com.wuwei.filestorage.utils.StorageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

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
            String s = storageStrategy.listParts(tenantKey, currentFileId, uploadId);
            Map<String, String> stringStringMap = storageStrategy.completeMultipartUpload(tenantKey, currentFileId, uploadId);
//            String copyFileId = storageStrategy.copyFile(currentFileId, tenantKey, tenantKey);
//            int i = storageStrategy.deleteFile(copyFileId, tenantKey);
            System.out.println(stringStringMap);
            msg = "上传成功！";
        } catch (Exception e) {
            e.printStackTrace();
            msg = "上传失败！";
        }
        return msg;
    }

    @GetMapping("/MD5")
    public void caculateMD5() {
        String s = StorageUtils.calculateMD5("/Users/wuwei/IdeaProjects/wuwei-dubbo/upload/storage/EF4TG6ZX/eteams/45eb311c86d542feabb2d1e489db2cb8_local");
        String s1 = StorageUtils.calculateMD5("/Users/wuwei/IdeaProjects/wuwei-dubbo/upload/storage/EF4TG6ZX/eteams/Wireshark 3.6.0 Intel 64.dmg");
        System.out.println(s.equals(s1));
    }

    private List<byte[]> convertToChunk(InputStream inputStream) {
        List<byte[]> chunkList = new ArrayList<>();
        try {
            int len;
            byte[] chunkByteArray = new byte[4 * 1024 * 1024];
            while ((len = inputStream.read(chunkByteArray)) != -1) {
                byte[] newByteArray = new byte[len];
                System.arraycopy(chunkByteArray, 0, newByteArray, 0, len);
                chunkList.add(newByteArray);
                chunkByteArray = new byte[4 * 1024 * 1024];
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return chunkList;
    }


}
