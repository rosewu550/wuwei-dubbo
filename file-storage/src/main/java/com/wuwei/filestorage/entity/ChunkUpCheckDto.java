package com.wuwei.filestorage.entity;

import com.google.gson.Gson;

public class ChunkUpCheckDto {

    private String check;

    private String fileKey;

    private int nowChunk;

    private String success;

    private String tenantKey;

    public String getCheck() {
        return check;
    }

    public void setCheck(String check) {
        this.check = check;
    }

    public String getFileKey() {
        return fileKey;
    }

    public void setFileKey(String fileKey) {
        this.fileKey = fileKey;
    }

    public int getNowChunk() {
        return nowChunk;
    }

    public void setNowChunk(int nowChunk) {
        this.nowChunk = nowChunk;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getTenantKey() {
        return tenantKey;
    }

    public void setTenantKey(String tenantKey) {
        this.tenantKey = tenantKey;
    }

    @Override
    public String toString() {
        return "ChunkUpCheckDto{" +
                "check=" + check +
                ", fileKey='" + fileKey + '\'' +
                ", nowChunk='" + new Gson().toJson(nowChunk) + '\'' +
                ", success='" + new Gson().toJson(success) + '\'' +
                ", tenantKey='" + new Gson().toJson(tenantKey) + '\'' +
                '}';
    }


}
