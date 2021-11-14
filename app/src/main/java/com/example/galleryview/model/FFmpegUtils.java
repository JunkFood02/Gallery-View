package com.example.galleryview.model;


import android.util.Log;

public class FFmpegUtils {
    private static final String TAG = "FFmpegUtils";

    private static onResultListener resultListener;
    public static class onResultListener {
        public void onResult(boolean result) {
            if (!result) {
                Log.d(TAG, "onProcessResult: Success");
            } else {
                Log.e(TAG, "onProcessResult: Failure");
            }
        }
    }



    static {

        System.loadLibrary("x264");
        System.loadLibrary("galleryview");
    }
    public static void run(String[] commands,onResultListener listener)
    {
        resultListener=listener;
        run(commands);
    }
    private static native void run(String[] commands);

    public static void onProcessResult(boolean status) {
        resultListener.onResult(status);
    }

}
