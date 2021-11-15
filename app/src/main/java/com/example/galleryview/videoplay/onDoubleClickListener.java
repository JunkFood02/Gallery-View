package com.example.galleryview.videoplay;

import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

public abstract class onDoubleClickListener implements View.OnClickListener {


    private static final long DEFAULT_QUALIFICATION_SPAN = 300;
    private static final String TAG = "onDoubleClickListener";
    private final long doubleClickQualificationSpanInMillis;
    private long timestampLastClick;
    private boolean readyForDoubleClick;
    private Timer timer;

    public onDoubleClickListener() {
        doubleClickQualificationSpanInMillis = DEFAULT_QUALIFICATION_SPAN;
        timestampLastClick = 0;
    }

    public onDoubleClickListener(long doubleClickQualificationSpanInMillis) {
        this.doubleClickQualificationSpanInMillis = doubleClickQualificationSpanInMillis;
        timestampLastClick = 0;
    }

    @Override
    public void onClick(View v) {
        if (!readyForDoubleClick) {
            timer=new Timer();
            readyForDoubleClick = true;
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Log.d(TAG, "onSingleClick: ");
                    onSingleClick();
                    readyForDoubleClick = false;
                    timer.cancel();
                }
            }, DEFAULT_QUALIFICATION_SPAN);
        } else {
            timer.cancel();
            Log.d(TAG, "onDoubleClick: ");
            readyForDoubleClick = false;
            onDoubleClick();
        }
    }

    public abstract void onDoubleClick();

    public abstract void onSingleClick();
}