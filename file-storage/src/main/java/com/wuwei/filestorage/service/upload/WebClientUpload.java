package com.wuwei.filestorage.service.upload;


import com.alibaba.fastjson.JSON;
import com.wuwei.filestorage.constant.FileConstant;
import com.wuwei.filestorage.entity.Upload4ModuleParam;
import com.wuwei.filestorage.entity.UploadModuleDto;
import com.wuwei.filestorage.utils.FileUtils;
import org.apache.commons.beanutils.BeanMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
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

    private WebClientUpload() {
    }

    public WebClientUpload(MultipartFile file) {
        MultiValueMap<String, Object> currentBodyMap = new LinkedMultiValueMap<>();
        currentBodyMap.add(FileConstant.FILE, file.getResource());
        this.bodyMap = currentBodyMap;
    }

    public WebClientUpload(File file) {
        MultiValueMap<String, Object> currentBodyMap = new LinkedMultiValueMap<>();
        currentBodyMap.add(FileConstant.FILE, new FileSystemResource(file));
        this.bodyMap = currentBodyMap;
    }

    public WebClientUpload(byte[] fileByteArray) {
        MultiValueMap<String, Object> currentBodyMap = new LinkedMultiValueMap<>();
        currentBodyMap.add(FileConstant.FILE, new ByteArrayResource(fileByteArray));
        this.bodyMap = currentBodyMap;
    }

    public WebClientUpload(InputStream inputStream) {
        MultiValueMap<String, Object> currentBodyMap = new LinkedMultiValueMap<>();
        currentBodyMap.add(FileConstant.FILE, new InputStreamResource(inputStream));
        this.bodyMap = currentBodyMap;
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

//    public Flux<UploadModuleDto> unblockUpload() {
//        return this.upload();
//    }

    public String blockUpload() {
        return this.upload().block();
    }

    public String blockUpload(long maxWaitTimeInSeconds) {
        return this.upload().block(Duration.ofSeconds(maxWaitTimeInSeconds));
    }

    private Mono<String> upload() {
        BeanMap beanMap = new BeanMap(this.assembleEntity());
        Set<Map.Entry<String, Object>> beanSet = beanMap.entrySet();
        Map<String, List<Object>> collectMap = beanSet.stream()
                .filter(entry -> null != entry.getValue())
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> Collections.singletonList(entry.getValue())));
        this.bodyMap.addAll(new LinkedMultiValueMap<>(collectMap));

        return WebClient.builder()
                .baseUrl(FileUtils.getUploadUrl())
                .defaultCookie(FileConstant.ETEAMS_ID, this.eteamsId)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
                .build()
                .post()
                .body(BodyInserters.fromMultipartData(this.bodyMap))
                .retrieve()
                .bodyToMono(String.class);
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
