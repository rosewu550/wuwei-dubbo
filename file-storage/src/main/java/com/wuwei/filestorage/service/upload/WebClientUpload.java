package com.wuwei.filestorage.service.upload;


import com.wuwei.filestorage.constant.FileConstant;
import com.wuwei.filestorage.entity.ResultDto;
import com.wuwei.filestorage.entity.Upload4ModuleParam;
import com.wuwei.filestorage.utils.FileUtils;
import org.apache.commons.beanutils.BeanMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.InputStream;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

/**
 * http请求快捷上传（基于webclient）
 *
 * @author wuwei
 * @since 2021/09/01 pm
 */
public class WebClientUpload {

    private static final Logger logger = LoggerFactory.getLogger(WebClientUpload.class);


    private String eteamsId;

    /**
     * 模块id
     */
    private String module;

    /**
     * 文件大小
     */
    private long size;

    /**
     * 文件名
     */
    private String name;

    /**
     * 文件最后修改时间
     */
    private String lastModified;


    private String lastModifiedDate;


    /**
     * 文件来源id
     */
    private long refId;

    /**
     * 分片个数
     */
    private int chunks;

    /**
     * 当前所传分片编号，起始值为0
     */
    private int chunk;

    /**
     * 水印信息
     */
    private String waterParamStr;

    /**
     * 省市位置，用于水印的支持
     */
    private String position;

    /**
     * 文件MD5值，用于秒传
     */
    private String md5;

    /**
     * 目录id
     */
    private long folderId;

    /**
     * 目录类型
     */
    private String folderType;

    private MultiValueMap<String, Object> bodyMap;

    private MultipartBodyBuilder uploadBodyBuilder;

    private WebClientUpload() {
    }

    public WebClientUpload(MultipartFile file) {
        this.assembleBuilder(file.getResource());
    }

    public WebClientUpload(File file) {
        this.assembleBuilder(new FileSystemResource(file));
    }

    public WebClientUpload(byte[] fileByteArray) {
        this.assembleBuilder(new ByteArrayResource(fileByteArray));
    }

    public WebClientUpload(InputStream inputStream) {
        this.assembleBuilder(new InputStreamResource(inputStream));
    }

    public WebClientUpload(MultipartFile... files) {
        MultiValueMap<String, Object> currentBodyMap = new LinkedMultiValueMap<>();
        for (MultipartFile file : files) {
            currentBodyMap.add(FileConstant.FILES, file.getResource());
        }
        this.bodyMap = currentBodyMap;
    }

    public WebClientUpload(File[] files) {
        MultiValueMap<String, Object> currentBodyMap = new LinkedMultiValueMap<>();
        for (File file : files) {
            currentBodyMap.add(FileConstant.FILES, new FileSystemResource(file));
        }
        this.bodyMap = currentBodyMap;
    }

    private void assembleBuilder(Resource resource){
        this.uploadBodyBuilder = new MultipartBodyBuilder();
        String headerStr =  String.format("form-data; name=%s; filename=''", "file");
        uploadBodyBuilder.part("file",resource).header("Content-Disposition",headerStr);

    }

    public WebClientUpload init(String eteamsId, String name, String module, long size, String lastModified, String lastModifiedDate) {
        this.name = name;
        this.size = size;
        this.module = module;
        this.eteamsId = eteamsId;
        this.lastModified = lastModified;
        this.lastModifiedDate = lastModifiedDate;
        return this;
    }

    public WebClientUpload addEteamsId(String eteamsId) {
        this.eteamsId = eteamsId;
        return this;
    }

    public WebClientUpload addRefId(long refId) {
        this.refId = refId;
        return this;
    }

    public WebClientUpload addChunk(int chunk) {
        this.chunk = chunk;
        return this;
    }

    public WebClientUpload addChunks(int chunks) {
        this.chunks = chunks;
        return this;
    }

    public WebClientUpload addWaterParamStr(String waterParamStr) {
        this.waterParamStr = waterParamStr;
        return this;
    }

    public WebClientUpload addPosition(String position) {
        this.position = position;
        return this;
    }

    public WebClientUpload addMd5(String md5) {
        this.md5 = md5;
        return this;
    }

    public WebClientUpload addFolderId(long folderId) {
        this.folderId = folderId;
        return this;
    }

    public WebClientUpload addFolderType(String folderType) {
        this.folderType = folderType;
        return this;
    }

    public Mono<ResultDto> unblockUpload() {
        return this.upload();
    }

    public ResultDto blockUpload() {
        return this.upload().block();
    }

    public ResultDto blockUpload(long maxWaitTimeInSeconds) {
        return this.upload().block(Duration.ofSeconds(maxWaitTimeInSeconds));
    }

    private Mono<ResultDto> upload() {
        BeanMap beanMap = new BeanMap(this.assembleEntity());
        Set<Map.Entry<String, Object>> beanSet = beanMap.entrySet();
        beanSet.stream()
                .filter(this::filterUploadBeanMap)
                .forEach(entry -> this.uploadBodyBuilder.part(entry.getKey(), entry.getValue()));

        logger.info(">>>>>>webClientUpload start upload<<<<<<");
        logger.info(">>>>>>webClientUpload current eteamsId:{}", this.eteamsId);
        logger.info(">>>>>>webClientUpload current url:{}", FileUtils.getUploadUrl());
        return WebClient.builder()
                .baseUrl(FileUtils.getUploadUrl())
                .defaultCookie(FileConstant.ETEAMS_ID, this.eteamsId)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
                .build()
                .post()
                .body(BodyInserters.fromMultipartData(uploadBodyBuilder.build()))
                .retrieve()
                .bodyToMono(ResultDto.class);
    }



    private boolean filterUploadBeanMap(Map.Entry<String,Object> entry){
        boolean isSaveParam;
        Object value = entry.getValue();
        if (null == value) {
            isSaveParam = false;
        } else if (value instanceof Integer){
            isSaveParam = 0 != (int)value;
        } else if (value instanceof Long) {
            isSaveParam = 0 != (long)value;
        } else {
            isSaveParam = true;
        }
        return isSaveParam;
    }

    private Upload4ModuleParam assembleEntity() {
        Upload4ModuleParam upload4ModuleParam = new Upload4ModuleParam();
        upload4ModuleParam.setMD5(this.md5);
        upload4ModuleParam.setName(this.name);
        upload4ModuleParam.setSize(this.size);
        upload4ModuleParam.setRefId(this.refId);
        upload4ModuleParam.setChunk(this.chunk);
        upload4ModuleParam.setChunks(this.chunks);
        upload4ModuleParam.setModule(this.module);
        upload4ModuleParam.setPosition(this.position);
        upload4ModuleParam.setFolderId(this.folderId);
        upload4ModuleParam.setFolderType(this.folderType);
        upload4ModuleParam.setLastModified(this.lastModified);
        upload4ModuleParam.setWaterParamStr(this.waterParamStr);
        upload4ModuleParam.setLastModifiedDate(this.lastModifiedDate);
        return upload4ModuleParam;
    }

}
