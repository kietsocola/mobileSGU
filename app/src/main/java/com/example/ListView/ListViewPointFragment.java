package com.example.ListView;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.Database.DBHelper;
import com.example.Model.User;
import com.example.XML_Action.ExportXML;
import com.example.XML_Action.ImportXML;
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
        Button btnExport = view.findViewById(R.id.btnExportXML);
        btnExport.setOnClickListener(v -> {
            ExportXML e = new ExportXML(getContext());
            e.exportCustomersToXML(userList);
        });

        Button btnImport = view.findViewById(R.id.btnImportXML);
        btnImport.setOnClickListener(v -> {
//            ImportXML e= new ImportXML(getContext());
//            List<User> u = e.importCustomersFromXML();
//            for (User user : u) {
//                Log.d("UserInfo", "Phone Number: " + user.getPhoneNumber() +
//                        ", Point: " + user.getPoint() +
//                        ", Date Create: " + user.getDateCreate() +
//                        ", Date Update: " + user.getDateUpdate());
//            }
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/xml"); // Chỉ chọn file XML
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(intent, "Choose XML File"), 1);
        });
        return view;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                ImportXML e= new ImportXML(getContext());
                List<User> u = e.importCustomersFromXML(uri);
                for (User user : u) {
                    Log.d("UserInfo", "Phone Number: " + user.getPhoneNumber() +
                            ", Point: " + user.getPoint() +
                            ", Date Create: " + user.getDateCreate() +
                            ", Date Update: " + user.getDateUpdate());
                    db = new DBHelper(getContext());
                    boolean rs = db.importUser(user.getPhoneNumber(), user.getPoint());
                    if(rs) Toast.makeText(getContext().getApplicationContext(), "Import user "+user.getPhoneNumber()+"thành công", Toast.LENGTH_LONG).show();
                    else Toast.makeText(getContext().getApplicationContext(), "Import user "+user.getPhoneNumber()+"lỗi vì trùng sđt hoặc sai định dạng", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
