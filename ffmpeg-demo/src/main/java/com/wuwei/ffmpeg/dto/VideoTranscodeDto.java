package com.wuwei.ffmpeg.dto;

/**
 * 视频转码类
 *
 * @author wuwei
 * @since 2022/1/13 pm
 */
public class VideoTranscodeDto {

    /**
     * 是否转码
     */
    private Boolean hasTranscode;

    /**
     * 转码后新文件名称
     */
    private String videoName;
    /**
     * 转码后文件地址
     */
    private String videoUrl;

    /**
     * 存储原始url
     */
    private String storageUrl;

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getStorageUrl() {
        return storageUrl;
    }

    public void setStorageUrl(String storageUrl) {
        this.storageUrl = storageUrl;
    }

    public Boolean getHasTranscode() {
        return hasTranscode;
    }

    public void setHasTranscode(Boolean hasTranscode) {
        this.hasTranscode = hasTranscode;
    }

    @Override
    public String toString() {
        return "VideoTranscodeDto{" +
                "videoName='" + videoName + '\'' +
                "videoUrl='" + videoUrl + '\'' +
                "storageUrl='" + storageUrl + '\'' +
                "hasTranscode='" + hasTranscode + '\'' +
                '}';
    }
}
