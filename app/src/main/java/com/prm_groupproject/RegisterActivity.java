package com.prm_groupproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashSet;
import java.util.Set;

public class RegisterActivity extends AppCompatActivity {

    EditText edtUsername, edtPassword;
    Button btnRegister, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnBack = findViewById(R.id.btnBack);

        btnRegister.setOnClickListener(v -> {
            String username = edtUsername.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences pref = getSharedPreferences("user_prefs", MODE_PRIVATE);

            // Kiểm tra username đã tồn tại chưa
            if (pref.contains(username)) {
                Toast.makeText(this, "Tài khoản đã tồn tại!", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences.Editor editor = pref.edit();
            editor.putString(username, password);        // Lưu mật khẩu theo key username
            editor.putInt("points_" + username, 100);    // Lưu điểm khởi tạo cho user mới
            editor.putString("current_user", username);  // Lưu user hiện tại (đăng nhập tự động)
            editor.apply();

            Set<String> userSet = pref.getStringSet("all_users", new HashSet<>());
            userSet = new HashSet<>(userSet);
            userSet.add(username);
            pref.edit().putStringSet("all_users", userSet).apply();

            Toast.makeText(this, "Đăng ký thành công! Bạn có 100 điểm.", Toast.LENGTH_SHORT).show();

            // Chuyển đến MainActivity
            Intent intent = new Intent(this, MenuActivity.class);
            startActivity(intent);
            finish();
        });

        btnBack.setOnClickListener(v -> finish());
    }
}
