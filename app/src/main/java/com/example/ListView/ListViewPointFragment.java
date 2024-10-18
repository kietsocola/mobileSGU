package com.example.ListView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.Database.DBHelper;
import com.example.Model.User;
import com.example.apptichdiem.R;

import java.util.List;

public class ListViewPointFragment extends Fragment {

    private DBHelper db;
    private ListView listViewUsers;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listview_point, container, false);
        listViewUsers = view.findViewById(R.id.listViewUsers);
        db = new DBHelper(getContext());

        // Lấy danh sách người dùng từ database
        List<User> userList = db.getAllUsers();

        // Sử dụng CustomAdapter để hiển thị danh sách người dùng trong ListView
        CustomAdapter adapter = new CustomAdapter(getActivity(), userList);
        listViewUsers.setAdapter(adapter);

        return view;
    }
}
