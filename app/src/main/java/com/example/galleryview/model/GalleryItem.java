package com.example.galleryview.model;

import android.content.ContentValues;
import android.graphics.Bitmap;

import com.bm.library.Info;
import com.example.galleryview.dao.Video;
import com.example.galleryview.model.DatabaseUtils;


public class GalleryItem {
    private String imagePath;
    private static final String TAG = "GalleryItem";
    private int type;
    private long id;
    private int heartCount = 0;
    private String label = null;
    Bitmap bitmap;
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
        DatabaseUtils.Update(new Video(this));
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

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public GalleryItem(String imagePath, int type, long id, int IS_LIKED) {
        this.imagePath = imagePath;
        this.type = type;
        this.id = id;
        this.heartCount = IS_LIKED;
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

    public String getImagePath() {
        return imagePath;
    }


    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
