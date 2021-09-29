package com.example.galleryview;

import static com.example.galleryview.MainActivity.BOOK_TITLE;
import static com.example.galleryview.MainActivity.instance;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
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

import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> implements ItemTouchHelperAdapter {
    private static final String TAG = "ItemAdapter";
    SQLiteDatabase db;
    MyDatabaseHelper helper;

    private List<GalleryItem> ItemList = new ArrayList<>();

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
        helper = new MyDatabaseHelper(instance.getApplicationContext(), "Gallery.db", null, 1);
        db = helper.getWritableDatabase();
        holder.cardView.setOnClickListener(v -> {
            int position = holder.getLayoutPosition();
            Intent intent = new Intent(v.getContext(), BigPictureActivity.class);
            GalleryItem item = ItemList.get(position);
            intent.putExtra("path", item.getImagePath());
            intent.putExtra("media_type", item.getType());
            v.getContext().startActivity(intent);
        });


        return holder;
    }

    public ItemAdapter(List<GalleryItem> itemList) {
        ItemList = itemList;
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
            bitmap = BitmapFactory.decodeFile(galleryItem.getImagePath());
            holder.textView.setText("Image Title " + position);
        } else {
            bitmap = createThumbnailAtTime(galleryItem.getImagePath(), 1);//生成第一秒的截图
            holder.textView.setText("Video Title " + position);
        }
        if (bitmap != null) {
            holder.imageView.setImageBitmap(bitmap);
        } else {
            Toast.makeText(instance.getApplicationContext(), "Failed to get bitmap.", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(instance.getApplicationContext(), "Like", Toast.LENGTH_SHORT).show();
            } else {
                holder.lottieAnimationView.setSpeed((float) -1.0);
                holder.lottieAnimationView.playAnimation();
                galleryItem.clickLike();
                Toast.makeText(instance.getApplicationContext(), "Like Undo", Toast.LENGTH_SHORT).show();
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
        helper = new MyDatabaseHelper(instance.getApplicationContext(), "Gallery.db", null, 1);
        db = helper.getWritableDatabase();
        db.delete(BOOK_TITLE, "id=?", new String[]{"" + currentItem.getId()});
        ItemList.remove(position);
        notifyItemRemoved(position);
        instance.undoRemove(currentItem, position);
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
