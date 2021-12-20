package com.wuwei.filestorage.utils;

import com.wuwei.filestorage.constant.FileConstant;
import io.netty.channel.ChannelOption;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.util.concurrent.atomic.AtomicReference;

/**
 * webclient客户端
 *
 * @author wuwei
 * @since 2021/12/14
 */
public class FileWebClient {
    private static final AtomicReference<WebClient> atomWebClient = new AtomicReference<>(null);

    private FileWebClient() {
    }

    public static WebClient getWebClient() {
        WebClient webClient = atomWebClient.get();
        if (null == webClient) {
            HttpClient timeOutClient = HttpClient.create()
                    .tcpConfiguration(client -> client.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30000));

            webClient = WebClient.builder()
                    .clientConnector(new ReactorClientHttpConnector(HttpClient.create().followRedirect(true)))
                    .baseUrl(FileUtils.getHost())
                    .build();

            if (atomWebClient.compareAndSet(null, webClient)) {
                webClient = atomWebClient.get();
            }
        }

        return webClient;
    }

    public static <T> Mono<T> upload(String eteamsId, MultipartBodyBuilder multipartBodyBuilder,
                                     ParameterizedTypeReference<T> typeReference) {
        return FileWebClient.getWebClient()
                .post()
                .uri(FileUtils.getUploadUrl())
                .cookie(FileConstant.ETEAMS_ID, eteamsId)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
                .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                .retrieve()
                .bodyToMono(typeReference);

    }

    public static <T> Mono<T> postFormData(String eteamsId, MultipartBodyBuilder multipartBodyBuilder,
                                           ParameterizedTypeReference<T> typeReference) {
        return FileWebClient.getWebClient()
                .post()
                .uri(FileConstant.CHUNK_UPLOAD_CHECK)
                .cookie(FileConstant.ETEAMS_ID, eteamsId)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                .retrieve()
                .bodyToMono(typeReference);

    }

    public static <T> Mono<T> download(long fileId, String eteamsId, ParameterizedTypeReference<T> typeReference) {
        return FileWebClient.getWebClient()
                .get()
                .uri(FileUtils.getDownloadUrl(fileId))
                .cookie(FileConstant.ETEAMS_ID, eteamsId)
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .retrieve()
                .bodyToMono(typeReference);

    }
}
