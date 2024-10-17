package com.example.apptichdiem;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.Database.DBHelper;
import com.example.ListView.CustomAdapter;
import com.example.Model.User;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DBHelper db;
    private ListView listViewUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
}
