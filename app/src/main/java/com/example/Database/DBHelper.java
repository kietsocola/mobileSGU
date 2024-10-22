package com.example.Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.example.Login.LoginActivity;
import com.example.Model.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "point.db";
    public static final int DATABASE_VERSION = 3; // Tăng version của database

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
        // Xóa bảng cũ nếu database có thay đổi
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_DETAILS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }

    public void insertSampleData() {
        SQLiteDatabase db = this.getWritableDatabase();

        // Kiểm tra xem bảng users có dữ liệu chưa
        String countQuery = "SELECT COUNT(*) FROM " + TABLE_USER;
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
//        cursor.close();

        String countQuery2 = "SELECT COUNT(*) FROM " + TABLE_USER_DETAILS;
        Cursor cursor2 = db.rawQuery(countQuery2, null);
        cursor2.moveToFirst();
        int count2 = cursor2.getInt(0);

//        cursor2.close();
        if (count == 0) { // Chỉ thêm dữ liệu khi bảng rỗng
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


        }
        if(count2 == 0){
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
        }

        // Đóng database sau khi thêm dữ liệu
        // db.close();
//        return count2;
    }


    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Truy vấn để lấy tất cả thông tin từ bảng user_details kết hợp với bảng users
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_PHONE_NUMBER + ", " +
                COLUMN_POINTS + ", " + COLUMN_CREATED_AT + ", " + COLUMN_UPDATED_AT +
                " FROM " + TABLE_USER_DETAILS , null);

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String phoneNumber = cursor.getString(cursor.getColumnIndex(COLUMN_PHONE_NUMBER));
                @SuppressLint("Range") int points = cursor.getInt(cursor.getColumnIndex(COLUMN_POINTS));
                @SuppressLint("Range") String dateCreate = cursor.getString(cursor.getColumnIndex(COLUMN_CREATED_AT));
                @SuppressLint("Range") String dateUpdate = cursor.getString(cursor.getColumnIndex(COLUMN_UPDATED_AT));

                // Tạo đối tượng User và thêm vào danh sách
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
    public User getUserBySDT(String sdt){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_USER_DETAILS,
                new String[]{COLUMN_USERNAME, COLUMN_UPDATED_AT, COLUMN_CREATED_AT, COLUMN_POINTS, COLUMN_PHONE_NUMBER},  // Chỉ cần trả về cột username
                COLUMN_PHONE_NUMBER + "=?",  // Điều kiện WHERE
                new String[]{sdt},  // Giá trị cho điều kiện WHERE
                null, null, null);
        if (cursor.moveToFirst()) {
            @SuppressLint("Range") String phoneNumber = cursor.getString(cursor.getColumnIndex(COLUMN_PHONE_NUMBER));
            @SuppressLint("Range") int points = cursor.getInt(cursor.getColumnIndex(COLUMN_POINTS));
            @SuppressLint("Range") String dateCreate = cursor.getString(cursor.getColumnIndex(COLUMN_CREATED_AT));
            @SuppressLint("Range") String dateUpdate = cursor.getString(cursor.getColumnIndex(COLUMN_UPDATED_AT));
            User u = new User(phoneNumber, points, dateCreate, dateUpdate);
            return u;
        }
        return null;
    }
    @SuppressLint("Range")
    public int updatePoint(int num, String sdt, boolean isUse) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Truy vấn để lấy thông tin hiện tại của người dùng
        Cursor cursor = db.query(TABLE_USER_DETAILS,
                new String[]{COLUMN_USERNAME, COLUMN_UPDATED_AT, COLUMN_CREATED_AT, COLUMN_POINTS, COLUMN_PHONE_NUMBER},  // Các cột cần lấy
                COLUMN_PHONE_NUMBER + "=?",  // Điều kiện WHERE
                new String[]{sdt},  // Giá trị cho điều kiện WHERE
                null, null, null);

        if (cursor.moveToFirst()) {
            // Lấy giá trị điểm hiện tại
            int points = cursor.getInt(cursor.getColumnIndex(COLUMN_POINTS));

            // Kiểm tra xem nếu là isUse (sử dụng điểm) và điểm không đủ thì trả về 0
            if (num > points && isUse) return 0;

            // Tạo đối tượng ContentValues để cập nhật giá trị
            ContentValues values = new ContentValues();

            // Tính toán điểm mới sau khi cộng/trừ điểm
            int newPoints;
            if (isUse) {
                newPoints = points - num;
            } else {
                newPoints = points + num;
            }
            values.put(COLUMN_POINTS, newPoints); // Cập nhật điểm

            // Lấy thời gian hiện tại và cập nhật cột dateUpdate
            values.put(COLUMN_UPDATED_AT, getCurrentDateTime()); // Cập nhật thời gian hiện tại

            // Thực hiện cập nhật bản ghi
            int result = db.update(TABLE_USER_DETAILS, values, COLUMN_PHONE_NUMBER + "=?", new String[]{sdt});

            // Nếu cập nhật thành công trả về 1, nếu không trả về -1
            if (result > 0) return 1;
        }

        // Trả về -1 nếu không tìm thấy bản ghi
        return -1;
    }
    public boolean importUser(String phoneNumber, int points) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (!isValidPhoneNumber(phoneNumber)) {
            return false; // Trả về false nếu định dạng không hợp lệ
        }

        // Kiểm tra định dạng điểm
        if (!isValidPoints(points)) {
            return false; // Trả về false nếu điểm không hợp lệ
        }
        // Kiểm tra xem số điện thoại đã tồn tại chưa
        Cursor cursor = db.query(TABLE_USER_DETAILS,
                new String[]{COLUMN_PHONE_NUMBER},
                COLUMN_PHONE_NUMBER + "=?",
                new String[]{phoneNumber},
                null, null, null);

        if (cursor.getCount() > 0) {
            cursor.close(); // Đóng con trỏ để tránh rò rỉ tài nguyên
            return false; // Số điện thoại đã tồn tại
        }
        cursor.close(); // Đóng con trỏ sau khi kiểm tra

        // Tạo đối tượng ContentValues cho bảng user_details
        ContentValues userDetailsValues = new ContentValues();
//        userDetailsValues.put(COLUMN_USERNAME, phoneNumber);
//        userDetailsValues.put(COLUMN_PASSWORD, phoneNumber);
        userDetailsValues.put(COLUMN_PHONE_NUMBER, phoneNumber);
        userDetailsValues.put(COLUMN_POINTS, points);
        userDetailsValues.put(COLUMN_CREATED_AT, getCurrentDateTime()); // Thêm thời gian hiện tại cho ngày tạo
        userDetailsValues.put(COLUMN_UPDATED_AT, getCurrentDateTime()); // Thêm thời gian hiện tại cho ngày cập nhật

        // Thêm vào bảng user_details
        long result = db.insert(TABLE_USER_DETAILS, null, userDetailsValues);

        return result > 0; // Trả về true nếu thêm thành công
    }
    public String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date(System.currentTimeMillis());
        return sdf.format(date);
    }
    private boolean isValidPoints(int points) {
        // Kiểm tra xem điểm có phải là số nguyên không âm
        return points >= 0; // Hoặc points > 0 nếu bạn không cho phép điểm bằng 0
    }
    private boolean isValidPhoneNumber(String phoneNumber) {
        // Biểu thức chính quy kiểm tra định dạng số điện thoại (ví dụ: 10-11 chữ số)
        String regex = "^(\\d{10}|\\d{11})$"; // Thay đổi theo yêu cầu cụ thể
        return phoneNumber.matches(regex);
    }
    public boolean deleteUserByPhoneNumber(String phoneNumber) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Xóa user từ bảng user_details dựa trên số điện thoại
        int result = db.delete(TABLE_USER_DETAILS, COLUMN_PHONE_NUMBER + "=?", new String[]{phoneNumber});

        return result > 0; // Trả về true nếu xóa thành công
    }


}
