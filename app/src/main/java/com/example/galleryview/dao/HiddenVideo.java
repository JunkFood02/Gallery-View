package com.example.galleryview.dao;

import androidx.room.Entity;
import androidx.room.Ignore;

@Entity
public class HiddenVideo extends Video{
    public HiddenVideo(String path, int heartCount) {
        super(path, heartCount);
    }
    @Ignore
    public HiddenVideo(Video v)
    {
        super(v.path,v.heartCount);
    }
}
