package com.example.galleryview.model;

import android.content.ContentValues;

import com.bm.library.Info;
import com.example.galleryview.model.DatabaseUtils;


public class GalleryItem {
    private String imagePath;
    private Info info;
    private static final String TAG = "GalleryItem";
    private int type;
    private long id;
    private int IS_LIKED = NOT_LIKED;
    private final static int LIKED = 1;
    private final static int NOT_LIKED = -1;

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
        return IS_LIKED;
    }

    public boolean IS_LIKED() {
        return IS_LIKED == LIKED;
    }

    public void clickLike() {
        IS_LIKED = -IS_LIKED;
        ContentValues values = new ContentValues();
        values.put("liked", IS_LIKED);
        DatabaseUtils.Update(id,values);
    }

    public void setId(long id) {
        this.id = id;
    }

    public GalleryItem(String imagePath) {
        this.imagePath = imagePath;
        this.type = TYPE_IMAGE;
        IS_LIKED = NOT_LIKED;
    }

    public GalleryItem(String imagePath, int type) {
        this.imagePath = imagePath;
        this.type = type;
        IS_LIKED = NOT_LIKED;
    }

    public GalleryItem(String imagePath, int type, long id) {
        this.imagePath = imagePath;
        this.type = type;
        this.id = id;
        IS_LIKED = NOT_LIKED;
    }

    public GalleryItem(String imagePath, int type, long id, int IS_LIKED) {
        this.imagePath = imagePath;
        this.type = type;
        this.id = id;
        this.IS_LIKED = IS_LIKED;
    }

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
