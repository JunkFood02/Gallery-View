package com.example.galleryview.model;

import static com.example.galleryview.model.MyActivity.context;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.room.Room;

import com.example.galleryview.database.AppDatabase;
import com.example.galleryview.database.PrivateVideo;
import com.example.galleryview.database.LabelRecord;
import com.example.galleryview.database.Video;
import com.example.galleryview.database.VideoBookDao;
import com.example.galleryview.database.GalleryItem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DatabaseUtils {
    private static final ExecutorService exec = Executors.newCachedThreadPool();
    private static final String TAG = "DatabaseUtils";
    public static AppDatabase appDatabase = Room.databaseBuilder(context, AppDatabase.class, "app_database")
            .build();
    public static VideoBookDao dao = appDatabase.dao();

    /**
     * 插入视频
     * @param video 输入的video视频类
     * @return 返回视频id
     */
    public static long insertVideo(Video video) {

        Future<Long> future = exec.submit(() -> dao.insertVideo(video));
        long id = 0;
        try {
            id = future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return id;
    }

    public static void deletePrivateVideoByID(long id) {
        new Thread(() -> dao.deleteHiddenVideoByID(id)).start();
    }

    public static void insertLabel(int labelID, long videoID) {
        new Thread(() -> dao.insertLabel(new LabelRecord(labelID, videoID))).start();

    }

    public static void Update(Video video) {
        new Thread(() -> dao.updateVideo(video)).start();
    }

    public static void UpdatePrivateVideo(PrivateVideo privateVideo) {
        new Thread(() -> dao.updateHiddenVideo(privateVideo)).start();
    }

    public static List<Video> getAllVideoFromRoom() {
        List<Video> videos = new ArrayList<>();
        Future<List<Video>> future = exec.submit(() -> dao.getAllVideos());
        try {
            videos = future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return videos;
    }

    public static void clearAllDataInRoom() {
        new Thread(() -> {
            dao.deleteAllVideo();
            dao.deleteAllLabel();
            dao.deleteAllHiddenVideo();
        }).start();

    }

    public static void deleteVideoByID(long id) {
        new Thread(() -> dao.deleteVideoByID(id)).start();
    }

    public static boolean[] findCheckedLabelsByVideoId(int labelNumbers, long videoID) {
        List<LabelRecord> labelRecords = new ArrayList<>();
        boolean[] checkedItems = new boolean[labelNumbers];
        Future<List<LabelRecord>> future = exec.submit(() -> dao.getAllLabelRecordByVideoID(videoID));
        try {
            labelRecords = future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        for (LabelRecord r : labelRecords
        ) {
            Log.d(TAG, "findCheckedLabelsByVideoId: labelID= " + r.LabelId);
            checkedItems[r.LabelId] = true;
        }
        return checkedItems;
    }

    public static void cleanLabelsByVideoID(long videoID) {
        new Thread(() -> {
            dao.clearLabelByVideoID(videoID);
        }).start();
    }

    public static void insertLabels(boolean[] checkedLabels, long videoID) {
        new Thread(() -> {

            for (int i = 0; i < checkedLabels.length; i++) {
                if (checkedLabels[i]) {
                    Log.d(TAG, "insertLabel: for video " + videoID + ", Label " + i + " is checked!");
                    dao.insertLabel(new LabelRecord(i, videoID));
                } else {
                    dao.deleteSpecificLabelByID(videoID, i);
                }

            }
        }).start();
    }

    public static List<Video> getAllVideosByLabelIDs(List<Long> ids) {
        List<Video> videos = new ArrayList<>();
        Future<List<Video>> future = exec.submit(() -> dao.getAllVideoByVideoIDs(dao.getAllVideoIDByLabelIds(ids)));
        try {
            videos = future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return videos;
    }


    public static void hideVideo(GalleryItem item) {
        new Thread(() -> {
            dao.hideVideo(new PrivateVideo(item));
            deleteVideoByID(item.getId());
            cleanLabelsByVideoID(item.getId());
        }).start();
    }
    public static void deleteVideoByPath(String path)
    {
        new Thread(() -> dao.deleteVideoByPath(path)).start();
    }
    public static List<PrivateVideo> getPrivateVideos() {
        List<PrivateVideo> privateVideos = new ArrayList<>();
        Future<List<PrivateVideo>> future = exec.submit(() -> dao.getAllHiddenVideo());
        try {
            privateVideos = future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return privateVideos;
    }

}
