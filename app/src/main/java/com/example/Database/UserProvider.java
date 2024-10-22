package com.example.Database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.content.ContentUris;

public class UserProvider extends ContentProvider {
    private static final String AUTHORITY = "com.example.app.Login";
    private static final String PATH_USER_LIST = "users";
    private static final String PATH_USER_BY_USERNAME = "user";


    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + PATH_USER_LIST);
    public static final Uri CONTENT_URI_BY_USERNAME = Uri.parse("content://" + AUTHORITY + "/" + PATH_USER_BY_USERNAME);

    private SQLiteDatabase database;
    private static final int USERS = 1;
    private static final int USER = 2;

    // UriMatcher để ánh xạ giữa URI và hành động tương ứng
    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, PATH_USER_LIST, USERS);
        sURIMatcher.addURI(AUTHORITY, PATH_USER_BY_USERNAME + "/*", USER);
    }

    @Override
    public boolean onCreate() {
        DBHelper dbHelper = new DBHelper(getContext());
        database = dbHelper.getWritableDatabase();
        return database != null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        switch (sURIMatcher.match(uri)) {
            case USERS:
                cursor = database.query(DBHelper.TABLE_USER, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case USER:
                String username = uri.getLastPathSegment();
                cursor = database.query(DBHelper.TABLE_USER, projection, DBHelper.COLUMN_USERNAME + "=?", new String[]{username}, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id = database.insert(DBHelper.TABLE_USER, null, values);
        if (id > 0) {
            Uri userUri = ContentUris.withAppendedId(CONTENT_URI, id);
            getContext().getContentResolver().notifyChange(userUri, null);
            return userUri;
        } else {
            throw new SQLException("Failed to insert row into " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int rowsUpdated;
        switch (sURIMatcher.match(uri)) {
            case USERS:
                rowsUpdated = database.update(DBHelper.TABLE_USER, values, selection, selectionArgs);
                break;
            case USER:
                String username = uri.getLastPathSegment();
                rowsUpdated = database.update(DBHelper.TABLE_USER, values, DBHelper.COLUMN_USERNAME + "=?", new String[]{username});
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int rowsDeleted;
        switch (sURIMatcher.match(uri)) {
            case USERS:
                rowsDeleted = database.delete(DBHelper.TABLE_USER, selection, selectionArgs);
                break;
            case USER:
                String username = uri.getLastPathSegment();
                rowsDeleted = database.delete(DBHelper.TABLE_USER, DBHelper.COLUMN_USERNAME + "=?", new String[]{username});
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }
}
