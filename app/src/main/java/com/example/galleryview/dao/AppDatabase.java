package com.example.galleryview.dao;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(version = 1, entities = {LabelRecord.class, Video.class}, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract VideoBookDao dao();
}
