package com.example.galleryview.videoplay;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.galleryview.R;
import com.example.galleryview.database.GalleryItem;
import com.example.galleryview.ui.VideoController;
import com.example.galleryview.ui.onDoubleClickListener;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FullScreenVideoAdapter extends RecyclerView.Adapter<FullScreenVideoAdapter.ViewHolder> {
    SwipeVideoPlayPresenter presenter;
    private static final String TAG = "FullScreenVideoAdapter";
    private List<GalleryItem> itemList;
    private final Map<Integer, VideoController> controllers = new HashMap<>();
    // 因为实在搞不懂 RecyclerView 的回收缓存机制 所以用了这种取巧的方法
    // 将每个 ViewHolder 与其位置的建立映射加入 map 方便调用

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
        holder.videoTitle.setText("视频标题 " + Position);
        holder.updateHeartCount(currentItem.getIS_LIKED());
        MediaItem mediaItem = MediaItem.fromUri(currentItem.getImagePath());
        holder.player.setMediaItem(mediaItem);
        holder.player.setPlayWhenReady(position == presenter.getActivePosition());
        holder.player.prepare();
        holder.player.seekTo(100);
        holder.player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == ExoPlayer.STATE_ENDED) //播放停止自动滑动到下一视频
                    presenter.swipeToNextVideo(Position);
                else if (playbackState == ExoPlayer.STATE_READY) {
                    holder.controlView.setPlayer(holder.player);
                }
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
            public void onClick(View v) {
                super.onClick(v);
                if (!holder.controlView.isVisible())
                    holder.controlView.show();
            }

            @Override
            public void onDoubleClick() {
                holder.heartAnimationLarge.playAnimation();
                holder.heartAnimationSmall.playAnimation();
                holder.heartAnimationLarge.setVisibility(View.VISIBLE);
                currentItem.doubleClickLike();
                holder.updateHeartCount(currentItem.getIS_LIKED());

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

    public void VideoRestart(int position) {
        if (controllers.get(position) != null) {
            Log.d(TAG, "VideoStart: position = " + position);
            Objects.requireNonNull(controllers.get(position)).noticeVideoRestart();
        }

    }

    public void VideoStop() {
        for (int i = 0; i <= getItemCount(); i++)
            if (controllers.containsKey(i))
                Objects.requireNonNull(controllers.get(i)).noticeVideoStop();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements VideoController {
        PlayerView videoView;
        LottieAnimationView heartAnimationLarge, heartAnimationSmall;
        FrameLayout frameLayout;
        SimpleExoPlayer player;
        PlayerControlView controlView;
        TextView textView, videoTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            videoView = itemView.findViewById(R.id.VideoPlayView);
            textView = itemView.findViewById(R.id.heartCountText);
            frameLayout = itemView.findViewById(R.id.VideoFrame);
            player = new SimpleExoPlayer.Builder(itemView.getContext()).build();
            videoTitle = itemView.findViewById(R.id.videoTitle);
            controlView = itemView.findViewById(R.id.controlView);
            heartAnimationLarge = itemView.findViewById(R.id.doubleClickAnimation);
            heartAnimationSmall = itemView.findViewById(R.id.heartAnimationSmall);
            heartAnimationLarge.setScaleX((float) 2);
            heartAnimationLarge.setScaleY((float) 2);
            heartAnimationLarge.setSpeed((float) 1);
            heartAnimationLarge.setVisibility(View.INVISIBLE);
            heartAnimationLarge.addAnimatorListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    heartAnimationLarge.setVisibility(View.INVISIBLE); //双击点赞 播放完成后自动消失
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    heartAnimationLarge.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });

        }

        public void updateHeartCount(int cnt) {
            textView.setText("" + cnt);
        }

        @Override
        public void noticeVideoStop() {
            player.pause();
        }

        public void noticeVideoRestart() {
            player.play();
        }

        public void noticeVideoStart() {
            if (player.getDuration() < player.getCurrentPosition() * 2) //视频播放进度过半则从头开始播放
                player.seekTo(100);
            player.prepare();
            player.play();
        }

    }

    public void addVideo(GalleryItem item) {
        itemList.add(item);
    }

    public void setItemList(List<GalleryItem> itemList) {
        this.itemList = itemList;
    }
}
