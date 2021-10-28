package com.example.galleryview;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.galleryview.dao.Video;
import com.example.galleryview.model.DatabaseUtils;
import com.example.galleryview.ui.ItemAdapter;

import VideoHandle.EpEditor;
import VideoHandle.EpVideo;
import VideoHandle.OnEditorListener;

public class VideoEditorActivity extends MyActivity implements View.OnClickListener {
    String path;
    SeekBar seekBar;
    Button button;
    TextView textView;
    private static final String TAG = "VideoEditorActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_editor);
        path = getIntent().getStringExtra("path");
        seekBar = findViewById(R.id.clipSeekBar);
        button = findViewById(R.id.clipButton);
        textView = findViewById(R.id.clipProgress);
        button.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setTextViewProgress(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }
    private void setTextViewProgress(int progress)
    {runOnUiThread(() -> textView.setText(progress+" sec"));}

    @Override
    public void onClick(View v) {

        ProgressDialog dialog = new ProgressDialog(v.getContext());
        dialog.setTitle("Clip progressing");
        dialog.setCancelable(false);
        dialog.show();
        EpVideo epVideo = new EpVideo(path);
        Log.d(TAG, "onClick: " + path);
        epVideo.clip(0, (float) seekBar.getProgress());
        String builder = path + "_clip.mp4";
        EpEditor.OutputOption outputOption = new EpEditor.OutputOption(builder);
        EpEditor.exec(epVideo, outputOption, new OnEditorListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess: ");
                runOnUiThread(() -> {
                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                });
                DatabaseUtils.insertVideo(new Video(builder,0));
            }

            @Override
            public void onFailure() {
                Log.d(TAG, "onFailure: ");

                runOnUiThread(() -> {
                    Toast.makeText(context, "Failure", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                });
            }

            @Override
            public void onProgress(float progress) {
                Log.d(TAG, "onProgress: ");
            }
        });
    }
}