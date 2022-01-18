package com.wuwei.ffmpeg.stream;

import com.github.kokorin.jaffree.StreamType;
import com.github.kokorin.jaffree.ffmpeg.*;
import com.google.gson.Gson;
import com.wuwei.ffmpeg.dto.MediaObjectInfo;
import com.wuwei.ffmpeg.util.FfmpegUtils;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.*;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.function.UnaryOperator;


/**
 * @author ：liyongfeng
 * @version :
 * @date ：Created in 2021/8/2 14:56
 * @description : 文件编码转换处理接口实现
 */
@Component
public class FileStreamConversionImpl implements FileStreamConversion {

    private final Logger logger = LoggerFactory.getLogger(FileStreamConversionImpl.class);


    /**
     * mp3 的编码
     */
    private static final String MP3_CODE = "libmp3lame";
    /**
     * 为新的重新编码的视频流设置比特率值。如果未设置比特率值，则编码器将选择默认值。该值应以每秒位数表示。例如，如果您想要360 kb / s的比特率，则应调用setBitRate（new Integer（360000）
     */
    private static final Integer bitRate = 360000;
    /**
     * 设置将在重新编码的音频流中使用的音频通道数（1 =单声道，2 =立体声）。如果未设置任何通道值，则编码器将选择默认值。
     */
    private static final Integer channels = 2;
    /**
     * 为新的重新编码的音频流设置采样率。如果未设置采样率值，则编码器将选择默认值。该值应以赫兹表示。例如，如果您想要类似CD的44100 Hz采样率，则应调用setSamplingRate（new Integer（44100）
     */
    private static final Integer samplingRate = 44100;
    /**
     * 为新的重新编码的音频流设置帧速率值。如果未设置比特率帧速率，则编码器将选择默认值。该值应以每秒帧数表示。例如，如果您想要30 f / s的帧速率，则应调用setFrameRate（new Integer（30）
     */
    private static final Integer frameRate = 15;


    @Override
    public void videoToMp4ByFfmpeg(String videoFormat, InputStream inputStream, OutputStream outputStream) {
        // 临时文件路径
        String temporaryPathStr = System.getProperty("user.dir") + File.separator + "VIDEO_TRANSCODE" + File.separator + UUID.randomUUID();
        MediaObjectInfo mediaObjectInfo = this.writeTemporaryFile(inputStream, temporaryPathStr);
        String temporaryPath = mediaObjectInfo.getPath();
        this.videoToMp4ByFfmpeg(videoFormat, temporaryPath, outputStream);
        // 删除临时文件
        mediaObjectInfo.deleteFile();
    }

    @Override
    public void videoToMp4ByFfmpeg(String videoFormat, String fileUrl, OutputStream outputStream) {
        if (StringUtils.isBlank(videoFormat) || StringUtils.isBlank(fileUrl) || outputStream == null) {
            return;
        }
        // 校验是否支持的视频格式
        boolean canConvert = FfmpegUtils.checkVideoMessageWithMov(videoFormat, fileUrl);
        Assert.state(canConvert, "不支持的视频格式");

        // 开始转码
        long startTime = System.currentTimeMillis();
        this.videoTranscode(UrlInput.fromUrl(fileUrl), videoFormat, outputStream);
        long endTime = System.currentTimeMillis();
        logger.info(">>>>>>FINISH CONVERT VIDEO :{}ms ", endTime - startTime);
    }

    @Override
    public void videoToMp4ByChannel(String outputFileName, String videoFormat, InputStream inputStream, OutputStream outputStream) {
        if (StringUtils.isBlank(videoFormat) || inputStream == null || outputStream == null) {
            return;
        }

        try (SeekableInMemoryByteChannel inputChannel
                     = new SeekableInMemoryByteChannel(org.apache.commons.compress.utils.IOUtils.toByteArray(inputStream))) {
            // 校验是否支持的视频格式
            boolean canConvert = FfmpegUtils.checkVideoMessageWithMov(videoFormat, inputChannel);
            Assert.state(canConvert, "不支持的视频格式");

            // 开始转码
            long startTime = System.currentTimeMillis();
            this.videoTranscodeByChannel(outputFileName, videoFormat, inputChannel, outputStream);
            long endTime = System.currentTimeMillis();
            logger.info(">>>>>>FINISH CONVERT VIDEO :{}ms ", endTime - startTime);

        } catch (Exception e) {
            logger.error("video transcode failed:", e);
        }
    }


    /**
     * 针对不同格式的转码
     */
    private void videoTranscode(Input input, String videoFormat, OutputStream outputStream) {
        FFmpeg initFfmpeg = this.initVideoTranscode(input);
        Output pipeOutput = assembleMp4PipOutput(outputStream);
        if ("mov".equalsIgnoreCase(videoFormat)) {
            executeVideoTranscode(initFfmpeg, this::movToMp4, pipeOutput);
        } else {
            executeVideoTranscode(initFfmpeg, this::commonToMp4, pipeOutput);
        }
    }

    /**
     * 初始化转码
     */
    private FFmpeg initVideoTranscode(Input input) {
        return FFmpeg.atPath(Paths.get(FfmpegUtils.FFMPEG_PATH))
                .addInput(input)
                .addArguments("-preset", "ultrafast");
    }

    /**
     * 执行转码
     */
    public FFmpegResult executeVideoTranscode(FFmpeg initFfmpeg, UnaryOperator<FFmpeg> ffmpegFunction, Output output) {
        FFmpegResult executeResult = ffmpegFunction.apply(initFfmpeg)
                .addOutput(output)
                .execute();

        // todo gson工具类替换点
        Gson gson = new Gson();
        String executeResultStr = gson.toJson(executeResult);
        logger.info(">>>>>>convert video result:{}", executeResultStr);

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

    /**
     * 组装输出流
     */
    public PipeOutput assembleMp4PipOutput(OutputStream outputStream) {
        return PipeOutput.pumpTo(outputStream)
                .addArguments("-preset", "ultrafast")
                .addArguments("-movflags", "faststart")
                .addArguments("-movflags", "frag_keyframe+empty_moov")
                .setFormat("mp4");
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

    public UrlOutput assembleMp4UrlOutput(String pathStr) {
        return UrlOutput.toUrl(pathStr)
                .addArguments("-preset", "ultrafast")
                .addArguments("-movflags", "faststart")
                .addArguments("-movflags", "frag_keyframe+empty_moov")
                .setCodec(StreamType.VIDEO, "libx264")
                .setFormat("mp4");
    }


    /**
     * 将源文件写入到临时文件路径下
     *
     * @param inputStream   原文件流
     * @param temporaryPath 临时文件路径
     */
    @Override
    public MediaObjectInfo writeTemporaryFile(InputStream inputStream, String temporaryPath) {
        logger.info("-------->> 开始将文件流写入指定路径：temporaryPath={}", temporaryPath);
        if (inputStream == null || StringUtils.isBlank(temporaryPath)) {
            throw new IllegalArgumentException("文件处理失败，请重试");
        }
        try {
            //文件夹路径
            String directoryPath = temporaryPath.substring(0, temporaryPath.lastIndexOf(File.separator));
            File directory = new File(directoryPath);
            if (!directory.exists()) {
                boolean md = directory.mkdirs();
                if (!md) {
                    throw new IllegalArgumentException("文件处理失败，请重试");
                }
            }
            FileUtils.copyInputStreamToFile(inputStream, new File(temporaryPath));
        } catch (Exception e) {
            logger.error("将源文件写入临时文件路径失败 temporaryPath={}", temporaryPath, e);
            throw new IllegalArgumentException("文件处理失败，请重试");
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                logger.error("关闭文件流失败");
            }

        }
        return new MediaObjectInfo(temporaryPath);
    }

    /**
     * 删除路径上的临时文件
     *
     * @param path 路径
     */
    @Override
    public void deleteTemporaryFile(String path) {
        File tif = new File(path);
        if (tif.exists()) {
            boolean de = tif.delete();
            if (!de) {
                logger.error("删除临时文件失败，再次进行重试");
                boolean de2 = tif.delete();
                if (!de2) {
                    logger.error("删除临时文件失败!,暂不进行处理，sourcePath={}", path);
                }
            }
        }
    }


    /**
     * channel方式转码
     */
    public void videoTranscodeByChannel(String fileName, String videoFormat, InputStream inputStream, OutputStream outputStream) {
        try (SeekableInMemoryByteChannel channel = new SeekableInMemoryByteChannel
                (IOUtils.toByteArray(inputStream));
        ) {
            this.videoTranscodeByChannel(fileName, videoFormat, channel, outputStream);
        } catch (Exception e) {
            logger.error(">>>>>>transcode by channel failed:", e);
        }

    }

    /**
     * channel方式转码
     */
    public void videoTranscodeByChannel(String fileName, String videoFormat, SeekableByteChannel inputChannel, OutputStream outputStream) {
        try {
            ChannelInput channelInput = ChannelInput.fromChannel(inputChannel);
            FFmpeg initFfmpeg = this.initVideoTranscode(channelInput);
            SeekableInMemoryByteChannel outPutChannel = new SeekableInMemoryByteChannel();
            ChannelOutput channelOutput = this.assembleMp4ChannelOutput(fileName, outPutChannel);
            long startTime = System.currentTimeMillis();
            if ("mov".equalsIgnoreCase(videoFormat)) {
                executeVideoTranscode(initFfmpeg, this::movToMp4, channelOutput);
            } else {
                executeVideoTranscode(initFfmpeg, this::commonToMp4, channelOutput);
            }
            long endTime = System.currentTimeMillis();
            logger.info(">>>>>>transcode use time : {}ms", endTime - startTime);

            byte[] array = outPutChannel.array();
            outputStream.write(array);
        } catch (Exception e) {
            logger.error(">>>>>>transcode by channel failed:", e);
        }

    }
}
