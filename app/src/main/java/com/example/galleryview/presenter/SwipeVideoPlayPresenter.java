package com.example.galleryview.presenter;

import android.database.Cursor;
import android.util.Log;

import com.example.galleryview.dao.Video;
import com.example.galleryview.ui.FullScreenVideoAdapter;
import com.example.galleryview.model.DatabaseUtils;
import com.example.galleryview.model.GalleryItem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SwipeVideoPlayPresenter {
    private static final String TAG = "SwipeVideoPlayPresenter";
    public SwipeVideoPlayInterface Interface;
    private int activePosition;
    private List<GalleryItem> itemList = new ArrayList<>();
    private boolean FILTER_ENABLE;
    private List<String> labels = new ArrayList<>();

    public FullScreenVideoAdapter getAdapter() {
        return adapter;
    }

    private FullScreenVideoAdapter adapter;

    public SwipeVideoPlayPresenter(SwipeVideoPlayInterface anInterface) {
        this.Interface = anInterface;
        adapter = new FullScreenVideoAdapter(this, itemList);
        initLabels();
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

    public void initLabels() {
        labels.add("Default Label 1");
        labels.add("Default Label 2");
        labels.add("Default Label 3");
        labels.add("Default Label 4");
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

    public void getVideoFromDatabase(boolean FILTER_ENABLE) throws ExecutionException, InterruptedException {
        if (!FILTER_ENABLE) {
            List<Video> videos = DatabaseUtils.getAllVideoFromRoom();
            for (Video v : videos
            ) {
                adapter.addVideo(new GalleryItem(v));
            }
        }
    }
}
