package com.example.galleryview.dao;

import androidx.room.Entity;
import androidx.room.Ignore;

import com.example.galleryview.model.GalleryItem;

@Entity
public class HiddenVideo extends Video {
    public HiddenVideo(String path, int heartCount) {
        super(path, heartCount);

    }

    @Ignore
    public HiddenVideo(Video v) {
        super(v.path, v.heartCount);
    }

    @Ignore
    public HiddenVideo(GalleryItem item) {
        super(item);
    }
}
