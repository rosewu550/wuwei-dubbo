package com.wuwei.filestorage.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * @author ：liyongfeng
 * @version :
 * @date ：Created in 2021/6/8 14:55
 * @description : 上传接口返回信息封装类
 */
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
    private Map<String,Object> otherParams;

    /**
     * 上传类型：second 秒传；chunk 分片；common 普通上传
     */
    private String uploadType;

    /**
     * 标记，是否是被屏蔽的，默认false
     */
    private boolean ban = false;

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
                '}';
    }
}
