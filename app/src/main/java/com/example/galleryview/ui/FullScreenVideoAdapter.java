package com.example.galleryview;

import static com.example.galleryview.MyActivity.context;
import static com.example.galleryview.MyActivity.getContext;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.galleryview.model.GalleryItem;
import com.example.galleryview.presenter.SwipeVideoPlayPresenter;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.BaseMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class FullScreenVideoAdapter extends RecyclerView.Adapter<FullScreenVideoAdapter.ViewHolder> {
    SwipeVideoPlayPresenter presenter;
    private static final String TAG = "FullScreenVideoAdapter";
    private List<GalleryItem> itemList;
    private Map<Integer, VideoController> controllers = new HashMap<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int Position = holder.getLayoutPosition();
        Log.d(TAG, "onBindViewHolder: position = " + Position);
        controllers.put(Position, holder);
        GalleryItem currentItem = itemList.get(Position);
        holder.videoView.setPlayer(holder.player);
        MediaItem mediaItem = MediaItem.fromUri(currentItem.getImagePath());
        holder.player.setMediaItem(mediaItem);
        holder.player.setPlayWhenReady(position == presenter.getActivePosition());
        holder.player.prepare();
        holder.controlView.setPlayer(holder.player);
        holder.player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == ExoPlayer.STATE_ENDED)
                    presenter.swipeToNextVideo(Position);
            }
        });
        /*
        holder.mediaController=new MediaController(holder.itemView.getContext());
        holder.mediaController.setMediaPlayer(holder.videoView);
        holder.videoView.setMediaController(holder.mediaController);
        holder.videoView.setVideoPath(currentItem.getImagePath());
        holder.videoView.setOnPreparedListener(mp -> {
            if (Position == presenter.getActivePosition())
                mp.start();
            mp.seekTo(1);
        });
        holder.videoView.setOnCompletionListener(mp -> presenter.swipeToNextVideo(Position));
        */

        holder.itemView.setOnClickListener(new onDoubleClickListener() {
            @Override
            public void onDoubleClick() {
                holder.lottieAnimationView.playAnimation();
                holder.lottieAnimationView.setVisibility(View.VISIBLE);
            }
        });


    }

    public FullScreenVideoAdapter(SwipeVideoPlayPresenter presenter, List<GalleryItem> itemList) {
        this.presenter = presenter;
        this.itemList = itemList;
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void VideoStart(int position) {
        if (controllers.get(position) != null) {
            Log.d(TAG, "VideoStart: position = " + position);
            Objects.requireNonNull(controllers.get(position)).noticeVideoStart();
        }

    }

    public void VideoStop() {
        for (int i = 0; i <= getItemCount(); i++)
            if (controllers.containsKey(i))
                Objects.requireNonNull(controllers.get(i)).noticeVideoStop();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements VideoController {
        PlayerView videoView;
        LottieAnimationView lottieAnimationView;
        FrameLayout frameLayout;
        MediaController mediaController;
        SimpleExoPlayer player;
        PlayerControlView controlView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            videoView = itemView.findViewById(R.id.VideoPlayView);
            frameLayout = itemView.findViewById(R.id.VideoFrame);
            player = new SimpleExoPlayer.Builder(itemView.getContext()).build();
            controlView= itemView.findViewById(R.id.controlView);
            lottieAnimationView = itemView.findViewById(R.id.doubleClickAnimation);
            lottieAnimationView.setScaleX((float) 5);
            lottieAnimationView.setScaleY((float) 5);
            lottieAnimationView.setAnimation("heart.json");
            lottieAnimationView.setVisibility(View.INVISIBLE);
            lottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    lottieAnimationView.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });

        }

        @Override
        public void noticeVideoStop() {
            player.stop();
        }

        public void noticeVideoStart() {
            player.prepare();
            player.seekTo(0);
            player.play();
        }
    }

    public void addVideo(GalleryItem item) {
        itemList.add(item);
    }
}
