package com.example.galleryview.videoplay;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.galleryview.R;
import com.example.galleryview.model.GalleryItem;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.StyledPlayerControlView;
import com.google.android.exoplayer2.ui.TimeBar;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FullScreenVideoAdapter extends RecyclerView.Adapter<FullScreenVideoAdapter.ViewHolder> {
    SwipeVideoPlayPresenter presenter;
    private static final String TAG = "FullScreenVideoAdapter";
    private List<GalleryItem> itemList;
    public static final int SINGLE_CLICK = 1;
    private static float PosX, PosY;
    private static final Map<Integer, VideoController> controllers = new HashMap<>();
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
                    holder.controlView.hideImmediately();
                    holder.timeBar.addListener(new TimeBar.OnScrubListener() {
                        final long durationSec = (holder.player.getDuration() / 1000);
                        final String dmin = durationSec / 600 + String.valueOf((durationSec / 60 % 10));
                        final String dsec = String.valueOf((durationSec % 60 / 10)) + durationSec % 10;
                        final String duration = dmin + ":" + dsec;

                        @Override
                        public void onScrubStart(TimeBar timeBar, long position) {
                            holder.videoProgress.setVisibility(View.VISIBLE);
                        }

                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onScrubMove(TimeBar timeBar, long position) {
                            int secs = (int) (position / 1000);
                            String min = secs / 600 + String.valueOf((secs / 60 % 10));
                            String sec = String.valueOf((secs % 60 / 10)) + secs % 10;
                            holder.videoProgress.setText(min + ":" + sec + " / " + duration);
                        }

                        @Override
                        public void onScrubStop(TimeBar timeBar, long position, boolean canceled) {
                            holder.videoProgress.setVisibility(View.INVISIBLE);
                            holder.noticeVideoRestart();
                        }
                    });
                }
            }
        });

        holder.itemView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                PosX = event.getX() - 180;
                PosY = event.getY() - 180;
            }
            return false;
        });
        holder.itemView.setOnClickListener(new onDoubleClickListener() {
            @Override
            public void onClick(View v) {
                super.onClick(v);
            }

            @Override
            public void onDoubleClick() {
                holder.heartAnimationLarge.setX(PosX);
                holder.heartAnimationLarge.setY(PosY);
                holder.heartAnimationLarge.playAnimation();
                holder.heartAnimationSmall.playAnimation();
                holder.heartAnimationLarge.setVisibility(View.VISIBLE);
                currentItem.doubleClickLike();
                holder.updateHeartCount(currentItem.getIS_LIKED());
            }

            @Override
            public void onSingleClick() {
                Message message = holder.handler.obtainMessage(SINGLE_CLICK);
                message.arg1 = holder.getLayoutPosition();
                holder.handler.sendMessage(message);
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
                Objects.requireNonNull(controllers.get(i)).noticeVideoPause();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements VideoController {

        PlayerView videoView;
        ImageView playButton;
        LottieAnimationView heartAnimationLarge, heartAnimationSmall;
        FrameLayout frameLayout;
        SimpleExoPlayer player;
        StyledPlayerControlView controlView;
        TextView heartCountText;
        TextView videoTitle, videoProgress;
        TimeBar timeBar;
        private final Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == SINGLE_CLICK) {
                    Objects.requireNonNull(controllers.get(msg.arg1)).changeVideoPlayStatus();
                }
            }
        };

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            videoView = itemView.findViewById(R.id.VideoPlayView);
            heartCountText = itemView.findViewById(R.id.heartCountText);
            frameLayout = itemView.findViewById(R.id.VideoFrame);
            playButton = itemView.findViewById(R.id.playButton);
            player = new SimpleExoPlayer.Builder(itemView.getContext()).build();
            videoTitle = itemView.findViewById(R.id.videoTitle);
            videoProgress = itemView.findViewById(R.id.videoProgressText);
            controlView = itemView.findViewById(R.id.controlView);
            controlView.setShowTimeoutMs(1000000);
            timeBar = controlView.findViewById(R.id.exo_progress);
            heartAnimationLarge = itemView.findViewById(R.id.doubleClickAnimation);
            heartAnimationSmall = itemView.findViewById(R.id.heartAnimationSmall);
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

        public void showCurrentPosition() {
            videoProgress.setVisibility(View.VISIBLE);
            final long durationSec = (player.getDuration() / 1000);
            final String dmin = durationSec / 600 + String.valueOf((durationSec / 60 % 10));
            final String dsec = String.valueOf((durationSec % 60 / 10)) + durationSec % 10;
            final String duration = dmin + ":" + dsec;
            int secs = (int) (player.getCurrentPosition() / 1000);
            String min = secs / 600 + String.valueOf((secs / 60 % 10));
            String sec = String.valueOf((secs % 60 / 10)) + secs % 10;
            videoProgress.setText(min + ":" + sec + " / " + duration);
        }

        public void updateHeartCount(int cnt) {
            heartCountText.setText("" + cnt);
        }

        @Override
        public void noticeVideoPause() {
            player.pause();
        }

        public void noticeVideoRestart() {
            if (player.getDuration() < player.getCurrentPosition() + 100L)
                player.seekTo(100);
            playButton.setVisibility(View.INVISIBLE);
            player.play();
            controlView.hide();
            videoProgress.setVisibility(View.INVISIBLE);
        }

        @Override
        public boolean changeVideoPlayStatus() {
            if (player.isPlaying()) {
                Log.d(TAG, "changeVideoPlayStatus: pause");
                noticeVideoPause();
                controlView.show();
                playButton.setVisibility(View.VISIBLE);
                showCurrentPosition();
                return true;
            } else {
                Log.d(TAG, "changeVideoPlayStatus: restart");
                noticeVideoRestart();
                return false;
            }
        }

        public void noticeVideoStart() {
            if (player.getDuration() < player.getCurrentPosition() + 2000L)
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
