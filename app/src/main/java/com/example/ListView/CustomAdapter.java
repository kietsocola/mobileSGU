package com.example.ListView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.Model.User;
import com.example.apptichdiem.R;

import java.util.List;

public class CustomAdapter extends BaseAdapter {
    private Context context;
    private List<User> userList;
    public CustomAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }
    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int position) {
        return userList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.user_list_item, parent, false);
        }
        // Lấy thông tin người dùng từ danh sách
        User user = userList.get(position);

        // Gán dữ liệu cho các TextView
        TextView tvPhoneNumber = convertView.findViewById(R.id.tvPhoneNumber);
        TextView tvPoints = convertView.findViewById(R.id.tvPoints);
        TextView tvDateCreate = convertView.findViewById(R.id.tvDateCreate);
        TextView tvDateUpdate= convertView.findViewById(R.id.tvDateUpdate);

        tvPhoneNumber.setText(user.getPhoneNumber());
        tvPoints.setText("Point: "+String.valueOf(user.getPoint()));
        tvDateCreate.setText("Date create: "+user.getDateCreate());
        tvDateUpdate.setText("Date update: "+user.getDateUpdate());

        return convertView;
    }

    // Cập nhật danh sách và thông báo cho adapter
    public void updateUserList(List<User> newUserList) {
        this.userList.clear();
        this.userList.addAll(newUserList);
        notifyDataSetChanged();
    }
}
