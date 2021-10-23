package com.example.galleryview.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    public static final String CREATE_BOOK = "create table Gallery  ("
            + "id integer primary key autoincrement, "
            + "imagepath text, "
            + "liked integer, "
            + "type integer)";

    private Context context;

    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_BOOK);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists Gallery");
        onCreate(db);
    }
    public void onClear(SQLiteDatabase db)
    {
        db.execSQL("drop table if exists Gallery");
        onCreate(db);
    }
}
