package com.example.ListView;

import static android.content.Context.MODE_PRIVATE;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.Database.DBHelper;
import com.example.apptichdiem.R;

public class ProfileFragment extends Fragment {

    private DBHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        TextView userNameTextView = view.findViewById(R.id.user_name);

        // Lấy SharedPreferences từ Activity
        SharedPreferences sharedPref1 = getActivity().getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        String username1 = sharedPref1.getString("username", null); // Lấy tên người dùng từ SharedPreferences
        userNameTextView.setText(username1);

        Button changePasswordButton = view.findViewById(R.id.change_password_button);
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangePasswordDialog();
            }
        });

        return view;
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Đổi mật khẩu");

        View view = getLayoutInflater().inflate(R.layout.dialog_change_password, null);
        builder.setView(view);

        // Lấy các trường nhập liệu từ layout
        EditText oldPasswordInput = view.findViewById(R.id.old_password);
        EditText newPasswordInput1 = view.findViewById(R.id.new_password);
        EditText newPasswordInput2 = view.findViewById(R.id.new_password_confirm);

        // Lấy các TextView để hiển thị lỗi
        TextView oldPasswordError = view.findViewById(R.id.old_password_error);
        TextView newPasswordError = view.findViewById(R.id.new_password_error);
        TextView newPasswordConfirmError = view.findViewById(R.id.new_password_confirm_error);

        builder.setPositiveButton("Xác nhận", null); // Don't set the listener yet

        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(dialogInterface -> {
            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(v -> {
                // Reset all error messages
                oldPasswordError.setVisibility(View.GONE);
                newPasswordError.setVisibility(View.GONE);
                newPasswordConfirmError.setVisibility(View.GONE);

                // Lấy giá trị từ các trường nhập liệu
                String oldPassword = oldPasswordInput.getText().toString();
                String newPassword1 = newPasswordInput1.getText().toString();
                String newPassword2 = newPasswordInput2.getText().toString();

                // Lấy tên người dùng đã lưu
                SharedPreferences sharedPref = getActivity().getSharedPreferences("LoginPrefs", MODE_PRIVATE);
                String username = sharedPref.getString("username", null);

                if (username != null) {
                    DBHelper dbHelper = new DBHelper(getActivity());
                    boolean isValidUser = dbHelper.checkUser(username, oldPassword);

                    boolean hasError = false; // Flag to track if there are any errors

                    if (!isValidUser) {
                        oldPasswordError.setText("Mật khẩu cũ không đúng");
                        oldPasswordError.setVisibility(View.VISIBLE);
                        hasError = true;
                    }

                    if (newPassword1.length() < 6) {
                        newPasswordError.setText("Mật khẩu mới phải có ít nhất 6 ký tự");
                        newPasswordError.setVisibility(View.VISIBLE);
                        hasError = true;
                    } else if (!newPassword1.equals(newPassword2)) {
                        newPasswordConfirmError.setText("Mật khẩu mới không khớp");
                        newPasswordConfirmError.setVisibility(View.VISIBLE);
                        hasError = true;
                    }

                    if (!hasError) {
                        dbHelper.updatePassword(username, newPassword1);
                        Toast.makeText(getActivity(), "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                        dialog.dismiss(); // Close the dialog only on success
                    }
                } else {
                    oldPasswordError.setText("Không tìm thấy tên người dùng");
                    oldPasswordError.setVisibility(View.VISIBLE);
                }
            });
        });

        dialog.show();
    }




}
