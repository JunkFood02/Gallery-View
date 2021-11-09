package com.example.galleryview.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class LabelRecord {
    @PrimaryKey(autoGenerate = true)
    public long id;
    @ColumnInfo(name = "label_id")
    public int LabelId;
    @ColumnInfo(name = "video_id")
    public long videoId;

    public LabelRecord(int LabelId, long videoId) {
        this.videoId = videoId;
        this.LabelId = LabelId;
    }

    @Ignore
    public LabelRecord(int id, int LabelId, long videoId) {
        this.id = id;
        this.videoId = videoId;
        this.LabelId = LabelId;
    }
}
