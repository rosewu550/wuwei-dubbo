package com.wuwei.watermark.constant;

import java.util.Arrays;

public enum WatermarkType {

    TEXT("text", "文字", "fileComponent_textWatermarkFront", "fileComponent_textWatermarkContent","fileComponent_textWatermarkStream"),
    IMAGE("image", "图片", "fileComponent_imageWatermarkFront", "fileComponent_imageWatermarkContent","fileComponent_imageWatermarkStream"),
    NONE("", "", "", "","");


    WatermarkType(String ename, String cname, String frontServiceName, String serviceName, String streamServiceName) {
        this.ename = ename;
        this.cname = cname;
        this.serviceName = serviceName;
        this.frontServiceName = frontServiceName;
        this.streamServiceName = streamServiceName;
    }

    private final String ename;

    private final String cname;

    private final String serviceName;

    private final String frontServiceName;

    private final String streamServiceName;

    public String getEname() {
        return ename;
    }

    public String getCname() {
        return cname;
    }

    public String getFrontServiceName() {
        return frontServiceName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getStreamServiceName() {
        return streamServiceName;
    }

    public static WatermarkType getType(String ename) {
        return Arrays.stream(WatermarkType.values())
                .filter(watermarkTypeTemp -> watermarkTypeTemp.ename.equals(ename))
                .findFirst()
                .orElse(WatermarkType.NONE);
    }

}
