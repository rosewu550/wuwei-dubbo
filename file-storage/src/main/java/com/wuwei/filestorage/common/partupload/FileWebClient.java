package com.wuwei.filestorage.common.partupload;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * webclient客户端
 *
 * @author wuwei
 * @since 2021/12/14
 */
public class FileWebClient {

//    public static final String FILE_HOST = "https://weapp.yunteams.cn";
    public static final String FILE_HOST = "https://www.e-cology.com.cn";


    private static final AtomicReference<WebClient> atomWebClient = new AtomicReference<>(null);

    private FileWebClient() {
    }

    public static WebClient getWebClient() {
        WebClient webClient = atomWebClient.get();
        if (null == webClient) {
            HttpClient timeOutClient = HttpClient.create()
                    .tcpConfiguration(tcpClient -> tcpClient.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 180000))
                    .tcpConfiguration(tcpClient -> tcpClient.doOnConnected(conn ->
                            conn.addHandler(new ReadTimeoutHandler(180, TimeUnit.SECONDS))
                                    .addHandler(new WriteTimeoutHandler(180, TimeUnit.SECONDS))
                    ));

            webClient = WebClient.builder()
                    .clientConnector(new ReactorClientHttpConnector(timeOutClient))
                    .clientConnector(new ReactorClientHttpConnector(HttpClient.create().followRedirect(true)))
                    .baseUrl(FILE_HOST)
                    .build();

            if (atomWebClient.compareAndSet(null, webClient)) {
                webClient = atomWebClient.get();
            }
        }

        return webClient;
    }

    public static <T> Mono<T> upload(String httpUrl, MultipartBodyBuilder multipartBodyBuilder,
                                     ParameterizedTypeReference<T> typeReference) {
        return FileWebClient.getWebClient()
                .post()
                .uri(httpUrl)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
                .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                .retrieve()
                .bodyToMono(typeReference);
    }

    public static <T> Mono<T> postFormData(String httpUrl,
                                           MultipartBodyBuilder multipartBodyBuilder,
                                           ParameterizedTypeReference<T> typeReference) {

        return FileWebClient.getWebClient()
                .post()
                .uri(httpUrl)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                .retrieve()
                .onStatus(HttpStatus::isError, FileWebClient::handleErrorResponse)
                .bodyToMono(typeReference);
    }

    private static Mono<Throwable> handleErrorResponse(ClientResponse response) {
        return response.bodyToMono(String.class).flatMap(errorBody -> Mono.error(new RuntimeException(errorBody)));
    }
}
