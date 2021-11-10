package com.example.galleryview.videoplay;

import android.os.SystemClock;
import android.view.View;

public abstract class onDoubleClickListener implements View.OnClickListener {


    private static final long DEFAULT_QUALIFICATION_SPAN = 500;
    private final long doubleClickQualificationSpanInMillis;
    private long timestampLastClick;

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
        if((SystemClock.elapsedRealtime() - timestampLastClick) < doubleClickQualificationSpanInMillis) {
            onDoubleClick();
        }
        timestampLastClick = SystemClock.elapsedRealtime();
    }

    public abstract void onDoubleClick();

}