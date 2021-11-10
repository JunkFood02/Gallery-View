package com.example.galleryview.gallerypage;

import com.example.galleryview.gallerypage.ItemAdapter;

public interface ItemTouchHelperAdapter {
    void onItemDelete(int position, ItemAdapter.ViewHolder viewHolder);
    void onItemHidden(int position,ItemAdapter.ViewHolder holder);
}
