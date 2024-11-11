package com.wuwei.dubboconsumer.utils;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.DefaultUriBuilderFactory;
import reactor.netty.http.client.HttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

public class WebClientUtils {
    private WebClientUtils() {
    }

    private static final Logger logger = LoggerFactory.getLogger(WebClientUtils.class);


    private static final ParameterizedTypeReference<InputStreamResource> typeReference =
            new ParameterizedTypeReference<InputStreamResource>() {
            };

    public static WebClient getWebClient(String url, String param) throws UnsupportedEncodingException {
        HttpClient timeOutClient = HttpClient.create()
                .tcpConfiguration(tcpClient -> tcpClient.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 180000))
                .tcpConfiguration(tcpClient -> tcpClient.doOnConnected(conn ->
                        conn.addHandler(new ReadTimeoutHandler(180, TimeUnit.SECONDS))
                                .addHandler(new WriteTimeoutHandler(180, TimeUnit.SECONDS))
                ));

        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(
                url + "?" + param);
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(timeOutClient))
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create().followRedirect(true)))
                .uriBuilderFactory(factory)
                .build();
    }

    public static InputStream processInputStreamResource(InputStreamResource inputStreamResource) {
        InputStream inputStream;
        try {
            inputStream = inputStreamResource.getInputStream();
        } catch (IOException e) {
            inputStream = new InputStream() {
                @Override
                public int read() throws IOException {
                    return 0;
                }
            };
        }
        return inputStream;
    }
}
