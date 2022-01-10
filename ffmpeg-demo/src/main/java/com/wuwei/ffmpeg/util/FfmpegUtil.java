package com.wuwei.ffmpeg.util;

import com.github.kokorin.jaffree.StreamType;
import com.github.kokorin.jaffree.ffmpeg.*;
import com.wuwei.ffmpeg.constant.FfmpegConstant;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class FfmpegUtil {

    public static void convertToMp4(InputStream inputStream, OutputStream outputStream) {
        outputStream = Optional.ofNullable(outputStream).orElse(new ByteArrayOutputStream());
        Path ffmpegPath = Paths.get(FfmpegConstant.FFMPEG);
        final AtomicLong durationMillis = new AtomicLong();

        FFmpeg.atPath()
                .addInput(PipeInput.pumpFrom(inputStream))
                .setOverwriteOutput(true)
                .addOutput(new NullOutput())
                .setProgressListener(new ProgressListener() {
                    @Override
                    public void onProgress(FFmpegProgress progress) {
                        durationMillis.set(progress.getTimeMillis());
                    }
                })
                .execute();

        FFmpeg.atPath(ffmpegPath)
                .addInput(PipeInput.pumpFrom(inputStream))
                .addOutput(
                        PipeOutput
                                .pumpTo(outputStream)
                                .addArguments("-preset", "ultrafast")
                                .setFormat("mp4")
                )
                .setProgressListener(new ProgressListener() {
                    @Override
                    public void onProgress(FFmpegProgress progress) {
                        double percents = 100. * progress.getTimeMillis() / durationMillis.get();
                        System.out.println("Progress: " + percents + "%");
                    }
                })
                .execute();
    }
}
