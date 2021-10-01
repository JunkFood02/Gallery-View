package com.example.galleryview;

import android.os.Build;
import android.util.JsonWriter;
import android.util.Log;
import android.webkit.WebSettings;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpUtils {
    private static final String TAG = "HttpUtils";

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static void getToken() {
        new Thread(() -> {
            OkHttpClient client=new OkHttpClient();
            RequestBody requestBody = new FormBody.Builder()
                    .add("username", "sunnycloud105@gmail.com")
                    .add("password", "Whathappened123")
                    .build();

            Request request = new Request.Builder()
                    .url("https://sm.ms/api/v2/"+"token")
                    .addHeader("User-Agent", WebSettings.getDefaultUserAgent(MainActivity.context))
                    .post(requestBody)
                    .build();


            Response response;
            String responseData = null;
            try {
                Log.d(TAG, "try to get token");
                Log.d(TAG, request.toString());
                response = client.newCall(request).execute();
                responseData = response.body().string();
                Log.d(TAG, responseData);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }).start();

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static void postImage(String filename, String filePath) {

        new Thread(() -> {
            Log.d(TAG, filePath);
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("smfile", "1.jpg", RequestBody.
                            create(MediaType.parse("multipart/form-data"), new File(filePath)))
                    .addFormDataPart("format", "json")
                    .build();
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://sm.ms/api/v2/upload")
                    .addHeader("Content-Type", "multipart/form-data")
                    .addHeader("Authorization", "cSuN108oywhL5LR8JYGUwzitpLZYHlvF")
                    .addHeader("User-Agent", WebSettings.getDefaultUserAgent(MainActivity.context))
                    .post(requestBody)
                    .build();

            Response response = null;
            String responseData = null;
            try {
                Log.d(TAG, "try to upload");
                response = client.newCall(request).execute();
                responseData = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d(TAG, responseData);
            try {
                JSONArray jsonArray=new JSONArray(responseData) ;

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }).start();
    }

}
