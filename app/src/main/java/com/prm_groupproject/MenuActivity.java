package com.prm_groupproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {
    
    private TextView tvWelcome, tvTotalPoints, tvGamesPlayed, tvGamesWon, tvWinRate;
    private Button btnPlayGame, btnLogout;
    private UserManager userManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        
        userManager = new UserManager(this);
        
        // Kiểm tra đã đăng nhập chưa
        if (!userManager.isLoggedIn()) {
            goToLogin();
            return;
        }
        
        // Khởi tạo views
        tvWelcome = findViewById(R.id.tvWelcome);
        tvTotalPoints = findViewById(R.id.tvTotalPoints);
        tvGamesPlayed = findViewById(R.id.tvGamesPlayed);
        tvGamesWon = findViewById(R.id.tvGamesWon);
        tvWinRate = findViewById(R.id.tvWinRate);
        btnPlayGame = findViewById(R.id.btnPlayGame);
        btnLogout = findViewById(R.id.btnLogout);
        
        // Set listeners
        btnPlayGame.setOnClickListener(v -> goToGame());
        btnLogout.setOnClickListener(v -> showLogoutDialog());
        
        // Load user data
        loadUserData();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data khi quay lại từ game
        if (userManager.isLoggedIn()) {
            loadUserData();
        }
    }
    
    private void loadUserData() {
        String username = userManager.getUsername();
        int totalPoints = userManager.getTotalPoints();
        int gamesPlayed = userManager.getGamesPlayed();
        int gamesWon = userManager.getGamesWon();
        
        // Tính win rate
        double winRate = gamesPlayed > 0 ? (double) gamesWon / gamesPlayed * 100 : 0;
        
        // Update UI
        tvWelcome.setText("Chào mừng, " + username + "!");
        tvTotalPoints.setText("Tổng điểm: " + totalPoints);
        tvGamesPlayed.setText("Số ván đã chơi: " + gamesPlayed);
        tvGamesWon.setText("Số ván thắng: " + gamesWon);
        tvWinRate.setText(String.format("Tỷ lệ thắng: %.1f%%", winRate));
    }
    
    private void goToGame() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    
    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Đăng xuất")
            .setMessage("Bạn có chắc muốn đăng xuất?")
            .setPositiveButton("Có", (dialog, which) -> {
                userManager.logoutUser();
                Toast.makeText(this, "Đã đăng xuất!", Toast.LENGTH_SHORT).show();
                goToLogin();
            })
            .setNegativeButton("Không", null)
            .show();
    }
    
    private void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
