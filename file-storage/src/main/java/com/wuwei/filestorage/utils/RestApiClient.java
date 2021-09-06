package com.wuwei.filestorage.utils;

import com.alibaba.fastjson.JSONValidator;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.fasterxml.jackson.core.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.BufferedImageHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;

import static com.alibaba.fastjson.JSON.parseObject;

/**
 * http请求客户端
 *
 * @author wuwei
 * @since 2021/09/01 pm
 */
public class RestApiClient {

    private static volatile RestTemplate restTemplate = null;

    private static final Logger logger = LoggerFactory.getLogger(RestApiClient.class);


    private RestApiClient() {
    }


    private static RestTemplate buildRestTemplate() {
        if (null == restTemplate) {
            synchronized (RestTemplate.class) {
                if (null == restTemplate) {
                    List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
                    StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
                    messageConverters.add(stringHttpMessageConverter);
                    messageConverters.add(new FormHttpMessageConverter());
                    messageConverters.add(new SourceHttpMessageConverter<>());
                    messageConverters.add(new FastJsonHttpMessageConverter());
                    messageConverters.add(new MarshallingHttpMessageConverter());
                    messageConverters.add(new BufferedImageHttpMessageConverter());

                    RestTemplate buildRestTemplate = new RestTemplateBuilder()
                            .setConnectTimeout(Duration.ofSeconds(5))
                            .rootUri(null)
                            .build();

                    buildRestTemplate.setMessageConverters(messageConverters);
                    restTemplate = buildRestTemplate;
                }
            }
        }
        return restTemplate;
    }


    /**
     * get方式
     */
    public static ResponseEntity<String> getByRest(String url) {
        return buildRestTemplate().getForEntity(url, String.class);
    }


    /**
     * post方式
     */
    public static ResponseEntity<String> postByRest(String url, Object entity) {
        buildRestTemplate().setInterceptors(
                Collections.singletonList(new ClientHttpRequestInterceptor() {
                    @Override
                    public ClientHttpResponse intercept(
                            HttpRequest httpRequest,
                            byte[] body,
                            ClientHttpRequestExecution execution
                    ) throws IOException {
                        List<String> headerList = httpRequest.getHeaders().get(HttpHeaders.COOKIE);
                        if (null != headerList) {
                            headerList.clear();
                        }
                        return execution.execute(httpRequest, body);
                    }
                })
        );
        return buildRestTemplate().postForEntity(url, entity, String.class);
    }

    /**
     * post方式,可设置cookie
     */
    public static ResponseEntity<String> postByRest(String cookies, String url, Object entity) {
        buildRestTemplate().setInterceptors(
                Collections.singletonList(new ClientHttpRequestInterceptor() {
                    @Override
                    public ClientHttpResponse intercept(
                            HttpRequest httpRequest,
                            byte[] body,
                            ClientHttpRequestExecution execution
                    ) throws IOException {
                        httpRequest.getHeaders().add(HttpHeaders.COOKIE, Optional.ofNullable(cookies).orElse(""));
                        return execution.execute(httpRequest, body);
                    }
                })
        );
        return buildRestTemplate().postForEntity(url, entity, String.class);
    }


    /**
     * 解析response
     *
     * @param forEntity    请求得到的response实体
     * @param valueTypeRef response实体要转化成的类型
     */
    public static <T> T analysisByRest(ResponseEntity<String> forEntity, TypeReference<T> valueTypeRef) {
        try {
            logger.info("》》》》》》》》》》》》》》》》》》响应体信息：{}{}",
                    System.getProperty("line.separator"),
                    forEntity.getBody());
            Type type = valueTypeRef.getType();
            String body = forEntity.getBody();
            boolean isJson = JSONValidator.from(body).validate();
            T t;
            if (isJson) {
                t = parseObject(body, type);
            } else {
                t = (T) body;
            }
            return t;
        } catch (Exception e) {
            logger.error("解析出错，信息：", e);
            return null;
        }
    }

    /**
     * 组装上传实体
     */
    public static HttpEntity<MultiValueMap<String, Object>> assembleRequestEntity(MultiValueMap<String, Object> bodyMap) {
        MultiValueMap<String, Object> currentBodyMap
                =
                Optional.ofNullable(bodyMap)
                        .filter(tempMap -> !tempMap.isEmpty())
                        .orElse(new LinkedMultiValueMap<>());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        return new HttpEntity<>(currentBodyMap, headers);
    }


}
