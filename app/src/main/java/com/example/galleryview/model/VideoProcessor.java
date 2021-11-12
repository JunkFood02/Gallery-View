package com.example.galleryview.model;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.microshow.rxffmpeg.RxFFmpegCommandList;
import io.microshow.rxffmpeg.RxFFmpegInvoke;
import io.microshow.rxffmpeg.RxFFmpegSubscriber;

public class VideoProcessor {
    private static final String TAG = "VideoProcessor";

    public static void makeVideoClip(String path, String newPath, int startPoint, int length, FFmpegUtils.onResultListener listener) {
        File file=new File(newPath);
        if(file.exists()&& file.isFile()&& file.canWrite())
            if(file.delete())
                Log.d(TAG, "makeVideoClip: present clip was deleted!");
        List<String> commands = new ArrayList<>();
        commands.add("ffmpeg");
        commands.add("-i");
        commands.add(path);
        commands.add("-ss");
        commands.add("" + startPoint);
        commands.add("-t");
        commands.add("" + length);
        commands.add(newPath);

        for (String command:commands
        ) {
            Log.d(TAG+"commands", command);
        }
        new Thread(() -> FFmpegUtils.run(commands.toArray(new String[0]),listener)).start();
        //它跑起来了 它真的能跑起来
    }
}
