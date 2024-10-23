package com.example.ListView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.Database.DBHelper;
import com.example.Model.User;
import com.example.apptichdiem.R;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ListViewPointFragment extends Fragment {

    private DBHelper db;
    private ListView listViewUsers;
    private CustomAdapter adapter;
    private String selectedPhoneNumber;

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
        adapter = new CustomAdapter(getActivity(), userList);
        listViewUsers.setAdapter(adapter);

        // Khởi tạo nút xuất danh sách
        Button exportButton = view.findViewById(R.id.exportButton);
        exportButton.setOnClickListener(v -> {
            exportCustomersToXML(getContext(), userList);
            // Đặt tiêu đề và nội dung email
            String subject = "Danh sách khách hàng";
            String message = "Đây là danh sách khách hàng đã xuất.";
            sendEmailWithAttachment(subject, message);
        });

        // Thêm sự kiện nhấp chuột vào item trong ListView
        listViewUsers.setOnItemClickListener((parent, view1, position, id) -> {
            User selectedUser = userList.get(position); // Lấy đối tượng người dùng đã chọn
            selectedPhoneNumber = selectedUser.getPhoneNumber(); // Lưu số điện thoại
            Toast.makeText(getActivity(), "Đã chọn: " + selectedPhoneNumber, Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    public void refreshUserList() {
        List<User> userList = db.getAllUsers(); // Lấy danh sách người dùng từ database
        adapter = new CustomAdapter(getActivity(), userList);
        listViewUsers.setAdapter(adapter);
    }

    public void exportCustomersToXML(Context context, List<User> customerList) {
        File file = new File(context.getExternalFilesDir(null), "customers.xml");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            XmlSerializer serializer = Xml.newSerializer();
            serializer.setOutput(fos, "UTF-8");
            serializer.startDocument("UTF-8", true);
            serializer.startTag(null, "customers");

            for (User customer : customerList) {
                serializer.startTag(null, "customer");
                serializer.startTag(null, "phoneNumber");
                serializer.text(customer.getPhoneNumber()); // Giả định rằng User có phương thức này
                serializer.endTag(null, "phoneNumber");
                serializer.startTag(null, "points");
                serializer.text(String.valueOf(customer.getPoint())); // Giả định rằng User có phương thức này
                serializer.endTag(null, "points");
                serializer.endTag(null, "customer");
            }

            serializer.endTag(null, "customers");
            serializer.endDocument();
            Toast.makeText(context, "Xuất danh sách thành công!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Lỗi xuất danh sách!", Toast.LENGTH_SHORT).show();
        }
    }

    public void sendEmailWithAttachment(String subject, String body) {
        File file = new File(getContext().getExternalFilesDir(null), "customers.xml");
        Uri fileUri = FileProvider.getUriForFile(getContext(), getActivity().getPackageName() + ".fileprovider", file);

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/xml");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"example@example.com"}); // Thay đổi địa chỉ email theo ý muốn
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);
        emailIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // Thêm flag để cấp quyền

        startActivity(Intent.createChooser(emailIntent, "Gửi email..."));
    }

    public String getSelectedPhoneNumber() {
        return selectedPhoneNumber; // Phương thức getter để lấy số điện thoại đã chọn
    }
}








