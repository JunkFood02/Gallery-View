package com.example.galleryview.model;

public class FFmpegUtils {
    static {
        System.loadLibrary("galleryview");
    }
    public static native void run(String[] commands);
}
