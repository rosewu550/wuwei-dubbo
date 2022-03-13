package com.wuwei.ffmpeg.util;

import com.github.kokorin.jaffree.StreamType;
import com.github.kokorin.jaffree.ffmpeg.*;
import com.github.kokorin.jaffree.ffprobe.FFprobe;
import com.github.kokorin.jaffree.ffprobe.FFprobeResult;
import com.google.gson.Gson;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * ffmpeg 工具类
 *
 * @author wuwei
 * @since 2022/1/11
 */
public class FfmpegUtils {
    private static final Logger logger = LoggerFactory.getLogger(FfmpegUtils.class);

//    public final static String FFMPEG_PATH = "/Users/wuwei/IdeaProjects/wuwei-dubbo/ffmpeg-demo/src/main/resources/ffmpeg/4.4_2/bin";
    public final static String FFMPEG_PATH = "/Volumes/storage/wuwei/neat_donwload/Compressed";

//    public static final String FFMPEG_PATH = "/usr/local/ffmpeg";

    public static final String FFPROBE_PATH = FFMPEG_PATH;


    private FfmpegUtils() {
    }

    public static void getVideoMessage(InputStream inputStream) {
        try (SeekableInMemoryByteChannel inputChannel
                     = new SeekableInMemoryByteChannel(org.apache.commons.compress.utils.IOUtils.toByteArray(inputStream))) {
            com.github.kokorin.jaffree.ffprobe.ChannelInput channelInput = com.github.kokorin.jaffree.ffprobe.ChannelInput.fromChannel(inputChannel);
            // 校验是否支持的视频格式
            boolean canConvert = checkVideoMessage(channelInput);
            Assert.state(canConvert, "不支持的视频格式");
        } catch (Exception e) {
            logger.error("video transcode failed:", e);
        }
    }

    public static boolean checkVideoMessageWithMov(String videoFormat, SeekableByteChannel seekableByteChannel) {
        com.github.kokorin.jaffree.ffprobe.ChannelInput channelInput = com.github.kokorin.jaffree.ffprobe.ChannelInput.fromChannel(seekableByteChannel);
        return "mov".equalsIgnoreCase(videoFormat) || checkVideoMessage(channelInput);
    }

    public static boolean checkVideoMessageWithMov(String videoFormat, String fileUrl) {
        com.github.kokorin.jaffree.ffprobe.UrlInput urlInput = com.github.kokorin.jaffree.ffprobe.UrlInput.fromUrl(fileUrl);
        return "mov".equalsIgnoreCase(videoFormat) || checkVideoMessage(urlInput);
    }

    public static boolean checkVideoMessage(com.github.kokorin.jaffree.ffprobe.Input input) {
        if (input == null) {
            return false;
        }

        FFprobeResult videoMessageResult =
                FFprobe.atPath(Paths.get(FFPROBE_PATH))
                        .setShowStreams(true)
                        .setInput(input)
                        .execute();

        boolean isVideo = false;
        boolean isSupportVideoCodec = false;
        for (com.github.kokorin.jaffree.ffprobe.Stream stream : videoMessageResult.getStreams()) {
            logger.info(">>>>>>{} stream : {}", stream.getCodecType(), new Gson().toJson(stream));
            StreamType codecType = stream.getCodecType();
            if (codecType.equals(StreamType.VIDEO)) {
                isVideo = true;
            }
            String codecLongName = Optional.ofNullable(stream.getCodecLongName()).orElse("");
            if (isVideo && (
                    codecLongName.contains("H.261")
                            || codecLongName.contains("H.262")
                            || codecLongName.contains("H.263")
                            || codecLongName.contains("MPEG-4 part 2")
                            || codecLongName.contains("VP6")
                            || codecLongName.contains("FLV")
                            || codecLongName.contains("H.265 / HEVC")
            )
            ) {
                isSupportVideoCodec = true;
            }

            logger.info("Stream #" + stream.getIndex()
                    + " type: " + stream.getCodecType()
                    + " duration: " + stream.getDuration() + " seconds");
        }

        return isVideo && isSupportVideoCodec;
    }

    /**
     * 计算视频总时长
     */
    public static Long calculateTotalTime(String fileUrl) {
        if (null == fileUrl) {
            return -1L;
        }

        AtomicLong totalTime = new AtomicLong();
        FFmpeg.atPath(Paths.get(FfmpegUtils.FFMPEG_PATH))
                .addInput(UrlInput.fromUrl(fileUrl))
                .addOutput(new NullOutput())
                .setOutputListener(new OutputListener() {
                    @Override
                    public void onOutput(String message) {
                        logger.info(">>>>>>calculate message: " + message);
                    }
                })
                .setProgressListener(new ProgressListener() {
                    @Override
                    public void onProgress(FFmpegProgress progress) {
                        Long timeMillis = progress.getTimeMillis();
                        totalTime.set(timeMillis);
                    }
                }).execute();

        return totalTime.get();
    }


}
