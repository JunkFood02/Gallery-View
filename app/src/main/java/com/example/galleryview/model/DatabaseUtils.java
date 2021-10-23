package com.example.galleryview.model;

import static com.example.galleryview.MainActivity.BOOK_TITLE;
import static com.example.galleryview.MyActivity.context;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import com.example.galleryview.MainActivity;
import com.example.galleryview.R;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.security.auth.callback.Callback;

public class DatabaseUtils {
   private static ExecutorService exec = Executors.newCachedThreadPool();

    static class MyThread<T> extends Thread {
        T data;

        public MyThread(T data) {
            this.data = data;
        }

        void updateData(T data) {
            this.data = data;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private static final MyDatabaseHelper helper = new MyDatabaseHelper(MainActivity.context, "Gallery.db", null, 1);
    private static final SQLiteDatabase rdb = helper.getReadableDatabase();
    private static final SQLiteDatabase wdb = helper.getWritableDatabase();

    public static void Update(long id, ContentValues values) {
        new Thread(() -> wdb.update(BOOK_TITLE, values, "id=?", new String[]{"" + id})).start();
    }

    public static long Insert(ContentValues values) throws ExecutionException, InterruptedException {

        long id = 0;
        Future<Long> future = exec.submit(() -> wdb.insert(BOOK_TITLE, null, values));
        id = future.get();
        return id;
    }

    public static Cursor Query() throws ExecutionException, InterruptedException {
        Cursor cursor;
        Future<Cursor> future = exec.submit(() -> rdb.query(BOOK_TITLE, null, null, null, null, null, null));
        cursor = future.get();
        return cursor;
    }
}
