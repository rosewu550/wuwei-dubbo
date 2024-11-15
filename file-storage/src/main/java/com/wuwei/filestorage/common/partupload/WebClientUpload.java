package com.wuwei.filestorage.common.partupload;


import org.apache.commons.beanutils.BeanMap;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.client.MultipartBodyBuilder;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

/**
 * http请求快捷上传（基于webclient）
 *
 * @author wuwei
 * @since 2021/09/01 pm
 */
public class WebClientUpload extends Upload {


    /**
     * 文件body
     */
    private MultipartBodyBuilder uploadBodyBuilder;

    private final ParameterizedTypeReference<ResultDto<UploadModuleDto>> uploadModuleDtoType =
            new ParameterizedTypeReference<ResultDto<UploadModuleDto>>() {
            };

    private WebClientUpload() {
    }

    public WebClientUpload(byte[] fileByteArray) {
        this.assembleBuilder(new ByteArrayResource(fileByteArray));
    }

    public WebClientUpload init(String tenantKey,String name, String module, long size, String lastModified, String lastModifiedDate) {
        logger.info(">>>>>>webclientUpload is init<<<<<<");
        this.name = name;
        this.size = size;
        this.module = module;
        this.tenantKey = tenantKey;
        this.lastModified = lastModified;
        this.lastModifiedDate = lastModifiedDate;
        return this;
    }

    public WebClientUpload addRefId(long refId) {
        this.refId = refId;
        return this;
    }

    public WebClientUpload addChunks(int chunks) {
        this.chunks = chunks;
        return this;
    }

    public WebClientUpload addChunk(int chunk) {
        this.chunk = chunk;
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

    public WebClientUpload canCreateDoc(boolean createDoc) {
        this.createDoc = createDoc;
        return this;
    }

    public WebClientUpload addTenantKey(String tenantKey) {
        this.tenantKey = tenantKey;
        return this;
    }

    public WebClientUpload addFilterUpload(Predicate<? super Map.Entry<Object, Object>> filterUploadBodyMap) {
        this.filterUploadBodyMap = filterUploadBodyMap;
        return this;
    }

    public <T> ResultDto<T> blockUpload(ParameterizedTypeReference<ResultDto<T>> typeReference) {
        return this.upload(typeReference).block();
    }

    private <T> Mono<ResultDto<T>> upload(ParameterizedTypeReference<ResultDto<T>> typeReference) {
        BeanMap beanMap = new BeanMap(this.assembleEntity());
        Set<Map.Entry<Object, Object>> beanSet = beanMap.entrySet();
        beanSet.stream()
                .filter(this.filterUploadBodyMap)
                .forEach(entry -> this.uploadBodyBuilder.part((String) entry.getKey(), entry.getValue()));

        logger.info(">>>>>>webClientUpload start upload<<<<<<");
        logger.info(">>>>>>webClientUpload current url:{}", PAPI_UPLOAD_ENDPOINT);

        return FileWebClient.upload(PAPI_UPLOAD_ENDPOINT, uploadBodyBuilder, typeReference);
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
        upload4ModuleParam.setCreateDoc(this.createDoc);
        upload4ModuleParam.setTenantKey(this.tenantKey);
        upload4ModuleParam.setFolderType(this.folderType);
        upload4ModuleParam.setLastModified(this.lastModified);
        upload4ModuleParam.setLastModifiedDate(this.lastModifiedDate);
        return upload4ModuleParam;
    }

    private void assembleBuilder(Resource resource) {
        this.uploadBodyBuilder = new MultipartBodyBuilder();
        String headerStr = String.format("form-data; name=%s; filename=''", "file");
        this.uploadBodyBuilder.part("file", resource).header("Content-Disposition", headerStr);
    }
}
