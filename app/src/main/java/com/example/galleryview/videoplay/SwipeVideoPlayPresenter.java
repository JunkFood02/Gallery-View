package com.example.galleryview.videoplay;

import android.util.Log;

import com.example.galleryview.model.GalleryItem;
import com.example.galleryview.database.Video;
import com.example.galleryview.model.DatabaseUtils;

import java.util.ArrayList;
import java.util.List;

public class SwipeVideoPlayPresenter {
    private static final String TAG = "SwipeVideoPlayPresenter";
    public SwipeVideoPlayInterface Interface;
    private int activePosition; //用于标记当前页面展示的视频 以实现自动播放

    public FullScreenVideoAdapter getAdapter() {
        return adapter;
    }

    private final FullScreenVideoAdapter adapter;

    public SwipeVideoPlayPresenter(SwipeVideoPlayInterface anInterface) {
        this.Interface = anInterface;
        List<GalleryItem> itemList = new ArrayList<>();
        adapter = new FullScreenVideoAdapter(this, itemList);
    }

    public void swipeToNextVideo(int position) {
        int NextPosition;
        if (position != adapter.getItemCount()) {
            NextPosition = position + 1;
            activePosition = NextPosition;
            Interface.swipeToNextVideo(NextPosition, adapter.getItemCount());
            Log.d(TAG, "swipeToNextVideo: NextPosition = " + NextPosition);
        } else
            activePosition = 0;
    }



    public int getActivePosition() {
        return activePosition;
    }

    public void setActivePosition(int activePosition) {
        this.activePosition = activePosition;
    }

    public void getVideoFromItemList(List<GalleryItem> itemList) {
        this.adapter.setItemList(itemList);
        //adapter.notifyItemChanged(activePosition);

    }

    public void getVideoFromDatabase(boolean FILTER_ENABLE) {
        if (!FILTER_ENABLE) {
            List<Video> videos = DatabaseUtils.getAllVideoFromRoom();
            for (Video v : videos
            ) {
                adapter.addVideo(new GalleryItem(v));
            }
        }
    }
}
