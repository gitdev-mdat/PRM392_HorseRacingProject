package com.prm_groupproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    ProgressBar[] progresses;
    CheckBox[] checks;
    EditText[] bets;
    Button btnStart, btnReset;
    Handler handler = new Handler();
    Runnable runnable;
    int totalWin = 0;
    int betResult = 0;

    ImageView winnerIcon;
    TextView winnerText, totalWinText, betWinResultText, betLoseResultText, betResultText;
    LinearLayout winBox;
    Button btnLogout;
    TextView tvPoints;
    Button btnTopUp;
    TextView tvPlayer;

    boolean isRacing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);

        String username = prefs.getString("current_user", null); // Lấy user đang đăng nhập
        if (username == null) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        progresses = new ProgressBar[]{
                findViewById(R.id.progress1),
                findViewById(R.id.progress2),
                findViewById(R.id.progress3)
        };
        checks = new CheckBox[]{
                findViewById(R.id.check1),
                findViewById(R.id.check2),
                findViewById(R.id.check3)
        };
        bets = new EditText[]{
                findViewById(R.id.bet1),
                findViewById(R.id.bet2),
                findViewById(R.id.bet3)
        };

        btnStart = findViewById(R.id.btnStart);
        btnReset = findViewById(R.id.btnReset);

        winnerIcon = findViewById(R.id.winnerIcon);
        winnerText = findViewById(R.id.winnerText);
        totalWinText = findViewById(R.id.totalWinText);
        winBox = findViewById(R.id.winBox);
        tvPlayer = findViewById(R.id.tvPlayer);
        betWinResultText = findViewById(R.id.betWinResultText);
        betLoseResultText = findViewById(R.id.betLoseResultText);
        betResultText = findViewById(R.id.betResultText);

        // Lấy điểm đã lưu từ SharedPreferences
        totalWin = prefs.getInt("points_" + username, 0);
        totalWinText.setText("Tổng điểm thắng đang có: " + totalWin);

        btnStart.setOnClickListener(v -> {
            if (isRacing) return;
            startRace();
        });

        btnReset.setOnClickListener(v -> resetRace());

        btnLogout = findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = getSharedPreferences("user_prefs", MODE_PRIVATE).edit();
            editor.remove("current_user"); // chỉ xóa user đăng nhập hiện tại
            editor.apply();

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        tvPoints = findViewById(R.id.tvPoints);


        int points = prefs.getInt("points_" + username, 0);

        tvPoints.setText("Điểm hiện có: " + points);
        tvPlayer.setText("Người chơi: " + username);

        btnTopUp = findViewById(R.id.btnTopUp);
        btnTopUp.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TopUpActivity.class);
            startActivity(intent);
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String username = prefs.getString("current_user", null);
        if (username != null) {
            totalWin = prefs.getInt("points_" + username, 0);
            tvPoints.setText("Điểm hiện có: " + totalWin);
            totalWinText.setText("Tổng điểm thắng đang có: " + totalWin);
        }
    }

    private void startRace() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String username = prefs.getString("current_user", null);
        if (username == null) return;

        // Tính tổng điểm cược
        int totalBet = 0;
        for (int i = 0; i < 3; i++) {
            if (checks[i].isChecked()) {
                String betStr = bets[i].getText().toString();
                int bet = betStr.isEmpty() ? 0 : Integer.parseInt(betStr);
                totalBet += bet;
            }
        }

        int currentPoints = prefs.getInt("points_" + username, 0);

        // Ngăn đặt cược nếu điểm không đủ
        if (totalBet == 0) {
            Toast.makeText(this, "Vui lòng đặt cược trước khi bắt đầu!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (totalBet > currentPoints) {
            Toast.makeText(this, "Không đủ điểm để đặt cược!", Toast.LENGTH_SHORT).show();
            return;
        }

        resetRace();
        isRacing = true;
        Random random = new Random();

        runnable = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 3; i++) {
                    int progress = progresses[i].getProgress();
                    progresses[i].setProgress(progress + random.nextInt(10));

                    if (progresses[i].getProgress() >= 100) {
                        isRacing = false;
                        handler.removeCallbacks(runnable);
                        checkResult(i);
                        return;
                    }
                }
                handler.postDelayed(this, 100);
            }
        };
        handler.post(runnable);
    }


    private void resetRace() {
        handler.removeCallbacks(runnable);
        for (ProgressBar bar : progresses) bar.setProgress(0);
        winBox.setVisibility(View.GONE);
        isRacing = false;
    }

    private void checkResult(int winner) {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String username = prefs.getString("current_user", null);
        if (username == null) return;

        String msg = "Ngựa " + (winner + 1) + " thắng!\n";
        int reward = 0;
        int totalBet = 0;
        boolean isCorrect = false;

        for (int i = 0; i < 3; i++) {
            if (checks[i].isChecked()) {
                String betStr = bets[i].getText().toString();
                int bet = betStr.isEmpty() ? 0 : Integer.parseInt(betStr);
                totalBet += bet;

                if (i == winner) {
                    isCorrect = true;
                    reward += bet * 2;
                }
            }
        }
        totalWin -= totalBet;
        if (isCorrect) {
            totalWin += reward;
            betResult = reward -totalBet;
            betLoseResultText.setText("Tổng điểm đã cược: " + totalBet);
            betWinResultText.setText("Tổng điểm thắng: " + reward);
            if (betResult < 0) {
                betResultText.setText("Bạn đã thua: " + Math.abs(betResult));
            } else {
                betResultText.setText("Bạn đã thắng: " + betResult);
            }
        } else {
            if (totalWin < 0) totalWin = 0;
            betResult = totalBet;
            betLoseResultText.setText("Tổng điểm đã cược: " + totalBet);
            betWinResultText.setText("Tổng điểm thắng: " + reward);
            betResultText.setText("bạn đã thua: " + totalBet);
        }

        // Cập nhật điểm và lưu vào SharedPreferences
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("points_" + username, totalWin);

        // Đảm bảo username được thêm vào all_users
        Set<String> allUsers = prefs.getStringSet("all_users", new HashSet<>());
        if (!allUsers.contains(username)) {
            allUsers.add(username);
            editor.putStringSet("all_users", allUsers);
        }

        editor.apply();

        // Cập nhật giao diện
        tvPoints.setText("Điểm hiện có: " + totalWin);
        totalWinText.setText("Tổng điểm đang có: " + totalWin);

        winBox.setVisibility(View.VISIBLE);
        winnerText.setText("Ngựa " + (winner + 1) + " chiến thắng!");

        int[] horseIcons = {
                R.drawable.horse1,
                R.drawable.horse2,
                R.drawable.horse3
        };
        winnerIcon.setImageResource(horseIcons[winner]);

        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

}
