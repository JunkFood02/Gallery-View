package com.example.galleryview.videoplay;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.galleryview.R;
import com.example.galleryview.model.MyActivity;
import com.example.galleryview.gallerypage.ItemAdapter;

import java.util.concurrent.ExecutionException;

public class SwipeVideoPlayActivity extends MyActivity implements SwipeVideoPlayInterface {
    private static final String TAG = "SwipeVideoPlayActivity";
    SwipeVideoPlayPresenter presenter;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    PagerSnapHelper pagerSnapHelper;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_video_play);
        //Objects.requireNonNull(getSupportActionBar()).hide();

        try {
            init();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void init() throws ExecutionException, InterruptedException {
        int position = getIntent().getIntExtra("position", 0);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE|View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        textView = findViewById(R.id.heartCountText);
        recyclerView = findViewById(R.id.VideoRecyclerView);
        presenter = new SwipeVideoPlayPresenter(this);
        presenter.setActivePosition(position);
        presenter.getVideoFromItemList(ItemAdapter.ItemList);
        recyclerView.setAdapter(presenter.getAdapter());
        layoutManager = new LinearLayoutManager(MyActivity.context, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(recyclerView);


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            /**
             * 根据滑动状态控制视频的播放与暂停
             * @param newState 视图的滚动状态
             */
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int currentPosition = layoutManager.findFirstCompletelyVisibleItemPosition();
                    presenter.setActivePosition(currentPosition);
                    Log.d(TAG, "onScrollStateChanged: currentPosition=" + currentPosition);
                    presenter.getAdapter().VideoStart(currentPosition);
                    /*
                    当滚动停止时 使当前位置视频开始播放 即实现滑动后自动播放
                    使用滑动停止的状态就可以不用判断上滑或是下滑
                    */
                } else if (newState == RecyclerView.SCREEN_STATE_ON) {
                    presenter.getAdapter().VideoStop();//当滑动开始时 使所有位置视频停止播放
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        Log.d(TAG, "init: position = " + position);
        recyclerView.scrollToPosition(position);
    }

    @Override
    public void swipeToNextVideo(int position, int itemCount) {
        recyclerView.smoothScrollToPosition(position);
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.getAdapter().VideoStop();//锁屏时停止播放
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.getAdapter().VideoRestart(layoutManager.findFirstCompletelyVisibleItemPosition());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.getAdapter().VideoStop();
        presenter = null;
    }
}