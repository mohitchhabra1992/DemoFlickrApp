package com.example.mohit.demoflickrsearch;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDatabase extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "flickrDemo.db";
    public static final String TABLE_NAME = "SearchResult";
    public static final String COLUMN_ID = "SearchUrl";
    public static final String COLUMN_NAME = "SearchResponse";

    public MyDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + COLUMN_ID +
                " TEXT PRIMARY KEY," + COLUMN_NAME + " BLOB)";
        Log.d("MyDatabase","onCreate MyDb query = " + CREATE_TABLE);
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}
