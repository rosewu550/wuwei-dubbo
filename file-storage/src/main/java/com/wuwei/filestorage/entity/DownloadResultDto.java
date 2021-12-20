package com.wuwei.filestorage.entity;



/**
 * 请求结果
 *
 * @author wuwei
 * @since 2021/09/06
 */
public class DownloadResultDto<T> {

    private int code;

    private boolean status;

    private String message;

    private T data;

    public DownloadResultDto(){}

    public DownloadResultDto(DownloadResultDto<?> downloadResultDto,Class<?> clazz){
       this.code = downloadResultDto.getCode();
       this.status = downloadResultDto.isStatus();
       this.message = downloadResultDto.getMessage();
        Object objData = downloadResultDto.getData();

        this.data = (T) objData;
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
