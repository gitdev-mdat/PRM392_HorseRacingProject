package com.prm_groupproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    
    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private UserManager userManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        userManager = new UserManager(this);
        
        // Khởi tạo views
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        
        // Set listeners
        btnLogin.setOnClickListener(v -> loginUser());
        tvRegister.setOnClickListener(v -> goToRegister());
    }
    
    private void loginUser() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString();
        
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (userManager.loginUser(username, password)) {
            Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
            goToMenu();
        } else {
            Toast.makeText(this, "Sai username hoặc password!", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void goToRegister() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }
    
    private void goToMenu() {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
        finish();
    }
}
