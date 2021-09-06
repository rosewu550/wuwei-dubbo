package com.wuwei.filestorage.utils;

import org.springframework.web.reactive.function.client.WebClient;

/**
 * webClient 工具类
 *
 * @author wuwei
 * @since 2021/09/07 am
 */
public class WebClientUtil {


    public void test(){
        WebClient.builder()
                .baseUrl("")
                .defaultCookie("test","test")

    }



}
