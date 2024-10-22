package com.example.Database;

import static android.app.PendingIntent.getActivity;

import static com.example.Database.Helper.COLUMN_PHONE;
import static com.example.Database.Helper.TABLE_CUSTOMER_POINTS;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.content.UriMatcher;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CustomerPointsProvider extends ContentProvider {
    private static final int CUSTOMER_POINTS_PHONE = 2;

    static {
        sURIMatcher.addURI(AUTHORITY, PATH_CUSTOMER_POINTS + "/*", CUSTOMER_POINTS_PHONE);
    }

    private static final String AUTHORITY = "com.example.app.CustomerPoints";
    private static final String PATH_CUSTOMER_POINTS = "customer_points";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + PATH_CUSTOMER_POINTS);

    private SQLiteDatabase database;
    private static final int CUSTOMER_POINTS = 1;

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, PATH_CUSTOMER_POINTS, CUSTOMER_POINTS);
    }

    @Override
    public boolean onCreate() {
        Helper Helper = new Helper(getContext());
        database = Helper.getWritableDatabase();
        return database != null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        switch (sURIMatcher.match(uri)) {
            case CUSTOMER_POINTS_PHONE:
                selection = COLUMN_PHONE + " = ?";
                selectionArgs = new String[]{uri.getLastPathSegment()};
                break;
            case CUSTOMER_POINTS:
                // Do nothing, use defaults
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        Cursor cursor = database.query(TABLE_CUSTOMER_POINTS, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id = database.insert(TABLE_CUSTOMER_POINTS, null, values);
        if (id > 0) {
            Uri customerUri = ContentUris.withAppendedId(CONTENT_URI, id);
            getContext().getContentResolver().notifyChange(customerUri, null);
            return customerUri;
        } else {
            throw new SQLException("Failed to insert row into " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        switch (sURIMatcher.match(uri)) {
            case CUSTOMER_POINTS_PHONE:
                selection = COLUMN_PHONE + " = ?";
                selectionArgs = new String[]{uri.getLastPathSegment()};
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        int rowsUpdated = database.update(TABLE_CUSTOMER_POINTS, values, selection, selectionArgs);
        if (rowsUpdated > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }


    @Override
    public String getType(Uri uri) {
        switch (sURIMatcher.match(uri)) {
            case CUSTOMER_POINTS:
                return "vnd.android.cursor.dir/" + AUTHORITY + "." + PATH_CUSTOMER_POINTS;
            case CUSTOMER_POINTS_PHONE:
                return "vnd.android.cursor.item/" + AUTHORITY + "." + PATH_CUSTOMER_POINTS;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch (sURIMatcher.match(uri)) {
            case CUSTOMER_POINTS_PHONE:
                selection = COLUMN_PHONE + " = ?";
                selectionArgs = new String[]{uri.getLastPathSegment()};
                break;
            // Handle other cases if necessary
        }

        int rowsDeleted = database.delete(TABLE_CUSTOMER_POINTS, selection, selectionArgs);
        if (rowsDeleted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }


}

