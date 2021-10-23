package com.example.galleryview.presenter;

import com.bm.library.Info;
import com.example.galleryview.model.GalleryItem;

public interface MainActivityInterface {
    void insertNewImage(GalleryItem newItem);

    void showFullscreenPhoto(String path, Info imageInfo);

    void showUndoRemoveSnackbar(GalleryItem item, int Position);

}
