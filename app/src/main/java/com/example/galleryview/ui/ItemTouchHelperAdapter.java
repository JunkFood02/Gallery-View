package com.example.galleryview.ui;

import com.example.galleryview.gallerypage.ItemAdapter;

public interface ItemTouchHelperAdapter {
    void onItemDelete(int position, ItemAdapter.ViewHolder viewHolder);
    void onItemHidden(int position,ItemAdapter.ViewHolder holder);
}
