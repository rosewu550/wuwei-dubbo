package com.wuwei.watermark.watermarkstream;


import com.aspose.diagram.Char;
import com.wuwei.watermark.constant.AlignEnum;
import com.wuwei.watermark.entity.WatermarkContentParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

@Service("fileComponent_textWatermarkStream")
public class TextWatermarkStream implements WatermarkStream {

    @Override
    public InputStream watermarkImage(InputStream imageInputStream, WatermarkContentParam watermarkContentParam) throws IOException {
        String fontStr = watermarkContentParam.getFont();
        String colorStr = watermarkContentParam.getColor();
        Integer size = watermarkContentParam.getSize();
        String alignment = watermarkContentParam.getAlignment();
        Integer width = Optional.ofNullable(watermarkContentParam.getWidth()).orElse(0);
        Integer height = Optional.ofNullable(watermarkContentParam.getHeight()).orElse(0);
        Integer opacity = Optional.ofNullable(watermarkContentParam.getOpacity()).orElse(100);
        Integer rotation = Optional.ofNullable(watermarkContentParam.getRotation()).orElse(0);
        String textContent = Optional.ofNullable(watermarkContentParam.getTextContent()).orElse("");

        // 字体处理
        Font font = new Font(fontStr, Font.BOLD, size);
        // 颜色处理,不透明度处理
        if (StringUtils.isBlank(colorStr) || !colorStr.startsWith("#") || colorStr.length() != 7) {
            colorStr = "#000000";
        } else {
            String colorNum = colorStr.substring(1);
            boolean isNumOrChar = colorNum.chars()
                    .allMatch(charTemp -> Character.isUpperCase(charTemp) || Character.isLowerCase(charTemp) || Character.isDigit(charTemp));
            if (!isNumOrChar) {
                colorStr = "#000000";
            }
        }
        opacity = opacity > 100 ? 100 : opacity;
        int alpha = (int) ((opacity / 100.0) * 255 + 0.5);
        Color color = new Color(
                Integer.valueOf(colorStr.substring(1, 3), 16),
                Integer.valueOf(colorStr.substring(3, 5), 16),
                Integer.valueOf(colorStr.substring(5, 7), 16),
                alpha
        );
        // 旋转角度处理
        double theta = -(rotation / 180.0) * Math.PI;
        // 读取待加水印的图片
        BufferedImage sourceImage = ImageIO.read(imageInputStream);
        Graphics2D g2d = (Graphics2D) sourceImage.getGraphics();
        AlphaComposite alphaChannel = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f);
        g2d.setComposite(alphaChannel);
        g2d.setFont(font);
        g2d.setColor(color);
        // 旋转图片
        AffineTransform originTransform = g2d.getTransform();
        FontMetrics fontMetrics = g2d.getFontMetrics();
        Rectangle2D rect = fontMetrics.getStringBounds(textContent, g2d);
        // 水印宽高处理
        double rectWidth = rect.getWidth();
        double rectHeight = rect.getHeight();
        double watermarkWidth = width <= rectWidth ? rectWidth : width;
        double watermarkHeight = height <= rectHeight ? rect.getHeight() : height;
        // 平铺水印
        for (int startY = 0; startY < sourceImage.getHeight(); startY += watermarkHeight) {
            for (int startX = 0; startX < sourceImage.getWidth(); startX += watermarkWidth) {
                // 旋转水印
                g2d.rotate(theta, startX, startY);
                g2d.drawString(textContent, startX, startY);
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

    private String formatAlignment(String alignment, String textContent) {
        AlignEnum align = AlignEnum.getType(alignment);
        return "";
    }

}
