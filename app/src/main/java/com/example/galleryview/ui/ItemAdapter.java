package com.example.galleryview.ui;

import static com.example.galleryview.model.DatabaseUtils.insertVideo;
import static com.example.galleryview.presenter.MainActivityPresenter.isEditorModeEnable;
import static com.example.galleryview.presenter.MainActivityPresenter.isPrivateModeEnable;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.galleryview.MainActivity;
import com.example.galleryview.R;
import com.example.galleryview.SwipeVideoPlayActivity;
import com.example.galleryview.VideoEditorActivity;
import com.example.galleryview.dao.Video;
import com.example.galleryview.model.DatabaseUtils;
import com.example.galleryview.presenter.GalleryItem;

import java.io.File;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> implements ItemTouchHelperAdapter {
    private static final String TAG = "ItemAdapter";

    Handler handler;
    public ViewHolder holder;
    public static List<GalleryItem> ItemList;

    @SuppressLint("NotifyDataSetChanged")
    public void clearList() {
        while (!ItemList.isEmpty()) {
            ItemList.remove(0);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        this.holder = holder;
        return holder;
    }

    public ItemAdapter(List<GalleryItem> itemList, Handler handler) {
        this.handler = handler;
        ItemList = itemList;
    }

    private Bitmap createThumbnailAtTime(String filePath, int timeInSeconds) {
        MediaMetadataRetriever mMMR = new MediaMetadataRetriever();
        mMMR.setDataSource(filePath);
        //api time unit is microseconds
        return mMMR.getFrameAtTime(timeInSeconds * 1000000L, MediaMetadataRetriever.OPTION_CLOSEST);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GalleryItem galleryItem = ItemList.get(holder.getLayoutPosition());
        Log.d(TAG, "ImagePath : " + galleryItem.getImagePath());
        Bitmap bitmap;
        if (galleryItem.getType() == GalleryItem.TYPE_IMAGE) {
            Glide.with(MainActivity.getContext()).load(new File(galleryItem.getImagePath())).into(holder.imageView);
            holder.textView.setText("Image Title " + holder.getLayoutPosition());
        } else {
            //galleryItem.setBitmap(createThumbnailAtTime(galleryItem.getImagePath(), 1));//生成第一秒的截图
            holder.textView.setText("Video Title " + holder.getLayoutPosition());
            Glide.with(holder.imageView).load((galleryItem.getImagePath())).into(holder.imageView);
        }
        //Toast.makeText(MainActivity.getContext(), "Failed to get bitmap.", Toast.LENGTH_SHORT).show();


        /*if (galleryItem.IS_LIKED()) {
            holder.lottieAnimationView.setProgress((float) 1.0);
        }
        holder.lottieAnimationView.setOnClickListener(v -> {
            if (!galleryItem.IS_LIKED()) {
                holder.lottieAnimationView.setSpeed((float) 1.0);
                holder.lottieAnimationView.playAnimation();
                Toast.makeText(MainActivity.getContext(), "Like", Toast.LENGTH_SHORT).show();
            } else {
                holder.lottieAnimationView.setSpeed((float) -1.0);
                holder.lottieAnimationView.playAnimation();
                Toast.makeText(MainActivity.getContext(), "Like Undo", Toast.LENGTH_SHORT).show();
            }
        });*/
        holder.cardView.setOnClickListener(v -> {
            Intent intent;
            if (!isEditorModeEnable()) {
                intent = new Intent(v.getContext(), SwipeVideoPlayActivity.class);
                intent.putExtra("position", holder.getLayoutPosition());
                //这里写进入短视频 Activity 的逻辑
            }
            else{
                intent=new Intent(v.getContext(), VideoEditorActivity.class);
                intent.putExtra("path",galleryItem.getImagePath());
                //进入视频编辑器 Activity
            }
            v.getContext().startActivity(intent);
        });
        holder.cardView.setOnLongClickListener(v -> {
            if (isPrivateModeEnable() || isEditorModeEnable()) return true;
            Message message = handler.obtainMessage(MainActivity.SHOW_FILTER_CHOOSE_DIALOG);
            message.obj = galleryItem;
            handler.sendMessage(message);
            //长按弹出标签选择 Dialog
            return true;
        });
    }

    public void addImage(GalleryItem galleryItem) {
        insertImage(galleryItem, getItemCount());
    }

    public void insertImage(GalleryItem galleryItem, int position) {
        ItemList.add(position, galleryItem);
        notifyItemInserted(position);
        Log.d(TAG, "" + position);

    }

    @Override
    public int getItemCount() {
        return ItemList.size();
    }

    @Override
    public void onItemDelete(int position, ViewHolder holder) {
        GalleryItem currentItem = ItemList.get(position);
        ItemList.remove(position);
        notifyItemRemoved(position);
        if (!isPrivateModeEnable()) {
            Log.d(TAG, "onItemDelete: id = " + currentItem.getId());
            DatabaseUtils.deleteVideoByID(currentItem.getId());
            Message message = handler.obtainMessage(MainActivity.UNDO_REMOVE_IMAGE);
            message.obj = currentItem;
            message.arg1 = position;
            handler.sendMessage(message);
        } else {
            DatabaseUtils.deletePrivateVideoByID(currentItem.getId());
            Message message = handler.obtainMessage(MainActivity.REMOVE_HIDDEN_VIDEO);
            handler.sendMessage(message);
        }
    }

    @Override
    public void onItemHidden(int position, ViewHolder holder) {
        GalleryItem currentItem = ItemList.get(position);
        Log.d(TAG, "onItemHidden: id = " + currentItem.getId());
        ItemList.remove(position);
        notifyItemRemoved(position);
        if (!isPrivateModeEnable()) {
            DatabaseUtils.hideVideo(currentItem);
            Message message = handler.obtainMessage(MainActivity.HIDE_VIDEO);
            handler.sendMessage(message);
        } else {
            DatabaseUtils.deletePrivateVideoByID(currentItem.getId());
            Video video = new Video(currentItem);
            currentItem.setId(insertVideo(video));
            DatabaseUtils.insertLabel(0, currentItem.getId());
            Message message = handler.obtainMessage(MainActivity.UNDO_HIDE_VIDEO);
            handler.sendMessage(message);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        LottieAnimationView lottieAnimationView;
        TextView textView;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardView);
            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.editText);
            /*lottieAnimationView = itemView.findViewById(R.id.animate);
            lottieAnimationView.setScaleX((float) 1.5);
            lottieAnimationView.setScaleY((float) 1.5);
            lottieAnimationView.setAnimation("heart.json");*/
        }
    }

}
