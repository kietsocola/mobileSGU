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
import androidx.fragment.app.FragmentTransaction;

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

        // Thiết lập sự kiện nhấn vào từng mục trong ListView
        listViewUsers.setOnItemClickListener((parent, view1, position, id) -> {
            // Lấy thông tin người dùng tại vị trí được nhấn
            User user = userList.get(position);

            // Tạo một đối tượng Bundle để chứa dữ liệu
            Bundle bundle = new Bundle();
            bundle.putString("phoneNumber", user.getPhoneNumber());
            bundle.putInt("points", user.getPoint());

            // Tạo đối tượng UsePointFragment và gán Bundle
            UsePoint usePointFragment = new UsePoint();
            usePointFragment.setArguments(bundle);

            // Thực hiện thay thế fragment hiện tại bằng UsePointFragment
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, usePointFragment);  // ID của container chứa fragment
            transaction.addToBackStack(null);  // Thêm vào back stack để có thể quay lại
            transaction.commit();
        });

        return view;
    }
}
