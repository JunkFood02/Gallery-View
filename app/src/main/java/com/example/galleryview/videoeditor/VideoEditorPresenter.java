package com.example.galleryview.videoeditor;

import static com.example.galleryview.model.MyActivity.context;

import android.media.MediaScannerConnection;
import android.net.Uri;

import com.example.galleryview.database.Video;
import com.example.galleryview.gallerypage.ItemAdapter;
import com.example.galleryview.gallerypage.MainActivityPresenter;
import com.example.galleryview.model.DatabaseUtils;
import com.example.galleryview.model.FFmpegUtils;
import com.example.galleryview.model.GalleryItem;
import com.example.galleryview.model.PhotoSelector;
import com.example.galleryview.model.VideoProcessor;


public class VideoEditorPresenter {
    public static final int PROCESS_SUCCESS = 1;
    public static final int PROCESS_CANCEL = 0;
    public static final int PROCESS_FAILURE = -1;

    VideoEditorInterface editorInterface;
    private static final String TAG = "VideoEditorPresenter";

    public VideoEditorPresenter(VideoEditorInterface editorInterface) {
        this.editorInterface = editorInterface;
    }

    public void makeVideoClip(String path, int startPoint, int length) {

        String newName = null;
        if (path.indexOf(".") > 0)
            newName = path.substring(0, path.lastIndexOf(".")) + "_clip.mp4";
        if (newName == null) editorInterface.onProcessFinish(PROCESS_FAILURE);

        String finalNewName = newName;
        VideoProcessor.makeVideoClip(path, finalNewName, startPoint, length, new FFmpegUtils.onResultListener() {
            @Override
            public void onResult(boolean result) {
                super.onResult(result);
                if (!result) {
                    editorInterface.onProcessFinish(PROCESS_SUCCESS);
                    DatabaseUtils.insertVideo(new Video(finalNewName, 0));
                    ItemAdapter.ItemList.add(new GalleryItem(finalNewName));
                    MediaScannerConnection.scanFile(context,
                            new String[]{finalNewName}, null, null);
                } else {
                    editorInterface.onProcessFinish(PROCESS_FAILURE);
                }
            }
        });
    }

    public static String getAudioPath(Uri uri) {
        return PhotoSelector.AudioUriToPath(uri);
    }
}
