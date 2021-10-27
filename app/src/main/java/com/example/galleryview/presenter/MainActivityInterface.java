package com.example.galleryview.presenter;

import com.bm.library.Info;
import com.example.galleryview.model.GalleryItem;

public interface MainActivityInterface {
    void insertNewImage(GalleryItem newItem);

    void showFullscreenPhoto(String path, Info imageInfo);

    void showUndoHideSnackbar(GalleryItem item, int Position);

    void showUndoRemoveSnackbar(GalleryItem item, int Position);

    void showFilterChooseDialog(CharSequence[] items, boolean[] checkedItems, long videoID);
}
