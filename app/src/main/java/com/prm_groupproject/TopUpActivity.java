package com.prm_groupproject;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class TopUpActivity extends AppCompatActivity {

    EditText edtTopUpAmount;
    Button btnTopUp;
    TextView tvCurrentPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up);

        edtTopUpAmount = findViewById(R.id.edtTopUpAmount);
        btnTopUp = findViewById(R.id.btnTopUp);
        tvCurrentPoints = findViewById(R.id.tvCurrentPoints);

        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String username = prefs.getString("current_user", null);
        if (username == null) {
            finish();
            return;
        }

        int currentPoints = prefs.getInt("points_" + username, 0);
        tvCurrentPoints.setText("Điểm hiện tại: " + currentPoints);

        btnTopUp.setOnClickListener(v -> {
            String input = edtTopUpAmount.getText().toString();
            if (input.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập số điểm muốn nạp", Toast.LENGTH_SHORT).show();
                return;
            }

            int amount = Integer.parseInt(input);
            if (amount <= 0) {
                Toast.makeText(this, "Số điểm phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                return;
            }

            int newPoints = currentPoints + amount;

            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("points_" + username, newPoints);
            editor.apply();

            Toast.makeText(this, "Nạp thành công " + amount + " điểm!", Toast.LENGTH_SHORT).show();
            finish(); // Quay lại MainActivity
        });
    }
}
