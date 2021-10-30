package com.example.galleryview.dao;

import androidx.room.Entity;
import androidx.room.Ignore;

import com.example.galleryview.GalleryItem;

@Entity
public class PrivateVideo extends Video {
    public PrivateVideo(String path, int heartCount) {
        super(path, heartCount);

    }

    @Ignore
    public PrivateVideo(Video v) {
        super(v.path, v.heartCount);
    }

    @Ignore
    public PrivateVideo(GalleryItem item) {
        super(item);
    }
}
