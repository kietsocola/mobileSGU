package com.example.apptichdiem;
import static android.app.PendingIntent.getActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.ListView.ListViewPointFragment;
import com.example.ListView.ProfileFragment;
import com.example.ListView.UsePoint;
import com.example.Login.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        loadFragment(new ListViewPointFragment());

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                int itemId = item.getItemId();

                if (itemId == R.id.menu_add) {
                    selectedFragment = new ListViewPointFragment();
                } else if (itemId == R.id.menu_delete) {
                    Toast.makeText(MainActivity.this, "Edit được chọn", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (itemId == R.id.menu_profile) {
                    selectedFragment = new ProfileFragment();
                } else if (itemId == R.id.menu_logout) {
                    handleLogout();
                    return true;
                } else if (itemId == R.id.menu_use) {
                    // Lấy instance của ListViewPointFragment
                    ListViewPointFragment listViewPointFragment = (ListViewPointFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);

                    // Lấy số điện thoại đã chọn
                    String phoneNumber = listViewPointFragment != null ? listViewPointFragment.getSelectedPhoneNumber() : null;

                    if (phoneNumber != null) {
                        navigateToUsePointFragment(phoneNumber); // Chuyển số điện thoại đến phương thức
                    } else {
                        Toast.makeText(MainActivity.this, "Vui lòng chọn một đối tượng trước", Toast.LENGTH_SHORT).show();
                    }
                }

                if (selectedFragment != null) {
                    loadFragment(selectedFragment);
                }
                return true;
            }
        });
    }

    private void navigateToUsePointFragment(String phoneNumber) {
        UsePoint usePointFragment = new UsePoint();

        // Chuyển số điện thoại dưới dạng tham số
        Bundle args = new Bundle();
        args.putString("phoneNumber", phoneNumber);
        usePointFragment.setArguments(args);

        // Chuyển đến fragment UsePoint
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, usePointFragment)
                .addToBackStack(null)
                .commit();
    }



    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null); // Thêm fragment vào back stack
        fragmentTransaction.commit();
    }

    private void handleLogout() {
        new android.app.AlertDialog.Builder(MainActivity.this)
                .setTitle("Xác nhận đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất không?")
                .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Nếu người dùng đồng ý đăng xuất
                        SharedPreferences sharedPref = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putBoolean("isLoggedIn", false); // Đánh dấu người dùng là chưa đăng nhập
                        editor.remove("loginTime"); // Xóa thời gian đăng nhập
                        editor.remove("username"); // Xóa tên người dùng (nếu bạn đã lưu)
                        editor.apply();

                        // Chuyển hướng về màn hình đăng nhập
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                        Toast.makeText(MainActivity.this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Nếu người dùng chọn hủy, đóng hộp thoại
                        dialog.dismiss();
                    }
                })
                .show();
    }

}