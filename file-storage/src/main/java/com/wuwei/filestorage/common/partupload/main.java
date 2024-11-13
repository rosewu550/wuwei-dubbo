package com.wuwei.filestorage.common.partupload;

import java.io.File;
import java.util.Date;

/**
 * 描述：
 *
 * @author alphaxx
 * @since 2024/11/12 15:44
 */
public class main {

    public static void main(String[] args) throws Exception {
        File file = new File("/Users/alphaxx/Downloads/dev-sys.log");
        String filename = file.getName();

        // 根据不同环境修改com.wuwei.filestorage.common.partupload.FileWebClient.FILE_HOST的值
        WebClientChunkUpload webClientChunkUpload = new WebClientChunkUpload(file);
        ResultDto<UploadModuleDto> resultDto = webClientChunkUpload
                .init("all_teams", filename, "document", file.length(), new Date().getTime()+"", new Date().getTime()+"")
                .addChunkSize(5 * 1024 * 1024) // 自定义分片大小，不设置默认4m,4*1024*1024
                .start();

        System.out.println(resultDto);
    }

}
