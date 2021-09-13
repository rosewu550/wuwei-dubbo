package com.wuwei.filestorage.entity;

/**
 * 请求结果
 *
 * @author wuwei
 * @since 2021/09/06
 */
public class ResultDto {

    private int code;

    private boolean status;

    private String message;

    private UploadModuleDto data;


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

    public UploadModuleDto getData() {
        return data;
    }

    public void setData(UploadModuleDto data) {
        this.data = data;
    }
}
