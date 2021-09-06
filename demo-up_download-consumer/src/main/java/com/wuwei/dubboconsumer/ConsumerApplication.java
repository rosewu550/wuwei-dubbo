package com.wuwei.dubboconsumer;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import com.wuwei.filestorage.utils.SpringContextUtil;


@SpringBootApplication(scanBasePackages = "com.wuwei")
@EnableDubbo
public class ConsumerApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(ConsumerApplication.class);
        SpringContextUtil springContextUtil = new SpringContextUtil();
        springContextUtil.setApplicationContext(run);
    }

    /**
     * 打成war包才可在tomcat下使用
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(ConsumerApplication.class);
    }
}
