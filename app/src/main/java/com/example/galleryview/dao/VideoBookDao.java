package com.example.galleryview.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface VideoBookDao {
    @Insert
    long insertVideo(Video video);
    @Delete
    void delete(Video video);
    @Query("SELECT * FROM video")
    List<Video> getAllVideos();
    @Query("SELECT * FROM video WHERE id Like :videoID LIMIT 1")
    Video findById(long videoID);


}
