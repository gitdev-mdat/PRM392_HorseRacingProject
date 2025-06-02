package com.prm_groupproject;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    ProgressBar[] progresses;
    CheckBox[] checks;
    EditText[] bets;
    Button btnStart, btnReset;
    Handler handler = new Handler();
    Runnable runnable;
    int totalWin = 0;

    ImageView winnerIcon;
    TextView winnerText, totalWinText;
    LinearLayout winBox;

    boolean isRacing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        btnStart.setOnClickListener(v -> {
            if (isRacing) return;
            startRace();
        });

        btnReset.setOnClickListener(v -> resetRace());
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
        int reward = 0;

        if (checks[winner].isChecked()) {
            String betStr = bets[winner].getText().toString();
            int bet = betStr.isEmpty() ? 0 : Integer.parseInt(betStr);
            reward = bet * 2;
            totalWin += reward;
            msg += "Bạn thắng: " + reward + " điểm!";
        } else {
            msg += "Bạn đã cược sai!";
        }

        winBox.setVisibility(View.VISIBLE);
        winnerText.setText("Ngựa " + (winner + 1) + " chiến thắng!");
        totalWinText.setText("Tổng điểm thắng đang có: " + totalWin);

        int[] horseIcons = {
                R.drawable.horse1,
                R.drawable.horse2,
                R.drawable.horse3
        };
        winnerIcon.setImageResource(horseIcons[winner]);

        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}