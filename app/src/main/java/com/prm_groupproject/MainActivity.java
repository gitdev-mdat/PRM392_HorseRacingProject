package com.prm_groupproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    ProgressBar[] progresses;
    CheckBox[] checks;
    EditText[] bets;
    Button btnStart, btnReset, btnMenu;
    Handler handler = new Handler();
    Runnable runnable;

    ImageView winnerIcon;
    TextView winnerText, totalWinText, currentPointsText, betWinResultText, betLoseResultText, betResultText;
    LinearLayout winBox;
    Button btnRecharge;

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

        winnerIcon = findViewById(R.id.winnerIcon);
        winnerText = findViewById(R.id.winnerText);
        totalWinText = findViewById(R.id.totalWinText);
        currentPointsText = findViewById(R.id.currentPointsText);
        winBox = findViewById(R.id.winBox);
        betWinResultText = findViewById(R.id.betWinResultText);
        betLoseResultText = findViewById(R.id.betLoseResultText);
        betResultText = findViewById(R.id.betResultText);
        btnRecharge = findViewById(R.id.btnRecharge);

        btnRecharge.setOnClickListener(v -> showRechargeDialog());
        // Load điểm hiện tại
        User user = userManager.getCurrentUser();
        currentPoints = user.totalPoints;
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

    private void showRechargeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nạp điểm");

        final EditText input = new EditText(this);
        input.setHint("Nhập số điểm muốn nạp");
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("Xác nhận", (dialog, which) -> {
            String inputStr = input.getText().toString().trim();
            if (!inputStr.isEmpty()) {
                try {
                    int amount = Integer.parseInt(inputStr);
                    if (amount > 0) {
                        currentPoints += amount;
                        userManager.updatePoints(currentPoints);
                        updatePointsDisplay();
                        Toast.makeText(this, "Nạp thành công: +" + amount + " điểm!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Số điểm phải lớn hơn 0!", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Số điểm không hợp lệ!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Vui lòng nhập số điểm!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
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
                    totalReward += bet * 2; // Thắng được gấp đôi
                    won = true;
                }
            }
        }

        // Cập nhật điểm
        currentPoints -= totalBet; // Trừ tiền cược
        if (won) {
            currentPoints += totalReward; // Cộng tiền thắng
            betLoseResultText.setText("Tổng điểm đã cược: " + totalBet);
            betWinResultText.setText("Tổng điểm thắng: " + totalReward);
            if ((totalReward - totalBet) < 0) {
                betResultText.setText("Bạn đã thua: " + Math.abs(totalReward - totalBet));
            } else {
                betResultText.setText("Bạn đã thắng: " + (totalReward - totalBet));
            }
            msg += "Bạn thắng: +" + totalReward + " điểm!";
        } else {
            betLoseResultText.setText("Tổng điểm đã cược: " + totalBet);
            betWinResultText.setText("Tổng điểm thắng: " + totalReward);
            betResultText.setText("bạn đã thua: " + totalBet);
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
