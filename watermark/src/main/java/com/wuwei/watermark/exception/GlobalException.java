package com.wuwei.watermark.exception;


public class GlobalException extends RuntimeException {

    protected Integer code;

    protected String information;

    public GlobalException() {
        this.code = 500;
        this.information = "failed";
    }

    public GlobalException(String information) {
        super(information);
        init(500, information);
    }

    public GlobalException(Integer code, String information) {
        super(information);
        init(code, information);
    }

    public GlobalException(Integer code, String information, String message) {
        super(message);
        init(code, information);
    }

    private void init(Integer code, String information) {
        this.code = code;
        this.information = information;
    }

    public Integer code() {
        return code;
    }

    public void code(Integer code) {
        this.code = code;
    }

    public String information() {
        return information;
    }

    public void information(String information) {
        this.information = information;
    }
}
