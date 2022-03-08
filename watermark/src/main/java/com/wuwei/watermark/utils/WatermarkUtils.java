package com.wuwei.watermark.utils;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class WatermarkUtils {
    private WatermarkUtils (){}


    /**
     * 获取自定义字体
     * @param filepath 字体文件路径
     * @param style 字体样式
     * @param size 字体大小
     * @return
     */
    public static Font getSelfDefinedFont(String filepath, int style, float size){
        String fonts[] =
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        Font font = null;
        File file = new File(filepath);
        try{
            font = Font.createFont(java.awt.Font.TRUETYPE_FONT, file);
            font = font.deriveFont(style, size);
        }catch (FontFormatException e){
            return null;
        }catch (FileNotFoundException e){
            return null;
        }catch (IOException e){
            return null;
        }
        return font;
    }

}
