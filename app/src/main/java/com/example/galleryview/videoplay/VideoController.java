package com.example.galleryview.videoplay;

public interface VideoController {
void noticeVideoStart();
void noticeVideoStop();
void noticeVideoRestart();
boolean changeVideoPlayStatus();
}
