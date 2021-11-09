package com.example.galleryview.model;

import io.microshow.rxffmpeg.RxFFmpegCommandList;
import io.microshow.rxffmpeg.RxFFmpegInvoke;
import io.microshow.rxffmpeg.RxFFmpegSubscriber;

public class VideoProcessor {
    private static final String TAG = "VideoProcessor";

    public static void makeVideoClip(String path, String newPath, int startPoint, int length, RxFFmpegSubscriber subscriber) {
        RxFFmpegCommandList commandList = new RxFFmpegCommandList();
        //本来想自己编译FFmpeg来用 摆弄了一下发现嗯嗯完全没懂 最后还是去github上找了别人封装好的来用
        commandList.append("-i")
                .append(path)
                .append("-ss")
                .append("" + startPoint)
                .append("-t")
                .append("" + length)
                .append(newPath);
        RxFFmpegInvoke.getInstance().runCommandRxJava(commandList.build())
                .subscribe(subscriber);
    }
}
