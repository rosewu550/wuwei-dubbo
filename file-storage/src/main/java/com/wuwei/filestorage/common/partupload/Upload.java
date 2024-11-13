package com.wuwei.filestorage.common.partupload;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.function.Predicate;

public abstract class Upload {

    public static final Logger logger = LoggerFactory.getLogger(Upload.class);

    public static final String PAPI_UPLOAD_ENDPOINT = "/papi/file/module/upload";

    public static final String PAPI_CHUNK_UPLOAD_CHECK_ENDPOINT = "/papi/file/chunkUpCheck";


    public String tenantKey;

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

    public boolean createDoc = true;

    /**
     * 对上传体的处理
     */
    public Predicate<? super Map.Entry<Object, Object>> filterUploadBodyMap = this::filterUploadBodyMap;

    private boolean filterUploadBodyMap(Map.Entry<Object, Object> entry) {
        boolean isSaveParam;
        Object value = entry.getValue();
        if (null == value) {
            isSaveParam = false;
        } else if (value instanceof Integer) {
            isSaveParam = 0 != (int) value;
        } else if (value instanceof Long) {
            isSaveParam = 0 != (long) value;
        } else {
            isSaveParam = true;
        }
        return isSaveParam;
    }
}
