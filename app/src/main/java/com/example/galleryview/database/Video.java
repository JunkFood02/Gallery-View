package com.example.galleryview.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Video {

    @PrimaryKey(autoGenerate = true)
    public long id = 0;
    @ColumnInfo(name = "video_path")
    public String path;
    @ColumnInfo(name = "heart_count")
    public int heartCount;

    public Video(String path, int heartCount) {
        this.path = path;
        this.heartCount = heartCount;
    }


    @Ignore
    public Video(GalleryItem item) {
        this.path = item.getImagePath();
        this.heartCount = item.getIS_LIKED();
        this.id = item.getId();
    }

}
