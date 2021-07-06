package com.wuwei.dubboConsumer.controller;


import com.wuwei.dubboApi.service.DownloadDemoService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;

@RestController
public class DownloadController {

    @Reference(group = "hessian")
    private DownloadDemoService downloadDemoService;

    @Reference(group = "dubbo")
    private DownloadDemoService dubboDownloadService;

    @PostMapping("/downloadInputStream")
    public InputStream downloadFile() {
        return downloadDemoService.downloadDocument();
    }

    @PostMapping("/dubbo/downloadInputStream")
    public InputStream downloadFileByDubbo() {
        return dubboDownloadService.downloadDocument();
    }

}
