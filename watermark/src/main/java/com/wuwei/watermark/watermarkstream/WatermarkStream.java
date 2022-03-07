package com.wuwei.watermark.watermarkstream;

import com.wuwei.watermark.entity.WatermarkContentParam;

import java.io.IOException;
import java.io.InputStream;

public interface WatermarkStream {

    InputStream watermarkImage(InputStream imageInputStream, WatermarkContentParam watermarkContentParam) throws IOException;

    InputStream watermarkDocFile(InputStream fileInputStream, WatermarkContentParam watermarkContentParam);

}
