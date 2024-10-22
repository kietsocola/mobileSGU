package com.example.ListView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Database.DBHelper;
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
        ImageView ivDelete = convertView.findViewById(R.id.ivDelete);


        tvPhoneNumber.setText(user.getPhoneNumber());
        tvPoints.setText("Point: "+String.valueOf(user.getPoint()));
        tvDateCreate.setText("Date create: "+user.getDateCreate());
        tvDateUpdate.setText("Date update: "+user.getDateUpdate());
        User currentUser = userList.get(position);
        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo AlertDialog để hỏi xác nhận
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Xác nhận xóa");
                builder.setMessage("Bạn có chắc chắn muốn xóa người dùng này không?");

                // Nút "Xóa"
                builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Gọi hàm xóa user trong database
                        DBHelper dbHelper = new DBHelper(context);
                        boolean rs = dbHelper.deleteUserByPhoneNumber(currentUser.getPhoneNumber());
                        if (rs) {
                            // Xóa khỏi danh sách và thông báo cho adapter
                            userList.remove(position);
                            notifyDataSetChanged();
                            Toast.makeText(context.getApplicationContext(), "Xóa thành công", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context.getApplicationContext(), "Xóa thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                // Nút "Hủy"
                builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Đóng dialog và không làm gì
                        dialog.dismiss();
                    }
                });

                // Hiển thị AlertDialog
                builder.show();
            }
        });

        return convertView;
    }
}
