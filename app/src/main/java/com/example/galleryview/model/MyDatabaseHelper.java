package com.example.galleryview.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    //还没来得及移除的 SQLite
    public static final String CREATE_BOOK = "create table Gallery  ("
            + "id integer primary key autoincrement, "
            + "imagepath text, "
            + "liked integer, "
            + "type integer)";

    public static final String CREATE_FILTER_BOOK = "create table Filters  ("
            + "id integer primary key autoincrement, "
            + "labelid int, "
            + "galleryid int)";

    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_BOOK);
        db.execSQL(CREATE_FILTER_BOOK);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void onClear(SQLiteDatabase db) {
        db.execSQL("drop table if exists Gallery");
        db.execSQL("drop table if exists Filters");
        onCreate(db);
    }
}
