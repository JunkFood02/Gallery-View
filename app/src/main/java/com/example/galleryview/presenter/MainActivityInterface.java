package com.example.galleryview.presenter;

import com.bm.library.Info;

public interface MainActivityInterface {
    void insertNewImage(GalleryItem newItem);

    void showFullscreenPhoto(String path, Info imageInfo);

    void showHideSnackbar();

    void showUndoHideSnackbar();

    void showUndoRemoveSnackbar(GalleryItem item, int Position);

    void showFilterChooseDialog(CharSequence[] items, boolean[] checkedItems, long videoID);

    void showRemoveHiddenVideoSnackbar();
}
