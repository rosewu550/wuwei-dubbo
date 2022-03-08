package com.wuwei.watermark.watermarkstream;


import com.wuwei.watermark.entity.WatermarkContentParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Service("fileComponent_imageWatermarkStream")
public class ImgWatermarkStream implements WatermarkStream {

    private static final Logger logger = LoggerFactory.getLogger(ImgWatermarkStream.class);

    @Override
    public InputStream watermarkImage(InputStream imageInputStream, WatermarkContentParam watermarkContentParam) throws IOException {
        Assert.state(imageInputStream != null, "imageInputStream is null");
        Long fileId = watermarkContentParam.getFileId();
        Assert.state(fileId != null, "fileId is inValid");
        Integer opacity = Optional.ofNullable(watermarkContentParam.getOpacity()).orElse(100);
        Integer rotation = Optional.ofNullable(watermarkContentParam.getRotation()).orElse(0);
        InputStream watermarkImageInputStream = this.getWatermarkImage(fileId);
        BufferedImage watermarkImage = ImageIO.read(watermarkImageInputStream);
        watermarkImage = this.resize(watermarkImage, 150, 150);
        // 不透明度
        opacity = opacity > 100 ? 100 : opacity;
        float alpha = opacity / 100.0f;
        // 读取待加水印的图片
        BufferedImage sourceImage = ImageIO.read(imageInputStream);
        Graphics2D g2d = (Graphics2D) sourceImage.getGraphics();
        AlphaComposite alphaChannel = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
        g2d.setComposite(alphaChannel);
        // 旋转角度处理
        double theta = -(rotation / 180.0) * Math.PI;
        // 旋转图片
        AffineTransform originTransform = g2d.getTransform();
        // 水印宽高处理
        double watermarkWidth = watermarkImage.getWidth() + 50.0;
        double watermarkHeight = watermarkImage.getHeight() + 50.0;
        // 平铺水印
        for (int startY = 0; startY < sourceImage.getHeight(); startY += watermarkHeight) {
            for (int startX = 0; startX < sourceImage.getWidth(); startX += watermarkWidth) {
                // 旋转水印
                g2d.rotate(theta, startX, startY);
                g2d.drawImage(watermarkImage, startX, startY, null);
                g2d.setTransform(originTransform);
            }
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(sourceImage, "png", byteArrayOutputStream);
        g2d.dispose();
        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    }

    @Override
    public InputStream watermarkDocFile(InputStream fileInputStream, WatermarkContentParam watermarkContentParam) {
        return null;
    }


    private InputStream getWatermarkImage(Long fileId) {
        Path path = Paths.get("/Volumes/other/下载/picture/image.png");
        try {
            return Files.newInputStream(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private BufferedImage resize(BufferedImage img, int height, int width) {
        Image tmp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return resized;
    }
}
