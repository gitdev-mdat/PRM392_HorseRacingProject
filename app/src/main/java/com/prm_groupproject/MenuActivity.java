package com.prm_groupproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MenuActivity extends AppCompatActivity {
    
    private TextView tvWelcome, tvTotalPoints, tvGamesPlayed, tvGamesWon, tvWinRate;
    private Button btnPlayGame, btnLogout;
    private UserManager userManager;
    ListView listView;
    
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

        listView = findViewById(R.id.listViewLeaderboard);
        userManager = new UserManager(this);

        List<User> users = userManager.getAllUsers();
        Collections.sort(users, (a, b) -> b.totalPoints - a.totalPoints);

        List<User> top3 = users.size() > 3 ? users.subList(0, 3) : users;

        List<String> top3Display = top3.stream()
                .map(u -> u.username + " - " + u.totalPoints + " điểm")
                .collect(Collectors.toList());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                top3Display);
        listView.setAdapter(adapter);


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
        User user = userManager.getCurrentUser();
        if (user == null) return;

        double winRate = user.loseCount + user.winCount > 0
                ? (double) user.winCount / (user.winCount + user.loseCount) * 100
                : 0;

        tvWelcome.setText("Chào mừng, " + user.username + "!");
        tvTotalPoints.setText("Tổng điểm: " + user.totalPoints);
        tvGamesPlayed.setText("Số ván đã chơi: " + (user.winCount + user.loseCount));
        tvGamesWon.setText("Số ván thắng: " + user.winCount);
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
                userManager.logout();
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
