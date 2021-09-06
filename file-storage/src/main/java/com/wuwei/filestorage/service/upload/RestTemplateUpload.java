package com.wuwei.filestorage.service.upload;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.wuwei.filestorage.config.FileProperties;
import com.wuwei.filestorage.constant.FileConstant;
import com.wuwei.filestorage.entity.ResultDto;
import com.wuwei.filestorage.entity.Upload4ModuleParam;
import com.wuwei.filestorage.entity.UploadModuleDto;
import org.apache.commons.beanutils.BeanMap;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import com.wuwei.filestorage.utils.RestApiClient;
import com.wuwei.filestorage.utils.SpringContextUtil;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * http请求快捷上传
 *
 * @author wuwei
 * @since 2021/09/01 pm
 */
public class RestTemplateUpload {

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

    private RestTemplateUpload() {
    }

    public RestTemplateUpload(MultipartFile file) {
        MultiValueMap<String, Object> currentBodyMap = new LinkedMultiValueMap<>();
        currentBodyMap.add("file", file.getResource());
        this.bodyMap = currentBodyMap;
    }

//    public RestTemplateUpload(MultipartFile... files) {
//        MultiValueMap<String, Object> currentBodyMap = new LinkedMultiValueMap<>();
//        for (MultipartFile file : files) {
//            currentBodyMap.add("files", file.getResource());
//        }
//        this.bodyMap = currentBodyMap;
//    }


    public RestTemplateUpload init(String eteamsId, String name, String module, long size, String lastModified, String lastModifiedDate) {
        this.name = name;
        this.size = size;
        this.module = module;
        this.eteamsId = eteamsId;
        this.lastModified = lastModified;
        this.lastModifiedDate = lastModifiedDate;
        return this;
    }


    public RestTemplateUpload addRefId(long refId) {
        this.refId = refId;
        return this;
    }

    public RestTemplateUpload addChunk(int chunk) {
        this.chunk = chunk;
        return this;
    }

    public RestTemplateUpload addChunks(int chunks) {
        this.chunks = chunks;
        return this;
    }

    public RestTemplateUpload addWaterParamStr(String waterParamStr) {
        this.waterParamStr = waterParamStr;
        return this;
    }

    public RestTemplateUpload addPosition(String position) {
        this.position = position;
        return this;
    }

    public RestTemplateUpload addMd5(String md5) {
        this.md5 = md5;
        return this;
    }

    public RestTemplateUpload addFolderId(long folderId) {
        this.folderId = folderId;
        return this;
    }

    public RestTemplateUpload addFolderType(String folderType) {
        this.folderType = folderType;
        return this;
    }


    public ResultDto upload() {
        String beanJsonStr = JSON.toJSONString(this.assembleEntity());
        Map<String,Object> beanMap = JSON.parseObject(beanJsonStr, Map.class);
        Set<Map.Entry<String, Object>> beanSet = beanMap.entrySet();
        Map<String, List<Object>> collectMap = beanSet.stream()
                .filter(entry -> null != entry.getValue())
                .filter(entry -> !"0".equals(entry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> Collections.singletonList(entry.getValue())));
        this.bodyMap.addAll(new LinkedMultiValueMap<>(collectMap));

        FileProperties fileProperties = SpringContextUtil.getBean(FileProperties.class);
        String uploadUrl = fileProperties.getHost() + FileConstant.UPLOAD_ENDPOINT;
        HttpEntity<MultiValueMap<String, Object>> httpEntity = RestApiClient.assembleRequestEntity(this.bodyMap);
        ResponseEntity<String> responseEntity = RestApiClient.postByRest(uploadUrl, httpEntity);
        return RestApiClient.analysisByRest(responseEntity, new TypeReference<ResultDto>() {
        });
    }

    public ResultDto upload2() {
        String beanJsonStr = JSON.toJSONString(this.assembleEntity());
        Map<String,Object> beanMap = JSON.parseObject(beanJsonStr, Map.class);
        Set<Map.Entry<String, Object>> beanSet = beanMap.entrySet();
        Map<String, List<Object>> collectMap = beanSet.stream()
                .filter(entry -> null != entry.getValue())
                .filter(entry -> !"0".equals(entry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> Collections.singletonList(entry.getValue())));
        this.bodyMap.addAll(new LinkedMultiValueMap<>(collectMap));

        FileProperties fileProperties = SpringContextUtil.getBean(FileProperties.class);
        String uploadUrl = fileProperties.getHost() + FileConstant.UPLOAD_ENDPOINT;
        HttpEntity<MultiValueMap<String, Object>> httpEntity = RestApiClient.assembleRequestEntity(this.bodyMap);
        ResponseEntity<String> responseEntity = RestApiClient.postByRest("ETEAMSID=b76c79d488e6ee77c56b7b44e6a54091",uploadUrl, httpEntity);
        return RestApiClient.analysisByRest(responseEntity, new TypeReference<ResultDto>() {
        });
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
