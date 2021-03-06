package com.example.galleryview.gallerypage;

import static android.os.Looper.getMainLooper;
import static com.example.galleryview.gallerypage.MainActivity.HIDE_VIDEO;
import static com.example.galleryview.gallerypage.MainActivity.REMOVE_HIDDEN_VIDEO;
import static com.example.galleryview.gallerypage.MainActivity.SHOW_FILTER_CHOOSE_DIALOG;
import static com.example.galleryview.gallerypage.MainActivity.SHOW_FULLSCREEN_IMAGE;
import static com.example.galleryview.gallerypage.MainActivity.UNDO_HIDE_VIDEO;
import static com.example.galleryview.gallerypage.MainActivity.UNDO_REMOVE_IMAGE;
import static com.example.galleryview.model.DatabaseUtils.getAllVideoFromRoom;
import static com.example.galleryview.model.DatabaseUtils.insertVideo;

import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.galleryview.model.GalleryItem;
import com.example.galleryview.database.PrivateVideo;
import com.example.galleryview.database.Video;
import com.example.galleryview.model.DatabaseUtils;
import com.example.galleryview.model.PhotoSelector;

import java.util.ArrayList;
import java.util.List;

public class MainActivityPresenter {
    static private ItemAdapter adapter;
    private int position;
    private GalleryItem galleryItem;
    private Handler handler;
    private boolean[] ShowLabels;
    private final List<String> labels = new ArrayList<>();
    static MainActivityInterface mainActivityInterface;
    private static boolean EditorModeEnable = false;
    private static boolean PrivateModeEnable = false;

    public MainActivityPresenter(MainActivityInterface mainActivity) {
        initLabels();
        MainActivityPresenter.mainActivityInterface = mainActivity;
        setupHandler();
        List<GalleryItem> galleryItemList = new ArrayList<>();
        adapter = new ItemAdapter(galleryItemList, handler);
    }

    public void initLabels() {
        labels.add("Default Label");
        labels.add("Label 1");
        labels.add("Label 2");
        labels.add("Label 3");
        ShowLabels = new boolean[labels.size()];
        ShowLabels[0] = true;
    }


    public void readAlbumDataFromRoomDatabase() {
        adapter.clearList();
        List<Video> videos = getAllVideoFromRoom();
        for (Video v : videos
        ) {
            adapter.addImage(new GalleryItem(v.path, v.id, v.heartCount));
        }
    }

    public void clearAll() {
        DatabaseUtils.clearAllDataInRoom();
    }


    public static void addNewImage(Uri uri, int operationCode) {
        String imagePath;
        GalleryItem newItem;
        boolean IS_IMAGE = uri.toString().contains("image");
        imagePath = PhotoSelector.uriToPath(uri);
        if (imagePath != null) {
            if (IS_IMAGE)
                newItem = new GalleryItem(imagePath);
            else {
                newItem = new GalleryItem(imagePath, GalleryItem.TYPE_VIDEO);
                Toast.makeText(MainActivity.context, "Succeeded to load video.", Toast.LENGTH_SHORT).show();
            }

            if (operationCode == 1) {
                Video video = new Video(newItem.getImagePath(), newItem.getIS_LIKED());
                newItem.setId(insertVideo(video));
                DatabaseUtils.insertLabel(0, newItem.getId());
                mainActivityInterface.insertNewImage(newItem);
            }
        } else {
            Toast.makeText(MainActivity.context, "Failed to get image path.", Toast.LENGTH_SHORT).show();
        }
    }

    public static void reAddItem(GalleryItem currentItem) {
        DatabaseUtils.insertVideo(new Video(currentItem));
    }

    public Handler getHandler() {
        return handler;
    }

    public CharSequence[] getLabels() {
        return labels.toArray(new CharSequence[0]);
    }

    /**
     * ???????????????????????????????????????????????? ???????????????
     *
     * @param checkedItems ???????????????
     */
    public void reArrangeAdapter(boolean[] checkedItems) {
        List<Long> ids = new ArrayList<>();
        for (int i = 0; i < labels.size(); i++)
            if (checkedItems[i])
                ids.add((long) i);
        adapter.clearList();
        for (Video v : DatabaseUtils.getAllVideosByLabelIDs(ids)
        ) {
            adapter.addImage(new GalleryItem(v));
        }
    }

    private void setupHandler() {
        handler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case SHOW_FULLSCREEN_IMAGE:
                        position = msg.arg1;
                        galleryItem = (GalleryItem) msg.obj;
                        //mainActivityInterface.showFullscreenPhoto(galleryItem.getImagePath(), info);
                        //???????????????????????????????????????
                        break;
                    case UNDO_REMOVE_IMAGE:
                        galleryItem = (GalleryItem) msg.obj;
                        position = msg.arg1;
                        mainActivityInterface.showUndoRemoveSnackbar(galleryItem, position);
                        break;
                    case SHOW_FILTER_CHOOSE_DIALOG:
                        CharSequence[] items = labels.toArray(new CharSequence[0]);
                        galleryItem = (GalleryItem) msg.obj;
                        long videoID = galleryItem.getId();
                        boolean[] checkedItems = DatabaseUtils.findCheckedLabelsByVideoId(labels.size(), videoID);
                        mainActivityInterface.showFilterChooseDialog(items, checkedItems, videoID);
                        break;
                    case HIDE_VIDEO:
                        mainActivityInterface.showHideSnackbar();
                        break;
                    case UNDO_HIDE_VIDEO:
                        mainActivityInterface.showUndoHideSnackbar();
                        break;
                    case REMOVE_HIDDEN_VIDEO:
                        mainActivityInterface.showRemoveHiddenVideoSnackbar();
                }
            }
        };
    }

    public void showPrivateVideos() {
        adapter.clearList();
        for (PrivateVideo v : DatabaseUtils.getPrivateVideos()
        ) {
            adapter.addImage(new GalleryItem(v));
        }
    }

    public void updateLabels(boolean[] checkedItems, long videoID) {

        DatabaseUtils.insertLabels(checkedItems, videoID);
    }

    public static boolean isEditorModeEnable() {
        return EditorModeEnable;
    }

    public static void setEditorMode(boolean editorMode) {
        EditorModeEnable = editorMode;
    }

    public static boolean isPrivateModeEnable() {
        return PrivateModeEnable;
    }

    public static void setPrivateMode(boolean b) {
        PrivateModeEnable = b;
    }

    public ItemAdapter getAdapter() {
        return adapter;
    }

    public boolean[] getShowLabels() {
        return ShowLabels;
    }

    public static void checkItemList() {
        adapter.notifyItemRangeChanged(ItemAdapter.ItemList.size()-1, ItemAdapter.ItemList.size());
    }
}
