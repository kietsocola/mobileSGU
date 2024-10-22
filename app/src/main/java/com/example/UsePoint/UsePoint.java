package com.example.UsePoint;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.Database.CustomerPointsProvider;
import com.example.Database.Helper;
import com.example.Database.UserProvider;
import com.example.apptichdiem.R;

public class UsePoint extends Fragment {

    private EditText txtPhone, txtCurrent, txtUsePoint,txtNote;
    private Button btnSave, btnNext;

    public UsePoint() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_use_point, container, false);

        txtPhone = view.findViewById(R.id.txtPhone);
        txtCurrent = view.findViewById(R.id.txtCurrent);
        txtUsePoint = view.findViewById(R.id.txtUsePoint);
        txtNote = view.findViewById(R.id.txtNote);
        btnSave = view.findViewById(R.id.btnSave);
        btnNext = view.findViewById(R.id.btnNext);


            btnSave.setOnClickListener(v -> {
                String phone = txtPhone.getText().toString().trim();
                String note = txtNote.getText().toString().trim();  // Lấy ghi chú

                // Kiểm tra trường trống như trước
                if (phone.isEmpty() || txtCurrent.getText().toString().trim().isEmpty() ||
                        txtUsePoint.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getActivity(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    int pointsToUse = Integer.parseInt(txtUsePoint.getText().toString().trim());
                    int current = Integer.parseInt(txtCurrent.getText().toString().trim());

                    if (current >= pointsToUse) {
                        int updatedPoints = current - pointsToUse;
                        updateCustomerPoints(phone, updatedPoints, note);  // Cập nhật điểm và ghi chú
                        Toast.makeText(getActivity(), "Sử dụng điểm thành công! Số điểm còn lại: " + updatedPoints, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Không đủ điểm!", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(getActivity(), "Vui lòng nhập số hợp lệ", Toast.LENGTH_SHORT).show();
                }
            });

            return view;
        }

    private void updateCustomerPoints(String phone, int updatedPoints, String note) {
        ContentValues values = new ContentValues();
        values.put(Helper.COLUMN_POINTS, updatedPoints);
        values.put(Helper.COLUMN_NOTE, note);

        Uri customerUri = Uri.withAppendedPath(CustomerPointsProvider.CONTENT_URI, phone);
        int rowsUpdated = getContext().getContentResolver().update(customerUri, values, null, null);

        if (rowsUpdated > 0) {
            Toast.makeText(getContext(), "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Không tìm thấy người dùng!", Toast.LENGTH_SHORT).show();
        }
    }



}
