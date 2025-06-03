package com.prm_groupproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.*;

public class MenuActivity extends AppCompatActivity {

    Button btnPlayGame, btnLogout;
    LinearLayout rankingLayout;
    TextView tvPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        btnPlayGame = findViewById(R.id.btnPlayGame);
        btnLogout = findViewById(R.id.btnLogout);
        rankingLayout = findViewById(R.id.rankingLayout);
        tvPlayer = findViewById(R.id.tvPlayer);

        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String username = prefs.getString("current_user", null);

        tvPlayer.setText("Người chơi: " + username);

        btnPlayGame.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, MainActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = getSharedPreferences("user_prefs", MODE_PRIVATE).edit();
            editor.remove("current_user");
            editor.apply();

            Intent intent = new Intent(MenuActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        showTop3Ranking();
    }

    private void showTop3Ranking() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        Set<String> userSet = prefs.getStringSet("all_users", new HashSet<>());

        Map<String, Integer> userPointsMap = new HashMap<>();
        for (String user : userSet) {
            int points = prefs.getInt("points_" + user, 0);
            userPointsMap.put(user, points);
        }

        List<Map.Entry<String, Integer>> sortedList = new ArrayList<>(userPointsMap.entrySet());
        sortedList.sort((a, b) -> b.getValue() - a.getValue());

        rankingLayout.removeAllViews();

        for (int i = 0; i < Math.min(3, sortedList.size()); i++) {
            String username = sortedList.get(i).getKey();
            int points = sortedList.get(i).getValue();

            TextView tv = new TextView(this);
            tv.setText((i + 1) + ". " + username + " - " + points + " điểm");
            tv.setTextSize(18);
            rankingLayout.addView(tv);
        }

        if (sortedList.isEmpty()) {
            TextView tv = new TextView(this);
            tv.setText("Chưa có người chơi nào.");
            tv.setTextSize(18);
            rankingLayout.addView(tv);
        }
    }
}
