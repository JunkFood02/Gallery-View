package com.example.galleryview.ui;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class MyItemTouchHelperCallBack extends ItemTouchHelper.Callback {
    private final ItemTouchHelperAdapter adapter;
    private boolean swipeEnable = true;

    public MyItemTouchHelperCallBack(ItemTouchHelperAdapter adapter) {
        this.adapter = adapter;
    }

    public MyItemTouchHelperCallBack(boolean swipeEnable) {
        this.swipeEnable = swipeEnable;
        adapter = null;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return swipeEnable;
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
        if ((direction & ItemTouchHelper.LEFT) != 0)
            adapter.onItemDelete(viewHolder.getLayoutPosition(), (ItemAdapter.ViewHolder) viewHolder);
        else
            adapter.onItemHidden(viewHolder.getLayoutPosition(), (ItemAdapter.ViewHolder) viewHolder);
    }

}
