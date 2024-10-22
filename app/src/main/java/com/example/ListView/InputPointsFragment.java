package com.example.ListView;

import android.annotation.SuppressLint;
import android.content.ContentValues; // Thêm dòng này
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.Database.DBHelper;
import com.example.apptichdiem.R;

public class InputPointsFragment extends Fragment {
    private DBHelper dbHelper;
    private EditText phoneNumberInput;
    private TextView currentPointsText;
    private EditText additionalPointsInput;
    private Button updatePointsButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_input_points, container, false);

        phoneNumberInput = view.findViewById(R.id.phone_number_input);
        currentPointsText = view.findViewById(R.id.current_points_text);
        additionalPointsInput = view.findViewById(R.id.additional_points_input);
        updatePointsButton = view.findViewById(R.id.update_points_button);

        dbHelper = new DBHelper(getContext());

        // Thêm TextWatcher để lắng nghe sự thay đổi trong số điện thoại
        phoneNumberInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String phoneNumber = s.toString().trim();
                if (!phoneNumber.isEmpty()) {
                    // Lấy điểm hiện tại từ database
                    int currentPoints = dbHelper.getPointsByPhoneNumber(phoneNumber);
                    currentPointsText.setText("Điểm hiện tại: " + currentPoints);
                } else {
                    currentPointsText.setText("Điểm hiện tại: 0");
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        // Xử lý khi nhấn vào nút cập nhật điểm
        updatePointsButton.setOnClickListener(v -> {
            String phoneNumber = phoneNumberInput.getText().toString().trim();
            String additionalPointsStr = additionalPointsInput.getText().toString().trim();

            if (phoneNumber.isEmpty() || additionalPointsStr.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            int additionalPoints;
            try {
                additionalPoints = Integer.parseInt(additionalPointsStr);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Điểm không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            // Cập nhật lại điểm
            int currentPoints = dbHelper.getPointsByPhoneNumber(phoneNumber);
            int updatedPoints = currentPoints + additionalPoints;
            dbHelper.updatePointsByPhoneNumber(phoneNumber, updatedPoints);
            String username = "user1"; // Thay đổi thành username thực tế nếu cần

            // Kiểm tra nếu số điện thoại chưa tồn tại thì thêm mới
            if (currentPoints == 0) {
                ContentValues values = new ContentValues();
                values.put(dbHelper.COLUMN_PHONE_NUMBER, phoneNumber);
                values.put(dbHelper.COLUMN_POINTS, updatedPoints);
                values.put(dbHelper.COLUMN_CREATED_AT, System.currentTimeMillis());
                values.put(dbHelper.COLUMN_UPDATED_AT, System.currentTimeMillis());
                values.put(dbHelper.COLUMN_USERNAME, username);
                dbHelper.getWritableDatabase().insert(dbHelper.TABLE_USER_DETAILS, null, values);
            }

            currentPointsText.setText("Điểm hiện tại: " + updatedPoints);
            Toast.makeText(getContext(), "Cập nhật điểm thành công", Toast.LENGTH_SHORT).show();

            // Gọi hàm để cập nhật danh sách
            Fragment fragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (fragment instanceof ListViewPointFragment) {
                ((ListViewPointFragment) fragment).refreshUserList();
            }
        });

        return view;
    }
}
