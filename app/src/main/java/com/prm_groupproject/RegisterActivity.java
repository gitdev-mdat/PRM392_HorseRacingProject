package com.prm_groupproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    
    private EditText etUsername, etPassword, etConfirmPassword;
    private Button btnRegister;
    private TextView tvLogin;
    private UserManager userManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        userManager = new UserManager(this);
        
        // Khởi tạo views
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);
        
        // Set listeners
        btnRegister.setOnClickListener(v -> registerUser());
        tvLogin.setOnClickListener(v -> goToLogin());
    }
    
    private void registerUser() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();
        
        // Validate input
        if (!UserManager.isValidUsername(username)) {
            Toast.makeText(this, "Username phải có 3-20 ký tự, chỉ chứa chữ cái, số và _", Toast.LENGTH_LONG).show();
            return;
        }
        
        if (!UserManager.isValidPassword(password)) {
            Toast.makeText(this, "Password phải có ít nhất 4 ký tự", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Password không khớp!", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Thử đăng ký
        if (userManager.registerUser(username, password)) {
            Toast.makeText(this, "Đăng ký thành công! Bạn nhận được 1000 điểm khởi đầu", Toast.LENGTH_LONG).show();
            goToMenu();
        } else {
            Toast.makeText(this, "Đăng ký thất bại! Tài khoản đã tồn tại", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    
    private void goToMenu() {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
        finish();
    }
}
