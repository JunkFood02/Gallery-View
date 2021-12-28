package com.example.galleryview.videoplay;

public interface VideoController {
    void noticeVideoStart();

    void noticeVideoPause();

    void noticeVideoRestart();

    boolean changeVideoPlayStatus();
}
