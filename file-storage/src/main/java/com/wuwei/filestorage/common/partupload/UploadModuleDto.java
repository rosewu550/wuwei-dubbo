package com.wuwei.filestorage.common.partupload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;


@JsonSerialize
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UploadModuleDto implements Serializable {


    private static final long serialVersionUID = -1216764134044983664L;
    /**
     * 文件id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long fileid;
    /**
     * 文件名
     */
    private String name;
    /**
     * 文件大小
     */
    private Long size;
    /**
     * 文件后缀名
     */
    private String extName;
    /**
     * minetype类型
     */
    private String type;
    /**
     * 是否是图片
     */
    private Boolean img = false;
    /**
     * 上传人id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long uploadUser;
    /**
     * 上传人姓名
     */
    private String uploadUserName;
    /**
     * 上传时间
     */
    private Date uploadTime;
    /**
     * 是否是文档
     */
    private boolean doc;
    /**
     * 文档id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long docId;
    /**
     * 版本号
     */
    private Integer version;
    /**
     * 下载地址
     */
    private String loadlink;
    /**
     * 压缩图下载地址
     */
    private String smallImg;
    /**
     * 原图下载地址
     */
    private String bigImg;

    /**
     * refid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long refId;

    /**
     * 契约锁的签署状态
     */
    private String signTip;
    /**
     * 契约锁预留自定义返回参数
     */
    private Map<String, Object> otherParams;

    /**
     * 上传类型：second 秒传；chunk 分片；common 普通上传
     */
    private String uploadType;

    /**
     * 标记，是否是被屏蔽的，默认false
     */
    private boolean ban = false;
    /**
     * 密级
     **/
    private Integer secretLevel;
    /**
     * 保密期限
     **/
    private String secretLevelValidity;
    private String validity;

    /**
     * 是否主附件
     */
    private Boolean isMainAccess;
    /**
     * 是否可设置为主附件
     */
    private Boolean canSetMainAccess;
    /**
     * 主附件id
     */
    private Long mainAccessId;
    /**
     * 图片id
     */
    private Long imageId;
    /**
     * 解密前文件
     */
    private Long beforeDecrypFileId;

    /**
     * 分片大小
     */
    private Integer chunkSize;

    /**
     * 分片数量
     */
    private Integer chunkCount;

    /**
     * 分片上传id
     */
    private String chunkUploadId;

    /**
     * 当前分片
     */
    private Integer currentChunk;

    /**
     * 当前分片是否上传成功
     */
    private Boolean isCurrentChunkSuccess;

    /**
     * 分片是否结束
     */
    private Boolean isEndChunk;


    private Long folderId;


    public UploadModuleDto() {
    }


    public Long getFolderId() {
        return folderId;
    }

    public void setFolderId(Long folderId) {
        this.folderId = folderId;
    }

    public Boolean getImg() {
        return img;
    }

    public void setImg(Boolean img) {
        this.img = img;
    }

    public boolean isDoc() {
        return doc;
    }

    public void setDoc(boolean doc) {
        this.doc = doc;
    }

    public void setDoc() {
        this.doc = (docId != null && docId != 0);
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

    public String getExtName() {
        return extName;
    }

    public void setExtName(String extName) {
        this.extName = extName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUploadUserName() {
        return uploadUserName;
    }

    public void setUploadUserName(String uploadUserName) {
        this.uploadUserName = uploadUserName;
    }

    public Date getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getLoadlink() {
        return loadlink;
    }

    public void setLoadlink(String loadlink) {
        this.loadlink = loadlink;
    }

    public String getSmallImg() {
        return smallImg;
    }

    public void setSmallImg(String smallImg) {
        this.smallImg = smallImg;
    }

    public String getBigImg() {
        return bigImg;
    }

    public void setBigImg(String bigImg) {
        this.bigImg = bigImg;
    }

    public Long getFileid() {
        return fileid;
    }

    public void setFileid(Long fileid) {
        this.fileid = fileid;
    }

    public Long getUploadUser() {
        return uploadUser;
    }

    public void setUploadUser(Long uploadUser) {
        this.uploadUser = uploadUser;
    }

    public Long getDocId() {
        return docId;
    }

    public void setDocId(Long docId) {
        this.docId = docId;
    }

    public Long getRefId() {
        return refId;
    }

    public void setRefId(Long refId) {
        this.refId = refId;
    }

    public String getSignTip() {
        return signTip;
    }

    public void setSignTip(String signTip) {
        this.signTip = signTip;
    }

    public Map<String, Object> getOtherParams() {
        if (otherParams == null) {
            otherParams = new LinkedHashMap<>();
        }
        return otherParams;
    }

    public void setOtherParams(Map<String, Object> otherParams) {
        this.otherParams = otherParams;
    }

    public String getUploadType() {
        return uploadType;
    }

    public void setUploadType(String uploadType) {
        this.uploadType = uploadType;
    }

    public boolean isBan() {
        return ban;
    }

    public void setBan(boolean ban) {
        this.ban = ban;
    }


    public Integer getSecretLevel() {
        return secretLevel;
    }

    public void setSecretLevel(Integer secretLevel) {
        this.secretLevel = secretLevel;
    }

    public String getSecretLevelValidity() {
        return secretLevelValidity;
    }

    public void setSecretLevelValidity(String secretLevelValidity) {
        this.secretLevelValidity = secretLevelValidity;
    }

    public String getValidity() {
        return validity;
    }

    public void setValidity(String validity) {
        this.validity = validity;
    }

    public Boolean isMainAccess() {
        return isMainAccess != null && isMainAccess;
    }

    public void setMainAccess(Boolean mainAccess) {
        isMainAccess = mainAccess;
    }

    public Boolean getMainAccess() {
        return isMainAccess;
    }

    public Boolean getCanSetMainAccess() {
        return canSetMainAccess;
    }

    public void setCanSetMainAccess(Boolean canSetMainAccess) {
        this.canSetMainAccess = canSetMainAccess;
    }

    public Long getMainAccessId() {
        return mainAccessId;
    }

    public void setMainAccessId(Long mainAccessId) {
        this.mainAccessId = mainAccessId;
    }

    public Long getImageId() {
        return imageId;
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }

    public Long getBeforeDecrypFileId() {
        return beforeDecrypFileId;
    }

    public void setBeforeDecrypFileId(Long beforeDecrypFileId) {
        this.beforeDecrypFileId = beforeDecrypFileId;
    }

    public Integer getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(Integer chunkSize) {
        this.chunkSize = chunkSize;
    }

    public Integer getChunkCount() {
        return chunkCount;
    }

    public void setChunkCount(Integer chunkCount) {
        this.chunkCount = chunkCount;
    }

    public String getChunkUploadId() {
        return chunkUploadId;
    }

    public void setChunkUploadId(String chunkUploadId) {
        this.chunkUploadId = chunkUploadId;
    }

    public Integer getCurrentChunk() {
        return currentChunk;
    }

    public void setCurrentChunk(Integer currentChunk) {
        this.currentChunk = currentChunk;
    }

    public Boolean getEndChunk() {
        return isEndChunk;
    }

    public void setEndChunk(Boolean endChunk) {
        isEndChunk = endChunk;
    }

    public Boolean getCurrentChunkSuccess() {
        return isCurrentChunkSuccess;
    }

    public void setCurrentChunkSuccess(Boolean currentChunkSuccess) {
        isCurrentChunkSuccess = currentChunkSuccess;
    }

    @Override
    public String toString() {
        return "UploadModuleDto{" +
                "fileid=" + fileid +
                ", name='" + name + '\'' +
                ", size=" + size +
                ", extName='" + extName + '\'' +
                ", type='" + type + '\'' +
                ", img=" + img +
                ", uploadUser=" + uploadUser +
                ", uploadUserName='" + uploadUserName + '\'' +
                ", uploadTime=" + uploadTime +
                ", doc=" + doc +
                ", docId=" + docId +
                ", version=" + version +
                ", loadlink='" + loadlink + '\'' +
                ", smallImg='" + smallImg + '\'' +
                ", bigImg='" + bigImg + '\'' +
                ", refId=" + refId +
                ", signTip='" + signTip + '\'' +
                ", otherParams=" + otherParams +
                ", uploadType='" + uploadType + '\'' +
                ", ban=" + ban +
                ", secretLevel=" + secretLevel +
                ", secretLevelValidity='" + secretLevelValidity + '\'' +
                ", validity='" + validity + '\'' +
                ", isMainAccess=" + isMainAccess +
                ", canSetMainAccess=" + canSetMainAccess +
                ", mainAccessId=" + mainAccessId +
                ", imageId=" + imageId +
                ", beforeDecrypFileId=" + beforeDecrypFileId +
                ", chunkSize=" + chunkSize +
                ", chunkCount=" + chunkCount +
                ", chunkUploadId='" + chunkUploadId + '\'' +
                ", currentChunk=" + currentChunk +
                ", isCurrentChunkSuccess=" + isCurrentChunkSuccess +
                ", isEndChunk=" + isEndChunk +
                ", folderId=" + folderId +
                '}';
    }
}
