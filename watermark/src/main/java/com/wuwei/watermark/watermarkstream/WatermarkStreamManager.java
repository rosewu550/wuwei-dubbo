package com.wuwei.watermark.watermarkstream;

import com.wuwei.watermark.constant.WatermarkType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WatermarkStreamManager {

    private static final Logger logger = LoggerFactory.getLogger(WatermarkStreamManager.class);

    private final Map<String, WatermarkStream> watermarkStreamInstanceMap = new ConcurrentHashMap<>();

    @Autowired
    public WatermarkStreamManager(Map<String, WatermarkStream> watermarkStreamInstanceMap) {
        this.watermarkStreamInstanceMap.clear();
        this.watermarkStreamInstanceMap.putAll(watermarkStreamInstanceMap);
    }

    public WatermarkStream getWatermark(String type) {
        WatermarkType watermarkType = WatermarkType.getType(type);
        logger.info(">>>>>>watermark type :{}", type);
        Assert.state(watermarkType != WatermarkType.NONE, "watermark type is not support");
        return this.watermarkStreamInstanceMap.get(watermarkType.getStreamServiceName());
    }


}
