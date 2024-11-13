package com.wuwei.filestorage.common.partupload;

/**
 * 请求结果
 *
 * @author wuwei
 * @since 2021/09/13 1m
 */
public class ResultDto<T> {

    private int code;

    private boolean status;

    private String message;

    private String detailErrorMessage;

    private T data;

    public String getDetailErrorMessage() {
        return detailErrorMessage;
    }

    public void setDetailErrorMessage(String detailErrorMessage) {
        this.detailErrorMessage = detailErrorMessage;
    }

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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
