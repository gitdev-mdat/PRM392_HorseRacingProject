package com.prm_groupproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    
    private UserManager userManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        userManager = new UserManager(this);
        
        // Delay 1 giây rồi điều hướng
        new Handler().postDelayed(() -> {
            Intent intent;
            
            if (!userManager.hasAccount()) {
                // Chưa có tài khoản -> đến Register
                intent = new Intent(this, RegisterActivity.class);
            } else if (userManager.isLoggedIn()) {
                // Đã đăng nhập -> đến Menu
                intent = new Intent(this, MenuActivity.class);
            } else {
                // Có tài khoản nhưng chưa đăng nhập -> đến Login
                intent = new Intent(this, LoginActivity.class);
            }
            
            startActivity(intent);
            finish();
        }, 1000);
    }
}
