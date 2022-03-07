package com.wuwei.watermark.watermarkstream;


import com.wuwei.watermark.entity.WatermarkContentParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service("fileComponent_imageWatermarkStream")
public class ImgWatermarkStream implements WatermarkStream {

    private static final Logger logger = LoggerFactory.getLogger(ImgWatermarkStream.class);


    @Override
    public InputStream watermarkImage(InputStream fileInputStream, WatermarkContentParam watermarkContentParam) {
        return null;
    }

    @Override
    public InputStream watermarkDocFile(InputStream fileInputStream, WatermarkContentParam watermarkContentParam) {
        return null;
    }
}
