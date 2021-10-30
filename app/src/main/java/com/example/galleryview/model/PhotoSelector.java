package com.example.galleryview.model;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import com.example.galleryview.MainActivity;

public class PhotoSelector {
    private static final String TAG = "PhotoSelector";

    public static String uriToPath(Uri uri) {
        boolean IS_IMAGE;
        String imagePath = null;
        IS_IMAGE = uri.toString().contains("image");
        Log.d(TAG, "uri :" + uri);
        if (DocumentsContract.isDocumentUri(MainActivity.context, uri)) {
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
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.parseLong(docID));
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
        return imagePath;
    }

    public static String AudioUriToPath(Uri uri) {
        String path = null;
        if (DocumentsContract.isDocumentUri(MainActivity.context, uri)) {
            String docID = DocumentsContract.getDocumentId(uri);
            Log.d(TAG, "getDocumentId(uri) :" + docID);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docID.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                path = getImagePath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, selection);

            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.parseLong(docID));
                path = getMusicPath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            path = getMusicPath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            path = uri.getPath();
        }
        return path;
    }

    private static String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = MainActivity.context.getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToNext()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private static String getVideoPath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = MainActivity.context.getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToNext()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
            }
            cursor.close();
        }
        Log.d(TAG, "getVideoPath: " + path);
        return path;
    }

    private static String getMusicPath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = MainActivity.context.getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToNext()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            }
            cursor.close();
        }
        Log.d(TAG, "getMusicPath: " + path);
        return path;
    }
}
