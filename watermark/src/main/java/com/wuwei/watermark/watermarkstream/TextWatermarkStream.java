package com.wuwei.watermark.watermarkstream;


import com.wuwei.watermark.constant.AlignEnum;
import com.wuwei.watermark.entity.LineParam;
import com.wuwei.watermark.entity.WatermarkContentParam;
import com.wuwei.watermark.utils.WatermarkUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service("fileComponent_textWatermarkStream")
public class TextWatermarkStream implements WatermarkStream {

    @Override
    public InputStream watermarkImage(InputStream imageInputStream, WatermarkContentParam watermarkContentParam) throws IOException {
        Integer size = watermarkContentParam.getSize();
        String fontStr = watermarkContentParam.getFont();
        String colorStr = watermarkContentParam.getColor();
        String alignment = Optional.ofNullable(watermarkContentParam.getAlignment()).orElse("left");
        AlignEnum align = AlignEnum.getType(alignment);
        Integer width = Optional.ofNullable(watermarkContentParam.getWidth()).orElse(0);
        Integer height = Optional.ofNullable(watermarkContentParam.getHeight()).orElse(0);
        Integer opacity = Optional.ofNullable(watermarkContentParam.getOpacity()).orElse(100);
        Integer rotation = Optional.ofNullable(watermarkContentParam.getRotation()).orElse(0);
        String textContent = Optional.ofNullable(watermarkContentParam.getTextContent()).orElse("");

        // 字体处理
        boolean isSupportFont = WatermarkUtils.validateFont(fontStr);
        Font font = new Font(fontStr, Font.PLAIN, size);
        // 颜色处理,不透明度处理
        if (StringUtils.isEmpty(colorStr) || !colorStr.startsWith("#") || colorStr.length() != 7) {
            colorStr = "#000000";
        } else {
            String colorNum = colorStr.substring(1);
            boolean isNumOrChar = colorNum.chars()
                    .allMatch(charTemp -> Character.isUpperCase(charTemp) || Character.isLowerCase(charTemp) || Character.isDigit(charTemp));
            if (!isNumOrChar) {
                colorStr = "#000000";
            }
        }
        Color color = new Color(
                Integer.valueOf(colorStr.substring(1, 3), 16),
                Integer.valueOf(colorStr.substring(3, 5), 16),
                Integer.valueOf(colorStr.substring(5, 7), 16)
        );
        // 旋转角度处理
        double theta = -(rotation / 180.0) * Math.PI;
        // 不透明度
        opacity = opacity > 100 ? 100 : opacity;
        float alpha = opacity / 100.0f;
        // 读取待加水印的图片
        BufferedImage sourceImage = ImageIO.read(imageInputStream);
        Graphics2D g2d = (Graphics2D) sourceImage.getGraphics();
        AlphaComposite alphaChannel = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
        g2d.setComposite(alphaChannel);
        g2d.setFont(font);
        g2d.setColor(color);
        // 水印内容处理（<p>标签换行）
        java.util.List<LineParam> lineParamList = this.processText(g2d, textContent);
        List<Double> widthList = lineParamList.stream().map(LineParam::getWidth).sorted().collect(toList());
        Double maxWidth = widthList.get(widthList.size() - 1);
        Double totalHeight = lineParamList.stream().map(LineParam::getHeight).reduce(Double::sum).orElse(0.0);
        // 水印宽高处理
        double watermarkWidth = width <= maxWidth ? maxWidth : Double.valueOf(width);
        double watermarkHeight = height <= totalHeight ? totalHeight : Double.valueOf(height);
        // 旋转图片
        AffineTransform originTransform = g2d.getTransform();
        // 平铺水印
        for (int startY = 0; startY < sourceImage.getHeight(); startY += watermarkHeight) {
            for (int startX = 0; startX < sourceImage.getWidth(); startX += watermarkWidth) {
                // 旋转水印
                g2d.rotate(theta, startX, startY);
                this.drawStringLine(g2d, align, lineParamList, startX, startY);
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

    private List<LineParam> processText(Graphics g, String text) {
        String[] lineStrArray = text.split("</p>");
        FontMetrics fontMetrics = g.getFontMetrics();
        return Arrays.stream(lineStrArray)
                .map(lineStr -> {
                    // 去除<p>标签
                    lineStr = lineStr.replace("<p>", "");
                    lineStr = lineStr.replace("<span contenteditable=\"false\">", "");
                    lineStr = lineStr.replace("</span>", "");
                    Rectangle2D rect = fontMetrics.getStringBounds(lineStr, g);
                    double currentLineWidth = rect.getWidth();
                    double currentLineHeight = rect.getHeight();
                    LineParam lineParam = new LineParam();
                    lineParam.setLine(lineStr);
                    lineParam.setWidth(currentLineWidth);
                    lineParam.setHeight(currentLineHeight);
                    return lineParam;
                })
                .collect(toList());
    }

    /**
     * 根据对齐方式输出文字水印
     */
    private void drawStringLine(Graphics g, AlignEnum align, List<LineParam> lineParamList, int x, int y) {
        for (int index = 0; index < lineParamList.size(); index++) {
            LineParam lineParam = lineParamList.get(index);
            String line = lineParam.getLine();
            double width = lineParam.getWidth();

            if (index == 0) {
                g.drawString(line, x, y);
            } else {
                LineParam preLineParam = lineParamList.get(index - 1);
                Double preWidth = preLineParam.getWidth();
                Double preHeight = preLineParam.getHeight();
                int alignmentOffset = this.getAlignmentOffset(align, preWidth, width);
                x += alignmentOffset;
                y += preHeight;
                g.drawString(line, x, y);
            }
        }
    }

    /**
     * 根据对齐方式获取偏移量
     */
    private int getAlignmentOffset(AlignEnum align, double originTextWidth, double currentLineWidth) {
        int offset;
        switch (align) {
            case right:
                offset = (int) (originTextWidth - currentLineWidth);
                break;
            case center:
                offset = (int) (originTextWidth - currentLineWidth) / 2;
                break;
            default:
                // 默认向左对齐
                offset = 0;
        }

        return offset;
    }

}
