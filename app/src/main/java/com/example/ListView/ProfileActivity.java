package com.example.ListView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.Database.DBHelper;
import com.example.Login.LoginActivity;
import com.example.apptichdiem.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        TextView userNameTextView = findViewById(R.id.user_name);
        SharedPreferences sharedPref1 = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        String username1 = sharedPref1.getString("username", null); // Lấy tên người dùng từ SharedPreferences
        userNameTextView.setText(username1);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.menu_profile);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.menu_add) {
                    // Xử lý khi chọn "Thêm"
                    Intent intent = new Intent(ProfileActivity.this, ListViewPoint.class);
                    startActivity(intent);
                    return true;  // Trả về true để thông báo rằng sự kiện đã được xử lý
                } else if (itemId == R.id.menu_delete) {
                    // Xử lý khi chọn "Xóa"
                    Toast.makeText(ProfileActivity.this, "Xóa được chọn", Toast.LENGTH_SHORT).show();
                    return true;  // Trả về true
                } else if (itemId == R.id.menu_logout) {
                    // Tạo một AlertDialog để xác nhận hành động đăng xuất
                    new android.app.AlertDialog.Builder(ProfileActivity.this)
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
                                    Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                    Toast.makeText(ProfileActivity.this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(ProfileActivity.this, "Đang ở trang hồ sơ", Toast.LENGTH_SHORT).show();
                    return true;  // Trả về true
                }

                return false;
            }});
        // Thiết lập sự kiện cho nút đổi mật khẩu
        Button changePasswordButton = findViewById(R.id.change_password_button);
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangePasswordDialog();
            }
        });
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Đổi mật khẩu");

        // Thiết lập layout cho dialog
        View view = getLayoutInflater().inflate(R.layout.dialog_change_password, null);
        builder.setView(view);

        // Lấy các trường nhập liệu từ layout
        EditText oldPasswordInput = view.findViewById(R.id.old_password);
        EditText newPasswordInput1 = view.findViewById(R.id.new_password);
        EditText newPasswordInput2 = view.findViewById(R.id.new_password_confirm);

        builder.setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Lấy giá trị từ các trường nhập liệu
                String oldPassword = oldPasswordInput.getText().toString();
                String newPassword1 = newPasswordInput1.getText().toString();
                String newPassword2 = newPasswordInput2.getText().toString();

                // Lấy tên người dùng đã lưu
                SharedPreferences sharedPref = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
                String username = sharedPref.getString("username", null); // Lấy tên người dùng từ SharedPreferences

                if (username != null) {
                    // Kiểm tra mật khẩu cũ bằng cách gọi hàm checkUser
                    DBHelper dbHelper = new DBHelper(ProfileActivity.this);
                    boolean isValidUser = dbHelper.checkUser(username, oldPassword); // Kiểm tra tên và mật khẩu

                    if (isValidUser) {
                        // Kiểm tra mật khẩu mới có khớp không
                        if (newPassword1.equals(newPassword2)) {
                            // Cập nhật mật khẩu mới vào cơ sở dữ liệu
                            dbHelper.updatePassword(username, newPassword1); // Giả sử bạn có phương thức này

                            // Xử lý đổi mật khẩu thành công
                            Toast.makeText(ProfileActivity.this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                        } else {
                            // Thông báo lỗi nếu mật khẩu mới không khớp
                            Toast.makeText(ProfileActivity.this, "Mật khẩu mới không khớp", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Thông báo lỗi nếu mật khẩu cũ không đúng
                        Toast.makeText(ProfileActivity.this, "Mật khẩu cũ không đúng", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "Không tìm thấy tên người dùng", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

}

