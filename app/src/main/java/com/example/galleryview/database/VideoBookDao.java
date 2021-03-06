package com.example.galleryview.database;

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

    @Update
    void updateVideo(Video video);

    @Delete
    void deleteVideo(Video video);

    @Query("delete from video where video_path=:path")
    void deleteVideoByPath(String path);
    @Query("DELETE FROM video WHERE id=:videoID")
    void deleteVideoByID(long videoID);

    @Query("SELECT * FROM video")
    List<Video> getAllVideos();

    @Query("SELECT video_id FROM labelrecord WHERE label_id IN(:labelIds)")
    List<Long> getAllVideoIDByLabelIds(List<Long> labelIds);

    @Query("SELECT * FROM video WHERE id = :videoID LIMIT 1")
    Video findVideoById(long videoID);

    @Query("SELECT * FROM labelrecord WHERE video_id = :videoID ")
    List<LabelRecord> getAllLabelRecordByVideoID(long videoID);

    @Query("SELECT * FROM video WHERE id IN(:ids)")
    List<Video> getAllVideoByVideoIDs(List<Long> ids);

    @Update
    void updateLabelRecord(LabelRecord record);

    @Query("DELETE FROM labelrecord WHERE video_id = :videoID")
    void clearLabelByVideoID(long videoID);

    @Insert
    void insertLabel(LabelRecord record);

    @Query("DELETE FROM video")
    void deleteAllVideo();

    @Query("DELETE FROM labelrecord")
    void deleteAllLabel();

    @Query("DELETE FROM LABELRECORD WHERE video_id=:videoID AND label_id=:labelID ")
    void deleteSpecificLabelByID(long videoID, int labelID);

    @Insert
    void hideVideo(PrivateVideo privateVideo);

    @Query("SELECT * FROM PrivateVideo")
    List<PrivateVideo> getAllHiddenVideo();

    @Query("DELETE FROM PrivateVideo")
    void deleteAllHiddenVideo();

    @Query("DELETE FROM PrivateVideo WHERE id=:videoID")
    void deleteHiddenVideoByID(long videoID);

    @Update
    void updateHiddenVideo(PrivateVideo video);
}
