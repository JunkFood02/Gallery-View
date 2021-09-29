package com.example.galleryview;

import android.Manifest;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bm.library.PhotoView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ItemAdapter adapter;
    int operationCode = 0;
    GalleryItem galleryItem = null;
    FloatingActionButton selectButton, clearAllButton;
    Button uploadButton;
    List<GalleryItem> galleryItemList = new ArrayList<>();
    ActivityResultLauncher<Intent> launcher_album;
    RecyclerView recyclerView;
    SQLiteDatabase db;
    MyDatabaseHelper helper;
    StaggeredGridLayoutManager layoutManager;
    public static final String BOOK_TITLE = "Gallery";
    private static final String TAG = "MainActivity";
    public static MainActivity instance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance = this;
        init();

        readAlbumDataFromDatabase();
        launcher_album = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        String imagePath = null;
                        Log.d(TAG, "onActivityResult: ");
                        boolean IS_IMAGE = true;
                        if (result.getResultCode() == RESULT_OK) {

                            Uri uri = Uri.parse(result.getData().toUri(Intent.URI_ALLOW_UNSAFE));
                            IS_IMAGE = uri.toString().contains("image");
                            Log.d(TAG, "uri :" + uri);
                            if (DocumentsContract.isDocumentUri(MainActivity.this, uri)) {
                                String docID = DocumentsContract.getDocumentId(uri);
                                Log.d(TAG, "getDocumentId(uri) :" + docID);

                                if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                                    String id = docID.split(":")[1];
                                    String selection = MediaStore.Images.Media._ID + "=" + id;
                                    if (IS_IMAGE)
                                        imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
                                    else
                                        imagePath = getImagePath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, selection);

                                } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                                    Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docID));
                                    if (IS_IMAGE)
                                        imagePath = getImagePath(contentUri, null);
                                    else
                                        imagePath = getVideoPath(contentUri, null);
                                }
                            } else if ("content".equalsIgnoreCase(uri.getScheme())) {
                                if (IS_IMAGE)
                                    imagePath = getImagePath(uri, null);
                                else
                                    imagePath = getVideoPath(uri, null);
                            } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                                imagePath = uri.getPath();
                            }
                        }
                        //获取路径后
                        if (imagePath != null) {
                            if (IS_IMAGE)
                                galleryItem = new GalleryItem(imagePath);
                            else {
                                galleryItem = new GalleryItem(imagePath, GalleryItem.TYPE_VIDEO);
                                Toast.makeText(MainActivity.this, "Succeeded to get image path.", Toast.LENGTH_SHORT).show();
                            }
                            if (operationCode == 0) {
                                ContentValues values = new ContentValues();
                                values.put("imagepath", galleryItem.getImagePath());
                                values.put("type", galleryItem.getType());
                                values.put("liked",galleryItem.getIS_LIKED());
                                recyclerView.scrollToPosition(0);
                                long imageID = db.insert(BOOK_TITLE, null, values);
                                galleryItem.setId(imageID);
                                adapter.addImage(galleryItem);
                            } else {
                                HttpUtils.postImage("1.jpg", imagePath);
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Failed to get image path.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button2:
                operationCode = 0;
                selectImage();
                recyclerView.setAdapter(adapter);
                break;
            case R.id.uploadButton:
                operationCode = 1;
                selectImage();
                break;
            case R.id.deleteButton:

                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("Remove All Images");
                dialog.setMessage("Are you to remove all the images? " + "This operation cannot be withdrawn.");
                dialog.setPositiveButton("Confirm", (dialog1, which) -> {
                    helper.onClear(db);
                    adapter.clearList();
                    Toast.makeText(this, "All images have been removed.", Toast.LENGTH_SHORT).show();
                });
                dialog.setNegativeButton("Cancel", (dialog12, which) -> {

                });
                dialog.show();
                //recyclerView.setAdapter(adapter);
        }

    }

    private void selectImage() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            openAlbum();
        }
    }

    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("*/*");
        launcher_album.launch(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openAlbum();
        } else Toast.makeText(this, "You denied the permission.", Toast.LENGTH_SHORT).show();
    }

    public String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToNext()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    public String getVideoPath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToNext()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void readAlbumDataFromDatabase() {
        db = helper.getReadableDatabase();
        Cursor cursor;
        cursor = db.query(BOOK_TITLE, null, null, null, null, null, null);
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
        recyclerView.setAdapter(adapter);

    }

    private void init() {
        recyclerView = findViewById(R.id.galleryRecyclerView);

        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        selectButton = findViewById(R.id.button2);
        uploadButton = findViewById(R.id.uploadButton);
        clearAllButton = findViewById(R.id.deleteButton);
        uploadButton.setOnClickListener(this);
        selectButton.setOnClickListener(this);
        clearAllButton.setOnClickListener(this);
        adapter = new ItemAdapter(galleryItemList);
        ItemTouchHelper.Callback callback = new MyItemTouchHelperCallBack(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        recyclerView.setAdapter(adapter);
        touchHelper.attachToRecyclerView(recyclerView);
        helper = new MyDatabaseHelper(this, "Gallery.db", null, 1);
        db = helper.getWritableDatabase();

    }

    public void undoRemove(GalleryItem currentItem, int position) {
        Snackbar.make(selectButton, "Image removed.", Snackbar.LENGTH_SHORT)
                .setAction("Undo", v -> {
                    ContentValues values = new ContentValues();
                    values.put("imagepath", currentItem.getImagePath());
                    values.put("type", currentItem.getType());
                    values.put("liked",currentItem.getIS_LIKED());
                    long imageID = db.insert(BOOK_TITLE, null, values);
                    currentItem.setId(imageID);
                    adapter.insertImage(currentItem, position);

                    recyclerView.scrollToPosition(position);
                }).show();
    }
}
