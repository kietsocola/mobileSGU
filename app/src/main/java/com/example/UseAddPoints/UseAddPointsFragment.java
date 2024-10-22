package com.example.UseAddPoints;

import static android.content.Context.MODE_PRIVATE;

import static androidx.core.widget.TextViewKt.addTextChangedListener;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.Database.DBHelper;
import com.example.Model.User;
import com.example.apptichdiem.R;

public class UseAddPointsFragment extends Fragment {
    private DBHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_use_add_point, container, false);
        dbHelper = new DBHelper(getContext());
        EditText stdText = view.findViewById(R.id.etCustomerPhone);
        EditText curPointText = view.findViewById(R.id.etCurrentPoint);
        curPointText.setEnabled(false);
        EditText pointText = view.findViewById(R.id.etPoint);

        Button btnAdd = view.findViewById(R.id.btnAdd);
        Button btnUse = view.findViewById(R.id.btnUse);
        btnAdd.setEnabled(false);
        btnUse.setEnabled(false);

        ImageView ivClear = view.findViewById(R.id.ivClear);
        ivClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stdText.setText("");
                curPointText.setText("");
                pointText.setText("");
                btnAdd.setEnabled(false);
                btnUse.setEnabled(false);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Thông báo");

        // Tự động điền current point sau khi nhập số điện thoại
        stdText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {  // Khi người dùng dừng nhập (mất focus)
                    String sdt = stdText.getText().toString().trim();
                    if (!sdt.isEmpty()) {
                        User user = dbHelper.getUserBySDT(sdt);
                        if (user != null) {
                            btnAdd.setEnabled(true);
                            btnUse.setEnabled(true);
                            curPointText.setText(String.valueOf(user.getPoint()));  // Hiển thị current point
                        } else {
                            btnAdd.setEnabled(false);
                            btnUse.setEnabled(false);
                            curPointText.setText("");
                            builder.setMessage("Số điện thoại không tồn tại").setPositiveButton("OK", null).show();
                        }
                    } else {
                        btnAdd.setEnabled(false);
                        btnUse.setEnabled(false);
                        curPointText.setText("");
                        builder.setMessage("Vui lòng nhập số điện thoại").setPositiveButton("OK", null).show();
                    }
                }
            }
        });

        // Xử lý nút "Use"
        btnUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sdt = stdText.getText().toString().trim();
                String pointStr = pointText.getText().toString().trim();

                if (validateInput(sdt, pointStr)) {
                    int pointsToUse = Integer.parseInt(pointStr);
                    int result = dbHelper.updatePoint(pointsToUse, sdt, true);

                    if (result == 1) {
                        builder.setMessage("Dùng điểm thành công").setPositiveButton("OK", null).show();
                        updateCurrentPoints(sdt, curPointText);
                        pointText.setText("");
                    } else if (result == 0) {
                        builder.setMessage("Số điểm không đủ để dùng").setPositiveButton("OK", null).show();
                    } else {
                        builder.setMessage("Lỗi khi dùng điểm").setPositiveButton("OK", null).show();
                    }
                }
            }
        });

        // Xử lý nút "Add"
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sdt = stdText.getText().toString().trim();
                String pointStr = pointText.getText().toString().trim();

                if (validateInput(sdt, pointStr)) {
                    int pointsToAdd = Integer.parseInt(pointStr);

                    int result = dbHelper.updatePoint(pointsToAdd, sdt, false);

                    if (result == 1) {
                        builder.setMessage("Thêm điểm thành công").setPositiveButton("OK", null).show();
                        updateCurrentPoints(sdt, curPointText);
                        pointText.setText("");
                    } else if (result == 0) {
                        builder.setMessage("Số điểm không đủ để dùng").setPositiveButton("OK", null).show();
                    } else {
                        builder.setMessage("Lỗi khi dùng điểm").setPositiveButton("OK", null).show();
                    }
                }
            }
        });

        return view;
    }

    // Hàm validate dữ liệu
    private boolean validateInput(String sdt, String pointStr) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        if (!sdt.matches("^0\\d{9}$")) {
            builder.setMessage("Số điện thoại phải bắt đầu bằng số 0 và có từ 10 chữ số").setPositiveButton("OK", null).show();
            return false;
        }

        if (pointStr.isEmpty()) {
            builder.setMessage("Vui lòng nhập điểm").setPositiveButton("OK", null).show();
            return false;
        }

        try {
            int points = Integer.parseInt(pointStr);
            if (points <= 0) {
                builder.setMessage("Điểm phải lớn hơn 0").setPositiveButton("OK", null).show();
                return false;
            }
        } catch (NumberFormatException e) {
            builder.setMessage("Vui lòng nhập số hợp lệ").setPositiveButton("OK", null).show();
            return false;
        }

        return true;
    }

    // Hàm cập nhật lại current points
    private void updateCurrentPoints(String sdt, EditText curPointText) {
        dbHelper = new DBHelper(getContext());
        User user = dbHelper.getUserBySDT(sdt);
        if (user != null) {
            curPointText.setText(String.valueOf(user.getPoint()));
        }
    }

}

