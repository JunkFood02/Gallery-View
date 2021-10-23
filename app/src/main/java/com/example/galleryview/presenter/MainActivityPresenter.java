package com.example.galleryview.presenter;

import static android.os.Looper.getMainLooper;
import static com.example.galleryview.MainActivity.SHOW_FULLSCREEN_IMAGE;
import static com.example.galleryview.MainActivity.UNDO_REMOVE_IMAGE;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bm.library.Info;
import com.example.galleryview.model.GalleryItem;
import com.example.galleryview.ItemAdapter;
import com.example.galleryview.MainActivity;
import com.example.galleryview.model.DatabaseUtils;
import com.example.galleryview.model.HttpUtils;
import com.example.galleryview.model.PhotoSelector;

import java.util.concurrent.ExecutionException;

public class MainActivityPresenter {
    private static final String TAG = "MainActivityPresenter";
    private ItemAdapter adapter;
    private int position;
    private GalleryItem galleryItem;
    private Info info;
    private Handler handler;
    static MainActivityInterface mainActivityInterface;

    public MainActivityPresenter(MainActivityInterface mainActivityInterface) {
        MainActivityPresenter.mainActivityInterface = mainActivityInterface;
        setupHandler();
    }

    public void readAlbumDataFromDatabase() throws ExecutionException, InterruptedException {
        try (Cursor cursor = DatabaseUtils.Query()) {
            if (cursor == null) {
                return;
            }
            new Thread(() -> {
                if (cursor.moveToFirst()) {
                    do {
                        String imagePath = cursor.getString(cursor.getColumnIndex("imagepath"));
                        int type = cursor.getInt(cursor.getColumnIndex("type"));
                        long id = cursor.getLong(cursor.getColumnIndex("id"));
                        int is_liked = cursor.getInt(cursor.getColumnIndex("liked"));
                        GalleryItem galleryItem = new GalleryItem(imagePath, type, id, is_liked);
                        Log.d(TAG, "id: " + id);
                        adapter.addImage(galleryItem);
                    } while (cursor.moveToNext());
                }
            }).start();

        }

    }

    public static void addNewImage(Uri uri, int operationCode) {
        String imagePath;
        GalleryItem newItem;
        boolean IS_IMAGE = uri.toString().contains("image");
        imagePath = PhotoSelector.uriToPath(uri);
        if (imagePath != null) {
            if (IS_IMAGE)
                newItem = new GalleryItem(imagePath);
            else {
                newItem = new GalleryItem(imagePath, GalleryItem.TYPE_VIDEO);
                Toast.makeText(MainActivity.context, "Succeeded to get image path.", Toast.LENGTH_SHORT).show();
            }
            if (operationCode == 0) {
                new Thread(() -> {
                    ContentValues values = new ContentValues();
                    values.put("imagepath", newItem.getImagePath());
                    values.put("type", newItem.getType());
                    values.put("liked", newItem.getIS_LIKED());
                    long imageID = 0;
                    try {
                        imageID = DatabaseUtils.Insert(values);
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    newItem.setId(imageID);
                    mainActivityInterface.insertNewImage(newItem);
                }).start();

            } else {
                HttpUtils.postImage("1.jpg", imagePath);
            }
        } else {
            Toast.makeText(MainActivity.context, "Failed to get image path.", Toast.LENGTH_SHORT).show();
        }
    }

    public static void reAddItem(GalleryItem currentItem) {
        new Thread(() -> {
            ContentValues values = new ContentValues();
            values.put("id", currentItem.getId());
            values.put("imagepath", currentItem.getImagePath());
            values.put("type", currentItem.getType());
            values.put("liked", currentItem.getIS_LIKED());
            try {
                DatabaseUtils.Insert(values);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public Handler getHandler() {
        return handler;
    }

    private void setupHandler() {
        new Thread(() -> handler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case SHOW_FULLSCREEN_IMAGE:
                        position = msg.arg1;
                        galleryItem = (GalleryItem) msg.obj;
                        info = galleryItem.getInfo();
                        mainActivityInterface.showFullscreenPhoto(galleryItem.getImagePath(), info);
                        break;
                    case UNDO_REMOVE_IMAGE:
                        galleryItem = (GalleryItem) msg.obj;
                        position = msg.arg1;
                        mainActivityInterface.showUndoRemoveSnackbar(galleryItem, position);
                        break;
                }
            }
        }).start();

    }

    public void setAdapter(ItemAdapter adapter) {
        this.adapter = adapter;
    }
}
