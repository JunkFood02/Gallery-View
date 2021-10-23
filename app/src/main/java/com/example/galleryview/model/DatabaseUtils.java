package com.example.galleryview.model;

import static com.example.galleryview.MainActivity.BOOK_TITLE;
import static com.example.galleryview.MyActivity.context;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.HeterogeneousExpandableList;

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
    private static final ExecutorService exec = Executors.newCachedThreadPool();
    private static final String TAG = "DatabaseUtils";

    @SuppressLint("StaticFieldLeak")
    private static final MyDatabaseHelper helper = new MyDatabaseHelper(MainActivity.context, "Gallery.db", null, 1);
    private static SQLiteDatabase rdb = helper.getReadableDatabase();
    private static SQLiteDatabase wdb = helper.getWritableDatabase();

    public static void Update(long id, ContentValues values) {
        new Thread(() -> wdb.update(BOOK_TITLE, values, "id=?", new String[]{"" + id})).start();
    }

    public static long Insert(ContentValues values) throws ExecutionException, InterruptedException {
        long id;
        Future<Long> future = exec.submit(() -> wdb.insert(BOOK_TITLE, null, values));
        id = future.get();
        Log.d(TAG, "Insert: id = " + id);
        return id;
    }

    public static Cursor Query() throws ExecutionException, InterruptedException {
        Cursor cursor;
        Future<Cursor> future = exec.submit(() -> rdb.query(BOOK_TITLE, null, null, null, null, null, null));
        cursor = future.get();
        return cursor;
    }

    public static void Delete(long id) {
        Log.d(TAG, "Delete: id = " + id);
        wdb.delete(BOOK_TITLE, "id=?", new String[]{"" + id});
    }

    private static void writableInit() {
        wdb = helper.getWritableDatabase();
    }

    private static void readableInit() {
        rdb = helper.getReadableDatabase();
    }
    public static void Clear()
    {
        helper.onClear(rdb);
    }
}
