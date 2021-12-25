package com.example.galleryview.gallerypage;

import com.example.galleryview.model.GalleryItem;

public interface MainActivityInterface {
    void insertNewImage(GalleryItem newItem);


    void showHideSnackbar();

    void showUndoHideSnackbar();

    void showUndoRemoveSnackbar(GalleryItem item, int Position);

    void showFilterChooseDialog(CharSequence[] items, boolean[] checkedItems, long videoID);

    void showRemoveHiddenVideoSnackbar();
}
