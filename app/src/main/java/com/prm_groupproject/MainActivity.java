package com.prm_groupproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;


import java.util.Random;

public class MainActivity extends AppCompatActivity {

    ProgressBar[] progresses;
    CheckBox[] checks;
    EditText[] bets;
    Button btnStart, btnReset, btnMenu;
    Handler handler = new Handler();
    Runnable runnable;
    Button btnAddPoints;


    ImageView winnerIcon;
    TextView winnerText, totalWinText, currentPointsText;
    LinearLayout winBox;

    boolean isRacing = false;
    UserManager userManager;
    int currentPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userManager = new UserManager(this);
        
        // Kiểm tra đã đăng nhập chưa
        if (!userManager.isLoggedIn()) {
            goToLogin();
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
        btnMenu = findViewById(R.id.btnMenu);
        btnAddPoints = findViewById(R.id.btnAddPoints);

        btnAddPoints.setOnClickListener(v -> showAddPointsDialog());


        winnerIcon = findViewById(R.id.winnerIcon);
        winnerText = findViewById(R.id.winnerText);
        totalWinText = findViewById(R.id.totalWinText);
        currentPointsText = findViewById(R.id.currentPointsText);
        winBox = findViewById(R.id.winBox);

        // Load điểm hiện tại
        currentPoints = userManager.getTotalPoints();
        updatePointsDisplay();

        btnStart.setOnClickListener(v -> {
            if (isRacing) return;
            if (validateBets()) {
                startRace();
            }
        });

        btnReset.setOnClickListener(v -> resetRace());
        btnMenu.setOnClickListener(v -> goToMenu());
    }

    private boolean validateBets() {
        int totalBet = 0;
        boolean hasValidBet = false;

        for (int i = 0; i < 3; i++) {
            if (checks[i].isChecked()) {
                String betStr = bets[i].getText().toString().trim();
                if (betStr.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập số tiền cược cho ngựa " + (i + 1), Toast.LENGTH_SHORT).show();
                    return false;
                }
                
                try {
                    int bet = Integer.parseInt(betStr);
                    if (bet <= 0) {
                        Toast.makeText(this, "Số tiền cược phải lớn hơn 0!", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    totalBet += bet;
                    hasValidBet = true;
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Số tiền cược không hợp lệ!", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }

        if (!hasValidBet) {
            Toast.makeText(this, "Vui lòng chọn ít nhất một ngựa để cược!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (totalBet > currentPoints) {
            Toast.makeText(this, "Không đủ điểm để cược! Điểm hiện tại: " + currentPoints, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void startRace() {
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
        String msg = "Ngựa " + (winner + 1) + " thắng!\n";
        int totalBet = 0;
        int totalReward = 0;
        boolean won = false;

        // Tính toán cược và phần thưởng
        for (int i = 0; i < 3; i++) {
            if (checks[i].isChecked()) {
                String betStr = bets[i].getText().toString().trim();
                int bet = betStr.isEmpty() ? 0 : Integer.parseInt(betStr);
                totalBet += bet;
                
                if (i == winner) {
                    totalReward = bet * 2; // Thắng được gấp đôi
                    won = true;
                }
            }
        }

        // Cập nhật điểm
        currentPoints -= totalBet; // Trừ tiền cược
        if (won) {
            currentPoints += totalReward; // Cộng tiền thắng
            msg += "Bạn thắng: +" + totalReward + " điểm!";
        } else {
            msg += "Bạn thua: -" + totalBet + " điểm!";
        }

        // Lưu điểm mới
        userManager.updatePoints(currentPoints);
        userManager.updateGameStats(won);

        updatePointsDisplay();

        winBox.setVisibility(View.VISIBLE);
        winnerText.setText("Ngựa " + (winner + 1) + " chiến thắng!");
        totalWinText.setText("Điểm sau ván: " + currentPoints);

        int[] horseIcons = {
                R.drawable.horse1,
                R.drawable.horse2,
                R.drawable.horse3
        };
        winnerIcon.setImageResource(horseIcons[winner]);

        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();

        // Kiểm tra hết điểm
        if (currentPoints <= 0) {
            Toast.makeText(this, "Bạn đã hết điểm! Quay về menu để xem thống kê.", Toast.LENGTH_LONG).show();
        }
    }

    private void updatePointsDisplay() {
        currentPointsText.setText("Điểm hiện tại: " + currentPoints);
    }
    private void showAddPointsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nạp thêm điểm");

        final EditText input = new EditText(this);
        input.setHint("Nhập số điểm muốn nạp");
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("Nạp", (dialog, which) -> {
            String value = input.getText().toString().trim();
            if (!value.isEmpty()) {
                try {
                    int addedPoints = Integer.parseInt(value);
                    if (addedPoints > 0) {
                        currentPoints += addedPoints;
                        userManager.updatePoints(currentPoints);
                        updatePointsDisplay();
                        Toast.makeText(this, "Đã nạp " + addedPoints + " điểm!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Số điểm phải lớn hơn 0!", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Giá trị không hợp lệ!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Vui lòng nhập số điểm!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());

        builder.show();
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
