package com.wuwei.filestorage.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Map;


/**
 * 动态获取host，以配置文件的方式
 *
 * @author wuwei
 * @since 2021/09/01 pm
 */
@Configuration
@PropertySource(value = "classpath:file.properties")
@PropertySource(value = "classpath:file-${spring.profiles.active}.properties", ignoreResourceNotFound = true)
@ConfigurationProperties(prefix = "file")
public class FileProperties {
    private String host;

    private Map<String, String> preview;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Map<String, String> getPreview() {
        return preview;
    }

    public void setPreview(Map<String, String> preview) {
        this.preview = preview;
    }
}
