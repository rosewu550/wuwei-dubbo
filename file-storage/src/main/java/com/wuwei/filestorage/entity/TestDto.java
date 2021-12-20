package com.wuwei.filestorage.entity;

import com.google.gson.Gson;

/**
 * 请求结果
 *
 * @author wuwei
 * @since 2021/09/13 1m
 */
public class TestDto {

    private int code;

    private boolean status;

    private String message;

    private ChunkUpCheckDto data;


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ChunkUpCheckDto getData() {
        return data;
    }

    public void setData(ChunkUpCheckDto data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "TestDto{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data='" + new Gson().toJson(data) + '\'' +
                '}';
    }
}
