package com.example.galleryview.videoeditor;

import static com.example.galleryview.videoeditor.VideoEditorPresenter.PROCESS_CANCEL;
import static com.example.galleryview.videoeditor.VideoEditorPresenter.PROCESS_FAILURE;
import static com.example.galleryview.videoeditor.VideoEditorPresenter.PROCESS_SUCCESS;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.example.galleryview.R;
import com.example.galleryview.gallerypage.MainActivityPresenter;
import com.example.galleryview.model.MyActivity;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

public class VideoEditorActivity extends MyActivity implements View.OnClickListener, VideoEditorInterface {
    private static final String TAG = "VideoEditorActivity";
    String path;
    SeekBar endpointSeekBar, beginSeekBar;
    Button clipButton, BGMButton;
    ActivityResultLauncher<Intent> launcherForBGM;
    TextView lengthText, beginPointText;
    PlayerView playerView;
    SimpleExoPlayer player;
    private int videoLength;
    private VideoEditorPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_editor);
        path = getIntent().getStringExtra("path");
        presenter = new VideoEditorPresenter(this);
        endpointSeekBar = findViewById(R.id.clipSeekBar);
        clipButton = findViewById(R.id.clipButton);
        BGMButton = findViewById(R.id.BGMButton);
        lengthText = findViewById(R.id.clipEndpoint);
        beginPointText = findViewById(R.id.beginPoint);
        beginSeekBar = findViewById(R.id.beginSeekBar);
        BGMButton.setOnClickListener(this);
        clipButton.setOnClickListener(this);
        playerView = findViewById(R.id.editorVideoView);
        MediaItem mediaItem = MediaItem.fromUri(path);
        player = new SimpleExoPlayer.Builder(this).build();
        playerView.setPlayer(player);
        player.setMediaItem(mediaItem);
        player.setPlayWhenReady(true);
        player.pause();
        player.prepare();
        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == Player.STATE_READY) {
                    videoLength = (int) (player.getDuration() / 1000);
                    beginSeekBar.setMax(videoLength);
                    endpointSeekBar.setMax(videoLength);
                    Log.d(TAG, "Duration = " + videoLength);
                }
            }
        });

        beginSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                runOnUiThread(() -> {
                    beginPointText.setText("begin: " + progress + " sec");
                    if (progress > endpointSeekBar.getProgress())
                        endpointSeekBar.setProgress(progress);
                    player.seekTo(progress * 1000L);
                    player.pause();
                });
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        endpointSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                runOnUiThread(() -> lengthText.setText("end: " + progress + " sec"));
                if (progress < beginSeekBar.getProgress())
                    beginSeekBar.setProgress(progress);
                if (player.getCurrentPosition() > progress * 1000L + 1000L) {
                    player.pause();
                    player.seekTo(progress * 1000L);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        launcherForBGM = registerForActivityResult( //从文件管理获取音乐uri
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Log.d(TAG, "onActivityResult: ");
                    if (result.getResultCode() == RESULT_OK) {
                        assert result.getData() != null;
                        Uri uri = Uri.parse(result.getData().toUri(Intent.URI_ALLOW_UNSAFE));
                        Toast.makeText(this, VideoEditorPresenter.getAudioPath(uri), Toast.LENGTH_SHORT).show();
                        //加背景音乐的 FFmpeg 命令还没有写 所以先弹个 Toast
                    }
                });
    }


    @Override
    public void onClick(View v) {
        final int clipButtonID = R.id.clipButton, selectMusicButton = R.id.BGMButton;
        switch (v.getId()) {
            case clipButtonID:
                Toast.makeText(getContext(), "Video processing.", Toast.LENGTH_SHORT).show();
                presenter.makeVideoClip(path, beginSeekBar.getProgress(),
                        endpointSeekBar.getProgress() - beginSeekBar.getProgress());
                break;
            case selectMusicButton:
                Intent intent = new Intent("android.intent.action.GET_CONTENT");
                intent.setType("audio/*");
                launcherForBGM.launch(intent);
                break;
        }

    }

    @Override
    public void onProcessFinish(int code) {
        switch (code) {
            case PROCESS_SUCCESS:
                runOnUiThread(() -> {
                    MainActivityPresenter.checkItemList();
                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                });
                break;
            case PROCESS_CANCEL:
                runOnUiThread(() -> Toast.makeText(context, "Cancel", Toast.LENGTH_SHORT).show());
                break;
            case PROCESS_FAILURE:
                runOnUiThread(() -> Toast.makeText(context, "Failure", Toast.LENGTH_SHORT).show());
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        player.pause();
    }
}
