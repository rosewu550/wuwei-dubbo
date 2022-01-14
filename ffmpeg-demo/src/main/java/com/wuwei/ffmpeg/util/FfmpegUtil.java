package com.wuwei.ffmpeg.util;

import com.github.kokorin.jaffree.StreamType;
import com.github.kokorin.jaffree.ffmpeg.*;
import com.github.kokorin.jaffree.ffprobe.FFprobe;
import com.github.kokorin.jaffree.ffprobe.FFprobeResult;
import com.wuwei.ffmpeg.constant.FfmpegConstant;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicLong;

public class FfmpegUtil {
    public static FFmpeg fFmpeg;
    public static FFprobe fFprobe;

    static {
        Path ffmpegPath = Paths.get(FfmpegConstant.FFMPEG);
        Path ffprobePath = Paths.get(FfmpegConstant.FFPROBE);
        fFmpeg = FFmpeg.atPath(ffmpegPath);
        fFprobe = FFprobe.atPath(ffprobePath);
    }


    /**
     * 转码程h264 mp4
     */
    public static void convertToMp4(String fileUrl, OutputStream outputStream) {
        if (null == fileUrl) {
            return;
        }

        boolean canConvert = checkVideoMessage(fileUrl);
        if (!canConvert) {
            return;
        }

        Long totalTime = calculateTotalTime(fileUrl);


        fFmpeg
                .addInput(UrlInput.fromUrl(fileUrl))
                .setOverwriteOutput(true)
                .addArguments("-movflags", "faststart")
                .addOutput(
                        PipeOutput.pumpTo(outputStream)
                                .addArguments("-movflags", "frag_keyframe+empty_moov")
                                .setCodec(StreamType.VIDEO, "libx264")
                                .setFormat("mp4")
                )
                .setProgressListener(new ProgressListener() {
                    @Override
                    public void onProgress(FFmpegProgress progress) {
                        System.out.println(">>>>>>current progress：" + (progress.getTimeMillis() * 100.0 / totalTime) + "%");
                    }
                })
                .execute();
    }

    public static boolean checkVideoMessage(String fileUrl) {
        if (fileUrl == null) {
            return false;
        }

        FFprobeResult videoMessageResult =
                fFprobe
                        .setShowStreams(true)
                        .setInput(com.github.kokorin.jaffree.ffprobe.UrlInput.fromUrl(fileUrl))
                        .execute();

        boolean isVideo = false;
        boolean isSupportVideoCodec = false;
        for (com.github.kokorin.jaffree.ffprobe.Stream stream : videoMessageResult.getStreams()) {
            StreamType codecType = stream.getCodecType();
            if (codecType.equals(StreamType.VIDEO)) {
                isVideo = true;
            }
            String codecLongName = stream.getCodecLongName();
            if (isVideo &&
                    ("MPEG-1 Part 2".equalsIgnoreCase(codecLongName)
                            || "H.261".equalsIgnoreCase(codecLongName)
                            || "H.262".equalsIgnoreCase(codecLongName)
                            || "MPEG-2 Part 2".equalsIgnoreCase(codecLongName)
                            || "H.263".equalsIgnoreCase(codecLongName)
                            || "MPEG-4 Part 2".equalsIgnoreCase(codecLongName)
                            || "VP6".equalsIgnoreCase(codecLongName)
                    )
            ) {
                isSupportVideoCodec = true;
            }

            System.out.println("Stream #" + stream.getIndex()
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
        fFmpeg.addInput(UrlInput.fromUrl(fileUrl))
                .addOutput(new NullOutput())
                .setOutputListener(new OutputListener() {
                    @Override
                    public void onOutput(String message) {
                        System.out.println(">>>>>>calculate message: " + message);
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
