package com.wuwei.filestorage.common.partupload;


import javax.validation.constraints.NotBlank;

public class Upload4ModuleParam {

    private String tenantKey;

    /**
     *  文件名
     */
    @NotBlank(message = "fileName is null")
    private String name;
    /**
     * 每个分块的大小
     */
    private Long size;
    /**
     * 文件最后修改时间，毫秒时间戳
     */
    private String lastModified;
    /**
     * 文件最后修改时间，毫秒时间戳
     */
    private String lastModifiedDate;
    /**
     * 当前为第几分片
     */
    private Integer chunk;
    /**
     * 分片总数
     */
    private Integer chunks;

    /**
     * 关联的文档id
     */
    private Long refId;
    /**
     * 模块名
     */
    @NotBlank(message = "module is null")
    private String module;
    /**
     * 登录token
     */
    private String ETEAMSID;

    /**
     * 水印信息
     */
    private String waterParamStr;
    /**
     * 标识是不是评论组件，用于获取契约锁的按钮信息
     */
    private Boolean commentFlag = false;
    /**
     * 文件的MD5值，用于秒传
     */
    private String MD5;

    /**
     * 省市位置，用于水印的支持
     */
    private String position;
    /**
     * 目录id
     */
    private Long folderId;
    /**
     * 目录类型
     */
    private String folderType;

    private String fromTerminalType;
    /**
     * 项目id
     */
    private Long mainlineId;
    /**
     * 项目文件夹id
     */
    private Long moduleFolderId;
    /**
     * 操作来源（用于标识，文件是从哪里操作上传，便于特殊业务逻辑处理）
     */
    private String source;

    private boolean createDoc;

    public String getTenantKey() {
        return tenantKey;
    }

    public void setTenantKey(String tenantKey) {
        this.tenantKey = tenantKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public Integer getChunk() {
        return chunk;
    }

    public void setChunk(Integer chunk) {
        this.chunk = chunk;
    }

    public Integer getChunks() {
        return chunks;
    }

    public void setChunks(Integer chunks) {
        this.chunks = chunks;
    }

    public Long getRefId() {
        return refId;
    }

    public void setRefId(Long refId) {
        this.refId = refId;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getWaterParamStr() {
        return waterParamStr;
    }

    public void setWaterParamStr(String waterParamStr) {
        this.waterParamStr = waterParamStr;
    }

    public String getETEAMSID() {
        return ETEAMSID;
    }

    public void setETEAMSID(String ETEAMSID) {
        this.ETEAMSID = ETEAMSID;
    }

    public Boolean getCommentFlag() {
        return commentFlag;
    }

    public void setCommentFlag(Boolean commentFlag) {
        this.commentFlag = commentFlag;
    }

    public String getMD5() {
        return MD5;
    }

    public void setMD5(String MD5) {
        this.MD5 = MD5;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Long getFolderId() {
        return folderId;
    }

    public void setFolderId(Long folderId) {
        this.folderId = folderId;
    }

    public String getFolderType() {
        return folderType;
    }

    public void setFolderType(String folderType) {
        this.folderType = folderType;
    }

    public Long getMainlineId() {
        return mainlineId;
    }

    public void setMainlineId(Long mainlineId) {
        this.mainlineId = mainlineId;
    }

    public Long getModuleFolderId() {
        return moduleFolderId;
    }

    public void setModuleFolderId(Long moduleFolderId) {
        this.moduleFolderId = moduleFolderId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public boolean isCreateDoc() {
        return createDoc;
    }

    public void setCreateDoc(boolean createDoc) {
        this.createDoc = createDoc;
    }

    @Override
    public String toString() {
        return "Upload4ModuleParam{" +
                "name='" + name + '\'' +
                ", size=" + size +
                ", lastModified='" + lastModified + '\'' +
                ", lastModifiedDate='" + lastModifiedDate + '\'' +
                ", chunk=" + chunk +
                ", chunks=" + chunks +
                ", refId=" + refId +
                ", module='" + module + '\'' +
                ", ETEAMSID='" + ETEAMSID + '\'' +
                ", waterParamStr='" + waterParamStr + '\'' +
                ", commentFlag=" + commentFlag +
                ", MD5='" + MD5 + '\'' +
                ", position='" + position + '\'' +
                ", folderId=" + folderId +
                ", folderType='" + folderType + '\'' +
                ", fromTerminalType='" + fromTerminalType + '\'' +
                ", mainlineId=" + mainlineId +
                ", moduleFolderId=" + moduleFolderId +
                ", source='" + source + '\'' +
                ", createDoc='" + createDoc + '\'' +
                '}';
    }

    public String getFromTerminalType() {
        return fromTerminalType;
    }

    public void setFromTerminalType(String fromTerminalType) {
        this.fromTerminalType = fromTerminalType;
    }

}
