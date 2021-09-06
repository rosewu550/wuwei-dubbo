package com.wuwei.dubboconsumer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Map;

/**
 * 文档类型配置类
 *
 * @author wuwei
 * @since 2021/08/04 pm
 */


@Configuration
@ConfigurationProperties(prefix = "content")
@PropertySource(value = "classpath:contentTypes.yml", encoding = "utf-8",factory = YamlPropertySourceFactory.class)
public class ContentTypeConfig {
    private Map<String, String> types;

    public Map<String, String> getTypes() {
        return types;
    }

    public void setTypes(Map<String, String> types) {
        this.types = types;
    }
}
