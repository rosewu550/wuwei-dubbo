package com.wuwei.filestorage.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 *
 * 普通类获取bean工具类
 *
 * @author wuwei
 * @since 2021/09/06 am
 */
public class SpringContextUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;
    private static final Logger logger = LoggerFactory.getLogger(SpringContextUtil.class);

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextUtil.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        if (applicationContext == null) {
            throw new IllegalStateException("applicaitonContext not defined!");
        } else {
            return applicationContext;
        }
    }

    public static <T> T getBean(String name) {
        logger.debug("getBean:{}", name);
        return (T)applicationContext.getBean(name);
    }

    public static <T> T getBean(Class<T> requiredType) {
        return applicationContext.getBean(requiredType);
    }


}
