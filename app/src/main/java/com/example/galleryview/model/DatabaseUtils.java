package com.example.galleryview.model;

import static com.example.galleryview.MainActivity.BOOK_TITLE;
import static com.example.galleryview.MainActivity.FILTER_BOOL_TITLE;
import static com.example.galleryview.MyActivity.context;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.HeterogeneousExpandableList;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.room.Room;

import com.airbnb.lottie.L;
import com.example.galleryview.MainActivity;
import com.example.galleryview.R;
import com.example.galleryview.dao.AppDatabase;
import com.example.galleryview.dao.LabelRecord;
import com.example.galleryview.dao.Video;
import com.example.galleryview.dao.VideoBookDao;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.security.auth.callback.Callback;

import kotlin.jvm.Synchronized;

public class DatabaseUtils {
    private static final ExecutorService exec = Executors.newCachedThreadPool();
    private static final String TAG = "DatabaseUtils";
    public static AppDatabase appDatabase = Room.databaseBuilder(context, AppDatabase.class, "app_database")
            .build();
    public static VideoBookDao dao = appDatabase.dao();

    @SuppressLint("StaticFieldLeak")


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

    public static void insertLabel(int labelID, long videoID) {
        new Thread(() -> dao.insertLabel(new LabelRecord(labelID, videoID))).start();

    }

    public static void Update(Video video) {
        new Thread(() -> dao.updateVideo(video)).start();
    }

    public static List<Video> getAllVideoFromRoom() {
        List<Video> videos = new ArrayList<>();
        Future<List<Video>> future = exec.submit(new Callable<List<Video>>() {
            @Override
            public List<Video> call() throws Exception {
                return dao.getAllVideos();
            }
        });
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


}
