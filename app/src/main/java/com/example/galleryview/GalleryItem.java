package com.example.galleryview;

import static com.example.galleryview.presenter.MainActivityPresenter.isPrivateModeEnable;

import android.graphics.Bitmap;

import com.example.galleryview.dao.PrivateVideo;
import com.example.galleryview.dao.Video;
import com.example.galleryview.model.DatabaseUtils;


public class GalleryItem {
    private final String imagePath;
    private static final String TAG = "GalleryItem";
    private int type; //媒体类型
    private long id; //在数据库中的id
    private int heartCount = 0; //点赞数
    public static final int TYPE_IMAGE = 1;
    public static final int TYPE_VIDEO = 2;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public int getIS_LIKED() {
        return heartCount;
    }

    public void doubleClickLike() {
        heartCount++;
        updateHeartCount();
    }


    private void updateHeartCount() {
        if (!isPrivateModeEnable()) DatabaseUtils.Update(new Video(this));
        else
            DatabaseUtils.UpdatePrivateVideo(new PrivateVideo(this));
    }

    public void setId(long id) {
        this.id = id;
    }

    public GalleryItem(String imagePath) {
        this.imagePath = imagePath;
        this.type = TYPE_IMAGE;
    }

    public GalleryItem(String imagePath, int type) {
        this.imagePath = imagePath;
        this.type = type;
    }

    public GalleryItem(String imagePath, int type, long id) {
        this.imagePath = imagePath;
        this.type = type;
        this.id = id;
    }


    public GalleryItem(String imagePath, int type, long id, int heartCount) {
        this.imagePath = imagePath;
        this.type = type;
        this.id = id;
        this.heartCount = heartCount;
    }

    public GalleryItem(String imagePath, long id, int heartCount) {
        this.imagePath = imagePath;
        this.id = id;
        this.heartCount = heartCount;
    }

    public GalleryItem(Video video) {
        this.imagePath = video.path;
        this.heartCount = video.heartCount;
        this.id = video.id;
    }

    public GalleryItem(PrivateVideo video) {
        this.imagePath = video.path;
        this.heartCount = video.heartCount;
        this.id = video.id;
    }

    public String getImagePath() {
        return imagePath;
    }

}
