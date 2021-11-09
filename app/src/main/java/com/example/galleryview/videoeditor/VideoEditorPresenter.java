package com.example.galleryview.videoeditor;

import android.net.Uri;
import android.util.Log;

import com.example.galleryview.database.Video;
import com.example.galleryview.model.DatabaseUtils;
import com.example.galleryview.model.PhotoSelector;
import com.example.galleryview.model.VideoProcessor;

import io.microshow.rxffmpeg.RxFFmpegSubscriber;

public class VideoEditorPresenter {
    public static final int PROCESS_SUCCESS = 1;
    public static final int PROCESS_CANCEL = 0;
    public static final int PROCESS_FAILURE = -1;

    VideoEditorInterface editorInterface;
    private static final String TAG = "VideoEditorPresenter";

    public VideoEditorPresenter(VideoEditorInterface editorInterface) {
        this.editorInterface = editorInterface;
    }

    public void makeVideoClip(String path,int startPoint, int length) {

        String newName = path.substring(0, path.length() - 4) + "_clip.mp4";
        RxFFmpegSubscriber subscriber = new RxFFmpegSubscriber() {
            @Override
            public void onFinish() {
                editorInterface.onProcessFinish(PROCESS_SUCCESS);
                DatabaseUtils.insertVideo(new Video(newName, 0));
            }

            @Override
            public void onProgress(int progress, long progressTime) {
            }

            @Override
            public void onCancel() {
                editorInterface.onProcessFinish(PROCESS_CANCEL);
            }

            @Override
            public void onError(String message) {
                editorInterface.onProcessFinish(PROCESS_FAILURE);
                Log.e(TAG, "onError: "+message);
            }
        };
        VideoProcessor.makeVideoClip(path, newName,startPoint, length, subscriber);
    }
    public static String getAudioPath(Uri uri)
    {
        return PhotoSelector.AudioUriToPath(uri);
    }
}
