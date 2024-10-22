package com.example.Database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Helper extends SQLiteOpenHelper {
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_POINTS = "points";
    public static final String COLUMN_NOTE = "note";
    public static final String TABLE_CUSTOMER_POINTS = "customer_points";

    private static final String DATABASE_NAME = "customer_points.db";
    private static final int DATABASE_VERSION = 1;

    public Helper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CUSTOMER_POINTS_TABLE = "CREATE TABLE " + TABLE_CUSTOMER_POINTS + " (" +
                COLUMN_PHONE + " TEXT PRIMARY KEY, " +
                COLUMN_POINTS + " INTEGER DEFAULT 0, " +
                COLUMN_NOTE + " TEXT" +
                ")";
        db.execSQL(CREATE_CUSTOMER_POINTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CUSTOMER_POINTS);
        onCreate(db);
    }
}

