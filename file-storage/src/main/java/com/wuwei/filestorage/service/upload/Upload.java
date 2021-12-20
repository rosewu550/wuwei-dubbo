package com.wuwei.filestorage.service.upload;


import com.wuwei.filestorage.constant.FileConstant;
import com.wuwei.filestorage.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Upload {

    public final Logger logger = LoggerFactory.getLogger(this.getClass());


    public String eteamsId;

    /**
     * 模块id
     */
    public String module;

    /**
     * 文件大小
     */
    public long size;

    /**
     * 文件名
     */
    public String name;

    /**
     * 文件最后修改时间
     */
    public String lastModified;


    public String lastModifiedDate;


    /**
     * 文件来源id
     */
    public long refId;

    /**
     * 分片个数
     */
    public int chunks;

    /**
     * 当前所传分片编号，起始值为0
     */
    public int chunk;

    /**
     * 水印信息
     */
    public String waterParamStr;

    /**
     * 省市位置，用于水印的支持
     */
    public String position;

    /**
     * 文件MD5值，用于秒传
     */
    public String md5;

    /**
     * 目录id
     */
    public long folderId;

    /**
     * 目录类型
     */
    public String folderType;

    /**
     * 获取上传地址
     */
    public String getUploadUrl() {
        return FileUtils.getUploadUrl();
    }

}
