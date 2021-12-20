package com.wuwei.filestorage.service.download;


import com.wuwei.filestorage.entity.DownloadResultDto;
import com.wuwei.filestorage.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.io.*;
import java.util.function.Function;


/**
 * http请求快捷下载（基于webclient）
 *
 * @author wuwei
 * @since 2021/09/01 pm
 */
public class WebClientDownload {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    private Long fileId;

    private String eteamsId;

    private String module;

    private WebClientDownload() {
    }

    public WebClientDownload(Long fileId) {
        this.fileId = fileId;
    }

    public Logger getLogger() {
        return logger;
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public String getEteamsId() {
        return eteamsId;
    }


    public String getModule() {
        return module;
    }


    public WebClientDownload init(String eteamsId, String module) {
        logger.info(">>>>>>webclientDownload is init<<<<<<");
        this.eteamsId = eteamsId;
        this.module = module;
        return this;
    }
//
//    private Mono<DownloadResultDto<T>> download(Class<T> clazz) {
//        logger.info(">>>>>>webClientDownload start download<<<<<<");
//        logger.info(">>>>>>webClientDownload current eteamsId:{}", this.eteamsId);
//        logger.info(">>>>>>webClientDownload current url:{}", FileUtils.getDownloadUrl(this.fileId));
//
//       WebClient.builder()
//                .clientConnector(new ReactorClientHttpConnector(HttpClient.create().followRedirect(true)))
//                .baseUrl(FileUtils.getDownloadUrl(this.fileId))
//                .defaultCookie("ETEAMSID", this.eteamsId)
//                .build()
//                .get()
//                .accept(MediaType.APPLICATION_OCTET_STREAM)
//                .retrieve()
//                .bodyToMono(DownloadResultDto.class)
//                .map((Function<DownloadResultDto<?>, DownloadResultDto<T>>) DownloadResultDto::new);
//    }

    public InputStream blockDownload() {
        logger.info(">>>>>>webClientDownload start download<<<<<<");
        logger.info(">>>>>>webClientDownload current eteamsId:{}", this.eteamsId);
        logger.info(">>>>>>webClientDownload current url:{}", FileUtils.getDownloadUrl(this.fileId));

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create().followRedirect(true)))
                .baseUrl(FileUtils.getDownloadUrl(this.fileId))
                .defaultCookie("ETEAMSID", this.eteamsId)
                .build()
                .get()
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .retrieve()
                .bodyToMono(InputStreamResource.class)
                .filter(InputStreamResource::exists)
                .filter(InputStreamResource::isOpen)
                .map(this::processInputStreamResource)
                .block();
    }

    private InputStream processInputStreamResource(InputStreamResource inputStreamResource) {
        InputStream inputStream;
        try {
            inputStream = inputStreamResource.getInputStream();
        } catch (IOException e) {
            logger.error(">>>>>>获取inputStream失败:", e);
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
