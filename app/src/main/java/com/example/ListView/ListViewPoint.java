package com.example.ListView;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.Database.DBHelper;
import com.example.Login.LoginActivity;
import com.example.Model.User;
import com.example.apptichdiem.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class ListViewPoint extends AppCompatActivity {

    private DBHelper db;
    private ListView listViewUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.menu_add) {
                    // Xử lý khi chọn "Thêm"
                    Toast.makeText(ListViewPoint.this, "Thêm được chọn", Toast.LENGTH_SHORT).show();
                    return true;  // Trả về true để thông báo rằng sự kiện đã được xử lý
                } else if (itemId == R.id.menu_delete) {
                    // Xử lý khi chọn "Xóa"
                    Toast.makeText(ListViewPoint.this, "Xóa được chọn", Toast.LENGTH_SHORT).show();
                    return true;  // Trả về true
                } else if (itemId == R.id.menu_logout) {
                    // Tạo một AlertDialog để xác nhận hành động đăng xuất
                    new AlertDialog.Builder(ListViewPoint.this)
                            .setTitle("Xác nhận đăng xuất")
                            .setMessage("Bạn có chắc chắn muốn đăng xuất không?")
                            .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Nếu người dùng đồng ý đăng xuất
                                    SharedPreferences sharedPref = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPref.edit();
                                    editor.clear(); // Xóa tất cả dữ liệu lưu trong SharedPreferences
                                    editor.apply();

                                    // Chuyển hướng về màn hình đăng nhập
                                    Intent intent = new Intent(ListViewPoint.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                    Toast.makeText(ListViewPoint.this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Nếu người dùng chọn hủy, đóng hộp thoại
                                    dialog.dismiss();
                                }
                            })
                            .show();

                    return true; // Trả về true để thông báo rằng sự kiện đã được xử lý
                }
                else if (itemId == R.id.menu_profile) {
                    // Xử lý khi chọn "Hồ sơ"
                    Intent intent = new Intent(ListViewPoint.this, ProfileActivity.class);
                    startActivity(intent);
                    return true;  // Trả về true
                }

                return false;
            }});
        listViewUsers = findViewById(R.id.listViewUsers);
        db = new DBHelper(this);

        // Lấy danh sách người dùng từ database
        List<User> userList = db.getAllUsers();

        // Sử dụng ArrayAdapter để hiển thị danh sách người dùng trong ListView
        CustomAdapter adapter = new CustomAdapter(this, userList);
        listViewUsers.setAdapter(adapter);
    }
}

