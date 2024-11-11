package com.wuwei.dubboconsumer.controller;

import com.github.kokorin.jaffree.ffmpeg.*;
import com.github.kokorin.jaffree.ffprobe.FFprobe;
import com.github.kokorin.jaffree.ffprobe.FFprobeResult;
import com.google.gson.Gson;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.function.UnaryOperator;

@RequestMapping("/papi/file/videoTranscode")
@RestController
public class VideoTranscodeController {

    private final Logger logger = LoggerFactory.getLogger(VideoTranscodeController.class);

    public String ffmpegPath = "/Users/alphaxx/Downloads/software";

    public String ffprobePath = "/Users/alphaxx/Downloads/software";


    @PostMapping
    public void test(@RequestParam("file") MultipartFile file, @RequestParam("name") String name, HttpServletResponse response) {
        try (SeekableInMemoryByteChannel inputChannel
                     = new SeekableInMemoryByteChannel(IOUtils.toByteArray(file.getInputStream()))) {

            // 打印视频信息
            this.checkVideoMessage(inputChannel);


            this.videoTranscodeByChannel("result.mp4", inputChannel);

        } catch (Exception e) {
            throw new RuntimeException(">>>>>>transcode is failed:", e);
        }
    }

    /**
     * channel方式转码
     */
    private void videoTranscodeByChannel(String fileName, SeekableByteChannel inputChannel) {
        try {
            ChannelInput channelInput = ChannelInput.fromChannel(inputChannel);
            FFmpeg initFfmpeg = this.initVideoTranscode(channelInput);
            SeekableInMemoryByteChannel outPutChannel = new SeekableInMemoryByteChannel();
            ChannelOutput channelOutput = this.assembleMp4ChannelOutput(fileName, outPutChannel);
            executeVideoTranscode(initFfmpeg, this::commonToMp4, channelOutput);
        } catch (Exception e) {
            logger.error(">>>>>>transcode by channel failed:", e);
            throw new RuntimeException(">>>>>>video transcode failed:", e);
        }
    }

    /**
     * 初始化转码
     */
    private FFmpeg initVideoTranscode(Input input) {
        return FFmpeg.atPath(Paths.get(ffmpegPath))
                .addInput(input)
                .addArguments("-preset", "ultrafast");
    }

    /**
     * 组装输出流
     */
    public ChannelOutput assembleMp4ChannelOutput(String fileName, SeekableByteChannel outputChannel) {
        return ChannelOutput.toChannel(fileName, outputChannel)
                .addArguments("-preset", "ultrafast")
                .addArguments("-movflags", "faststart")
                .addArguments("-movflags", "frag_keyframe+empty_moov")
                .addArguments("-loglevel", "error")
                .setFormat("mp4");
    }

    /**
     * 执行转码
     */
    public FFmpegResult executeVideoTranscode(FFmpeg initFfmpeg, UnaryOperator<FFmpeg> ffmpegFunction, Output output) {
        // 开始转码
        logger.info(">>>>>>START CONVERT VIDEO<<<<<<");
        Instant start = Instant.now();
        FFmpegResult executeResult = ffmpegFunction.apply(initFfmpeg)
                .addOutput(output)
                .setOutputListener(new OutputListener() {
                    @Override
                    public void onOutput(String message) {
                        logger.info(">>>>>>calculate message: " + message);
                    }
                })
                .setProgressListener(new ProgressListener() {
                    @Override
                    public void onProgress(FFmpegProgress progress) {
                        logger.info(new Gson().toJson(progress));
                    }
                })
                .execute();

        Instant end = Instant.now();
        long seconds = Duration.between(start, end).toMillis();
        logger.info(">>>>>>FINISH CONVERT VIDEO :{}ms ", seconds);

        return executeResult;
    }

    /**
     * mov 转到 mp4
     */
    private FFmpeg movToMp4(FFmpeg fFmpeg) {
        return fFmpeg
                .addArguments("-c:v", "copy")
                .addArguments("-c:a", "copy");

    }

    /**
     * 一般视频转mp4
     */
    private FFmpeg commonToMp4(FFmpeg fFmpeg) {
        return fFmpeg.addArguments("-c:a", "copy")
                .addArguments("-c:v", "libx264");
    }

    public void checkVideoMessage(SeekableByteChannel seekableByteChannel) {
        com.github.kokorin.jaffree.ffprobe.ChannelInput input = com.github.kokorin.jaffree.ffprobe.ChannelInput.fromChannel(seekableByteChannel);

        FFprobeResult videoMessageResult =
                FFprobe.atPath(Paths.get(ffprobePath))
                        .setShowStreams(true)
                        .setInput(input)
                        .execute();


        for (com.github.kokorin.jaffree.ffprobe.Stream stream : videoMessageResult.getStreams()) {
            logger.info(">>>>>>视频信息<<<<<<");
            logger.info("Stream #" + stream.getIndex()
                    + " type: " + stream.getCodecType()
                    + " codec: " + stream.getCodecLongName()
                    + " duration: " + stream.getDuration() + " seconds");
        }
    }


}
