package com.example.Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.Model.User;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "point.db";
    public static final int DATABASE_VERSION = 2; // Tăng version của database


    // Bảng users
    public static final String TABLE_USER = "users";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";

    // Bảng user_details
    public static final String TABLE_USER_DETAILS = "user_details";
    public static final String COLUMN_PHONE_NUMBER = "phone_number";
    public static final String COLUMN_POINTS = "points";
    public static final String COLUMN_CREATED_AT = "created_at";
    public static final String COLUMN_UPDATED_AT = "updated_at";

    // Câu SQL để tạo bảng users
    private static final String TABLE_CREATE_USER = "CREATE TABLE " + TABLE_USER + " (" +
            COLUMN_USERNAME + " TEXT PRIMARY KEY, " +
            COLUMN_PASSWORD + " TEXT NOT NULL);";

    // Câu SQL để tạo bảng user_details
    private static final String TABLE_CREATE_USER_DETAILS = "CREATE TABLE " + TABLE_USER_DETAILS + " (" +
            COLUMN_PHONE_NUMBER + " TEXT NOT NULL, " +
            COLUMN_POINTS + " INTEGER DEFAULT 0, " +
            COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
            COLUMN_UPDATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
            COLUMN_USERNAME + " TEXT, " + // Khóa ngoại liên kết đến bảng users
            "FOREIGN KEY (" + COLUMN_USERNAME + ") REFERENCES " + TABLE_USER + "(" + COLUMN_USERNAME + "));";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo bảng users và user_details khi khởi tạo database
        db.execSQL(TABLE_CREATE_USER);
        db.execSQL(TABLE_CREATE_USER_DETAILS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            // Example: adding a 'note' column to the user_details table if it doesn't exist
            db.execSQL("ALTER TABLE " + TABLE_USER_DETAILS + " ADD COLUMN note TEXT");
        }
    }

    public void insertSampleData() {
        SQLiteDatabase db = this.getWritableDatabase();

        // Dữ liệu mẫu 1
        ContentValues values1 = new ContentValues();
        values1.put(COLUMN_USERNAME, "user1");
        values1.put(COLUMN_PASSWORD, "password1");

        // Dữ liệu mẫu 2
        ContentValues values2 = new ContentValues();
        values2.put(COLUMN_USERNAME, "user2");
        values2.put(COLUMN_PASSWORD, "password2");

        // Thêm dữ liệu vào bảng users
        db.insert(TABLE_USER, null, values1);
        db.insert(TABLE_USER, null, values2);

        // Dữ liệu mẫu cho user_details
        ContentValues userDetails1 = new ContentValues();
        userDetails1.put(COLUMN_PHONE_NUMBER, "0123456789");
        userDetails1.put(COLUMN_POINTS, 100);
        userDetails1.put(COLUMN_USERNAME, "user1"); // Liên kết với bảng users

        ContentValues userDetails2 = new ContentValues();
        userDetails2.put(COLUMN_PHONE_NUMBER, "0987654321");
        userDetails2.put(COLUMN_POINTS, 200);
        userDetails2.put(COLUMN_USERNAME, "user2"); // Liên kết với bảng users

        // Thêm dữ liệu vào bảng user_details
        db.insert(TABLE_USER_DETAILS, null, userDetails1);
        db.insert(TABLE_USER_DETAILS, null, userDetails2);

        // Đóng database sau khi thêm dữ liệu
        db.close();
    }

    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + TABLE_USER_DETAILS + "." + COLUMN_PHONE_NUMBER + ", " +
                TABLE_USER_DETAILS + "." + COLUMN_POINTS + ", " +
                TABLE_USER_DETAILS + "." + COLUMN_CREATED_AT + ", " +
                TABLE_USER_DETAILS + "." + COLUMN_UPDATED_AT +
                " FROM " + TABLE_USER_DETAILS +
                " INNER JOIN " + TABLE_USER +
                " ON " + TABLE_USER_DETAILS + "." + COLUMN_USERNAME + " = " + TABLE_USER + "." + COLUMN_USERNAME;

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String phoneNumber = cursor.getString(cursor.getColumnIndex(COLUMN_PHONE_NUMBER));
                @SuppressLint("Range") int points = cursor.getInt(cursor.getColumnIndex(COLUMN_POINTS));
                @SuppressLint("Range") String dateCreate = cursor.getString(cursor.getColumnIndex(COLUMN_CREATED_AT));
                @SuppressLint("Range") String dateUpdate = cursor.getString(cursor.getColumnIndex(COLUMN_UPDATED_AT));

                userList.add(new User(phoneNumber, points, dateCreate, dateUpdate));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return userList;
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Truy vấn để kiểm tra xem người dùng có tồn tại với tên và mật khẩu cung cấp hay không
        Cursor cursor = db.query(TABLE_USER,
                new String[]{COLUMN_USERNAME},  // Chỉ cần trả về cột username
                COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?",  // Điều kiện WHERE
                new String[]{username, password},  // Giá trị cho điều kiện WHERE
                null, null, null);

        boolean exists = cursor.getCount() > 0;  // Kiểm tra xem có bản ghi nào trả về hay không
        cursor.close();  // Đóng con trỏ để tránh rò rỉ tài nguyên
        return exists;
    }
    public void updatePassword(String username, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_PASSWORD, newPassword); // Cập nhật mật khẩu mới

        // Cập nhật bản ghi trong cơ sở dữ liệu
        db.update(TABLE_USER, values, COLUMN_USERNAME + "=?", new String[]{username});
    }

    public int getPointsByPhoneNumber(String phoneNumber) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER_DETAILS,
                new String[]{COLUMN_POINTS},
                COLUMN_PHONE_NUMBER + "=?",
                new String[]{phoneNumber}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") int points = cursor.getInt(cursor.getColumnIndex(COLUMN_POINTS));
            cursor.close();
            return points;
        }

        return 0;  // Nếu không tìm thấy số điện thoại
    }

    public void updatePointsByPhoneNumber(String phoneNumber, int newPoints) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_POINTS, newPoints);
        values.put(COLUMN_UPDATED_AT, System.currentTimeMillis());  // Cập nhật thời gian

        db.update(TABLE_USER_DETAILS, values, COLUMN_PHONE_NUMBER + "=?", new String[]{phoneNumber});
    }

    public void addNewUserDetails(String phoneNumber, int points, String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PHONE_NUMBER, phoneNumber);
        values.put(COLUMN_POINTS, points);
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_CREATED_AT, System.currentTimeMillis());
        values.put(COLUMN_UPDATED_AT, System.currentTimeMillis());

        long result = db.insert(TABLE_USER_DETAILS, null, values);
        if (result == -1) {
            Log.e("DBHelper", "Thêm số điện thoại thất bại: " + phoneNumber);
        } else {
            Log.i("DBHelper", "Thêm số điện thoại thành công: " + phoneNumber);
        }
    }

    public void updateCustomerPoints(String phoneNumber, int newPoints, String note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_POINTS, newPoints);
        values.put(COLUMN_UPDATED_AT, System.currentTimeMillis());

        // Add note column if necessary
        if (!note.isEmpty()) {
            values.put("note", note);  // Replace "note" with the actual column name for the notes if you have one
        }

        db.update(TABLE_USER_DETAILS, values, COLUMN_PHONE_NUMBER + "=?", new String[]{phoneNumber});
    }


}
