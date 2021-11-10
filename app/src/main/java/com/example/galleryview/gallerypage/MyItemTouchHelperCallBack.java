package com.example.galleryview.gallerypage;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class MyItemTouchHelperCallBack extends ItemTouchHelper.Callback {
    private final ItemTouchHelperAdapter adapter;

    public MyItemTouchHelperCallBack(ItemTouchHelperAdapter adapter) {
        this.adapter = adapter;
    }


    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        if ((direction & ItemTouchHelper.LEFT) != 0) //左滑删除 右滑隐藏
            adapter.onItemDelete(viewHolder.getLayoutPosition(), (ItemAdapter.ViewHolder) viewHolder);
        else
            adapter.onItemHidden(viewHolder.getLayoutPosition(), (ItemAdapter.ViewHolder) viewHolder);
    }

}
