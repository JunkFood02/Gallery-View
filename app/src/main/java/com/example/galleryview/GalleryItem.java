package com.example.galleryview;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class GalleryItem {
    private String imagePath;
    private static final String TAG = "GalleryItem";
    private int type;
    private long id;
    private int IS_LIKED = NOT_LIKED;
    private final static int LIKED = 1;
    private final static int NOT_LIKED = -1;
    private static MyDatabaseHelper helper = MainActivity.instance.helper;
    private static SQLiteDatabase db = MainActivity.instance.db;

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
        Log.d(TAG, IS_LIKED+"");
        IS_LIKED = -IS_LIKED;
        Log.d(TAG, IS_LIKED+"");
        db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("liked", IS_LIKED);
        db.update(MainActivity.BOOK_TITLE, values, "id=?", new String[]{"" + id});
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

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
