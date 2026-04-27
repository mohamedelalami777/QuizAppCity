package com.example.quizapp_elalami;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText etUsername, etPassword;
    Button bLogin, bGoToRegister;
    ImageView ivLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        bLogin = findViewById(R.id.btnLogin);
        bGoToRegister = findViewById(R.id.btnGoToRegister);
        ivLogo = findViewById(R.id.ivLogo);

        // Entry Animations
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        
        ivLogo.startAnimation(fadeIn);
        etUsername.startAnimation(slideUp);
        etPassword.startAnimation(slideUp);
        bLogin.startAnimation(slideUp);
        bGoToRegister.startAnimation(fadeIn);

        bLogin.setOnClickListener(v -> {
            // Scale animation on click
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.press_scale));
            
            String user = etUsername.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();

            if (TextUtils.isEmpty(user) || TextUtils.isEmpty(pass)) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Fixed credentials: username "toto", password "123"
            if (user.equals("toto") && pass.equals("123")) {
                navigateToMain();
            } else {
                // Check SharedPreferences for registered users
                SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                String registeredPass = prefs.getString(user, null);

                if (registeredPass != null && registeredPass.equals(pass)) {
                    navigateToMain();
                } else {
                    Toast.makeText(this, "Invalid credentials (use toto/123)", Toast.LENGTH_SHORT).show();
                }
            }
        });

        bGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, Register.class));
        });
    }

    private void navigateToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}