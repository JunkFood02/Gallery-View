package com.example.galleryview;

import static com.example.galleryview.MainActivity.BOOK_TITLE;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bm.library.Info;
import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> implements ItemTouchHelperAdapter {
    private static final String TAG = "ItemAdapter";
    SQLiteDatabase db;
    MyDatabaseHelper helper;
    Handler handler;
    public ViewHolder holder;
    private List<GalleryItem> ItemList = new ArrayList<>();

    public ItemAdapter(List<GalleryItem> itemList,Handler handler) {
        this.handler = handler;
        ItemList = itemList;
    }

    public void clearList() {
        while (!ItemList.isEmpty()) {
            notifyItemRemoved(0);
            ItemList.remove(0);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        helper = new MyDatabaseHelper(MainActivity.getContext(), "Gallery.db", null, 1);
        db = helper.getWritableDatabase();

        this.holder =holder;
        return holder;
    }



    private Bitmap createThumbnailAtTime(String filePath, int timeInSeconds) {
        MediaMetadataRetriever mMMR = new MediaMetadataRetriever();
        mMMR.setDataSource(filePath);
        //api time unit is microseconds
        return mMMR.getFrameAtTime(timeInSeconds * 1000000, MediaMetadataRetriever.OPTION_CLOSEST);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GalleryItem galleryItem = ItemList.get(position);
        Log.d(TAG, "ImagePath : " + galleryItem.getImagePath());
        Bitmap bitmap;
        if (galleryItem.getType() == GalleryItem.TYPE_IMAGE) {
            Glide.with(MainActivity.getContext()).load(new File(galleryItem.getImagePath())).into(holder.imageView);
            holder.textView.setText("Image Title " + position);
        } else {
            bitmap = createThumbnailAtTime(galleryItem.getImagePath(), 1);//生成第一秒的截图
            holder.textView.setText("Video Title " + position);
            if (bitmap != null) {
                holder.imageView.setImageBitmap(bitmap);
            } else {
                Toast.makeText(MainActivity.getContext(), "Failed to get bitmap.", Toast.LENGTH_SHORT).show();
            }
        }
        if (galleryItem.IS_LIKED()) {
            holder.lottieAnimationView.setProgress((float) 1.0);
            Log.d(TAG, "LIKE!");
        }
        holder.lottieAnimationView.setOnClickListener(v -> {
            if (!galleryItem.IS_LIKED()) {
                holder.lottieAnimationView.setSpeed((float) 1.0);
                holder.lottieAnimationView.playAnimation();
                galleryItem.clickLike();
                Toast.makeText(MainActivity.getContext(), "Like", Toast.LENGTH_SHORT).show();
            } else {
                holder.lottieAnimationView.setSpeed((float) -1.0);
                holder.lottieAnimationView.playAnimation();
                galleryItem.clickLike();
                Toast.makeText(MainActivity.getContext(), "Like Undo", Toast.LENGTH_SHORT).show();
            }
        });
        holder.cardView.setOnClickListener(v -> {
            if (galleryItem.getType() == GalleryItem.TYPE_VIDEO) {
                Intent intent = new Intent(v.getContext(), BigPictureActivity.class);
                intent.putExtra("path", galleryItem.getImagePath());
                intent.putExtra("media_type", galleryItem.getType());
                v.getContext().startActivity(intent);
            } else {
                Info info = PhotoView.getImageViewInfo(holder.imageView);
                Message message=handler.obtainMessage(MainActivity.SHOW_FULLSCREEN_IMAGE);
                galleryItem.setInfo(info);
                message.obj=galleryItem;
                handler.sendMessage(message);
            }
        });
    }

    public void addImage(GalleryItem galleryItem) {
        insertImage(galleryItem, 0);
    }

    public void insertImage(GalleryItem galleryItem, int position) {
        ItemList.add(position, galleryItem);
        notifyItemInserted(position);
    }

    @Override
    public int getItemCount() {
        return ItemList.size();
    }

    @Override
    public void onItemDelete(int position, ViewHolder holder) {
        GalleryItem currentItem = ItemList.get(position);
        helper = new MyDatabaseHelper(MainActivity.getContext(), "Gallery.db", null, 1);
        db = helper.getWritableDatabase();
        db.delete(BOOK_TITLE, "id=?", new String[]{"" + currentItem.getId()});
        ItemList.remove(position);
        notifyItemRemoved(position);
        Message message=handler.obtainMessage(MainActivity.UNDO_REMOVE);
        message.obj=currentItem;
        message.arg1=position;
        handler.sendMessage(message);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View ItemView;
        ImageView imageView;
        LottieAnimationView lottieAnimationView;
        TextView textView;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG, "init: ");
            cardView = itemView.findViewById(R.id.cardView);
            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.editText);
            lottieAnimationView = itemView.findViewById(R.id.animate);
            lottieAnimationView.setScaleX((float) 1.5);
            lottieAnimationView.setScaleY((float) 1.5);
            lottieAnimationView.setAnimation("heart.json");
        }
    }

}
