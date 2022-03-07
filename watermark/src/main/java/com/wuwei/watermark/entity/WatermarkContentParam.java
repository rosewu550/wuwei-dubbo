package com.wuwei.watermark.entity;


import java.io.Serializable;

/**
 * @author wuwei
 * @since 2022/3/2 pm
 */
public class WatermarkContentParam implements Serializable {

    private Long id;

    private String name;

    private String type;

    private Integer width;

    private Integer height;

    private String font;

    private Integer size;

    private String color;

    private String alignment;

    private Integer opacity;

    private Integer rotation;

    private String textContent;

    private String textField;

    private Long fileId;

    private String module;

    private String tenantKey;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getAlignment() {
        return alignment;
    }

    public void setAlignment(String alignment) {
        this.alignment = alignment;
    }

    public Integer getOpacity() {
        return opacity;
    }

    public void setOpacity(Integer opacity) {
        this.opacity = opacity;
    }

    public Integer getRotation() {
        return rotation;
    }

    public void setRotation(Integer rotation) {
        this.rotation = rotation;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public String getTextField() {
        return textField;
    }

    public void setTextField(String textField) {
        this.textField = textField;
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getTenantKey() {
        return tenantKey;
    }

    public void setTenantKey(String tenantKey) {
        this.tenantKey = tenantKey;
    }

    @Override
    public String toString() {
        return "WatermarkContentParam{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", font='" + font + '\'' +
                ", size=" + size +
                ", color='" + color + '\'' +
                ", alignment='" + alignment + '\'' +
                ", opacity=" + opacity +
                ", rotation=" + rotation +
                ", textContent='" + textContent + '\'' +
                ", textField='" + textField + '\'' +
                ", fileId=" + fileId +
                ", module='" + module + '\'' +
                ", tenantKey='" + tenantKey + '\'' +
                '}';
    }
}
