package com.example.galleryview;

import com.example.galleryview.ui.ItemAdapter;

public interface ItemTouchHelperAdapter {
    void onItemDelete(int position, ItemAdapter.ViewHolder viewHolder);

}
