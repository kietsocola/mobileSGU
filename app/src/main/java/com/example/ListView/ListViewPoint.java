package com.example.ListView;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.Database.DBHelper;
import com.example.Model.User;
import com.example.apptichdiem.R;

import java.util.List;

public class ListViewPoint extends AppCompatActivity {

    private DBHelper db;
    private ListView listViewUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listViewUsers = findViewById(R.id.listViewUsers);
        db = new DBHelper(this);

        // Lấy danh sách người dùng từ database
        List<User> userList = db.getAllUsers();

        // Sử dụng ArrayAdapter để hiển thị danh sách người dùng trong ListView
        CustomAdapter adapter = new CustomAdapter(this, userList);
        listViewUsers.setAdapter(adapter);
    }
}

