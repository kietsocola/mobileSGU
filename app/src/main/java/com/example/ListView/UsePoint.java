package com.example.ListView;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.Database.DBHelper;
import com.example.apptichdiem.R;

public class UsePoint extends Fragment {

    private EditText txtPhone, txtCurrent, txtUsePoint;
    private Button btnSave, btnNext;

    public UsePoint() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_use_point, container, false);

        DBHelper dbHelper = new DBHelper(getContext());

        txtPhone = view.findViewById(R.id.txtPhone);
        txtCurrent = view.findViewById(R.id.txtCurrent);
        txtUsePoint = view.findViewById(R.id.txtUsePoint);
        btnSave = view.findViewById(R.id.btnSave);

        // Lấy các tham số và điền số điện thoại
        Bundle bundle = getArguments();
        if (bundle != null) {
            String phoneNumber = bundle.getString("phoneNumber");
            txtPhone.setText(phoneNumber);
            // Lấy số điểm hiện tại cho số điện thoại và điền vào
            int points = dbHelper.getPointsByPhoneNumber(phoneNumber);
            txtCurrent.setText(String.valueOf(points));
        }




        // TextWatcher for phone number input
        txtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String phoneNumber = s.toString().trim();
                if (!phoneNumber.isEmpty()) {
                    // Fetch current points from DB
                    int currentPoints = dbHelper.getPointsByPhoneNumber(phoneNumber);
                    txtCurrent.setText(String.valueOf(currentPoints));
                } else {
                    txtCurrent.setText("0");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Save button logic
        btnSave.setOnClickListener(v -> {
            String phone = txtPhone.getText().toString().trim();
//            String note = txtNote.getText().toString().trim();
            String pointsInput = txtUsePoint.getText().toString().trim();

            // Validate input
            if (phone.isEmpty() || txtCurrent.getText().toString().trim().isEmpty() || pointsInput.isEmpty()) {
                Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra xem điểm sử dụng có phải là số không
            if (!pointsInput.matches("\\d+")) {  // Kiểm tra xem có phải là số nguyên dương
                Toast.makeText(getActivity(), "Points must be a valid number!", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int pointsToUse = Integer.parseInt(pointsInput);
                int currentPoints = Integer.parseInt(txtCurrent.getText().toString().trim());

                if (currentPoints >= pointsToUse) {
                    int updatedPoints = currentPoints - pointsToUse;
                    dbHelper.updatePointsByPhoneNumber(phone, updatedPoints);

                    // Update customer points and notes
                    updateCustomerPoints(phone, updatedPoints);
                    Toast.makeText(getActivity(), "Points used successfully! Remaining: " + updatedPoints, Toast.LENGTH_SHORT).show();

                    // Quay lại trang danh sách sau khi lưu
                    getActivity().getSupportFragmentManager().popBackStack();
                } else {
                    Toast.makeText(getActivity(), "Insufficient points!", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(getActivity(), "Invalid points format!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();  // In ra lỗi để xem chi tiết
            } catch (Exception e) {
                Toast.makeText(getActivity(), "Error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();  // In ra lỗi để xem chi tiết
            }
        });



        return view;
    }

    // Method to update customer points and notes
    private void updateCustomerPoints(String phoneNumber, int updatedPoints) {
        DBHelper dbHelper = new DBHelper(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("points", updatedPoints);


        db.update(DBHelper.TABLE_USER_DETAILS, values, "phone_number = ?", new String[]{phoneNumber});
    }
}
