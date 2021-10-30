package com.example.galleryview;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bm.library.PhotoView;

public class BigPictureActivity extends AppCompatActivity implements View.OnClickListener {
    //这里的代码没用 别看
    VideoView videoView;
    PhotoView fullscreenImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_pigture);
        ConstraintLayout videoLayout = findViewById(R.id.videoPlayLayout);
        fullscreenImage = findViewById(R.id.fullscreenImage);
        videoView = findViewById(R.id.videoView);
        Intent intent = getIntent();
        intent.getStringExtra("path");
        String path = intent.getStringExtra("path");
        int type = intent.getIntExtra("media_type", 1);
        Bitmap bitmap;
        if (type == GalleryItem.TYPE_IMAGE) {
            bitmap = BitmapFactory.decodeFile(path);
            if (bitmap != null) {
                fullscreenImage.setImageBitmap(bitmap);
                //fullscreenImage.animaFrom(fullscreenImage.getInfo());
                fullscreenImage.enable();
            } else {
                Toast.makeText(this, "Fail to load image.", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            fullscreenImage.setVisibility(View.INVISIBLE);
            videoLayout.setVisibility(View.VISIBLE);
            videoView.setVideoPath(path);
            videoView.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        if (videoView.isPlaying()){
            Toast.makeText(v.getContext(), "Video Pause", Toast.LENGTH_SHORT).show();
            videoView.pause();}

        else {videoView.start();
            Toast.makeText(v.getContext(), "Video Start", Toast.LENGTH_SHORT).show();}
    }
}