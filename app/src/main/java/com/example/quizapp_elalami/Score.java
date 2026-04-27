package com.example.quizapp_elalami;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class Score extends AppCompatActivity {

    TextView tvScorePercent, tvStatusTitle, tvStatusSubtitle, tvCorrect, tvWrong;
    ProgressBar progressBar;
    Button bTry, bLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        tvScorePercent = findViewById(R.id.tvScorePercent);
        tvStatusTitle = findViewById(R.id.tvStatusTitle);
        tvStatusSubtitle = findViewById(R.id.tvStatusSubtitle);
        tvCorrect = findViewById(R.id.tvCorrect);
        tvWrong = findViewById(R.id.tvWrong);
        progressBar = findViewById(R.id.progressBar);
        bTry = findViewById(R.id.bTry);
        bLogout = findViewById(R.id.bLogout);

        int score = getIntent().getIntExtra("score", 0);
        int total = 5; // Assuming 5 questions
        int percent = (score * 100) / total;

        tvScorePercent.setText(percent + "%");
        tvCorrect.setText(String.valueOf(score));
        tvWrong.setText(String.valueOf(total - score));
        progressBar.setProgress(percent);

        if (percent >= 80) {
            tvStatusTitle.setText("Excellent !");
            tvStatusTitle.setTextColor(ContextCompat.getColor(this, R.color.success_green));
            tvStatusSubtitle.setText("Vous avez une excellente connaissance de la ville !");
        } else if (percent >= 50) {
            tvStatusTitle.setText("Bien joué !");
            tvStatusTitle.setTextColor(ContextCompat.getColor(this, R.color.warning_orange));
            tvStatusSubtitle.setText("Pas mal du tout, vous y êtes presque !");
        } else {
            tvStatusTitle.setText("Essayez encore !");
            tvStatusTitle.setTextColor(ContextCompat.getColor(this, R.color.error_red));
            tvStatusSubtitle.setText("Continuez à explorer pour en savoir plus.");
        }

        bTry.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_click));
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        bLogout.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_click));
            startActivity(new Intent(this, LoginActivity.class));
            finishAffinity();
        });
    }
}