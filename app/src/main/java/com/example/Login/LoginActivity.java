package com.example.Login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.Database.DBHelper;
import com.example.ListView.ListViewPoint;
import com.example.apptichdiem.MainActivity;
import com.example.apptichdiem.R;

public class LoginActivity extends AppCompatActivity {
    private EditText txtUsername;
    private EditText txtPwd;
    private Button loginBtn;
    private DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Kiểm tra xem người dùng đã đăng nhập hay chưa
        SharedPreferences sharedPref = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPref.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            // Người dùng đã đăng nhập, chuyển hướng đến màn hình ListViewPoint
            Intent intent = new Intent(LoginActivity.this, ListViewPoint.class);
            startActivity(intent);
            finish(); // Kết thúc LoginActivity để tránh quay lại màn hình đăng nhập
            return; // Không thực hiện phần còn lại của onCreate
        }
        setContentView(R.layout.login);

        txtUsername = findViewById(R.id.editTextText2);
        txtPwd = findViewById(R.id.editTextTextPassword2);
        loginBtn = findViewById(R.id.buttonLogin);
        db = new DBHelper(this);
        db.insertSampleData();
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = txtUsername.getText().toString();
                String pwd = txtPwd.getText().toString();

                if (db.checkUser(username, pwd)) {
//                    Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                    SharedPreferences sharedPref = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean("isLoggedIn", true); // Đánh dấu người dùng đã đăng nhập
                    editor.putString("username", username); // Lưu thêm tên người dùng nếu cần
                    editor.apply();
                    Intent intent = new Intent(LoginActivity.this, ListViewPoint.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Sai tên đăng nhập hoặc mật khẩu", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
