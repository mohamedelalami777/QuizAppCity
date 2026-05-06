package com.example.quizapp_elalami;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText etUsername, etPassword;
    Button bLogin, bGoToRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        bLogin = findViewById(R.id.btnLogin);
        bGoToRegister = findViewById(R.id.btnGoToRegister);

        bLogin.setOnClickListener(v -> {
            String user = etUsername.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();

            if (TextUtils.isEmpty(user) || TextUtils.isEmpty(pass)) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // 🔥 FIX هنا
            if (user.equals("toto") && pass.equals("123")) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                String registeredPass = prefs.getString(user, null);

                if (registeredPass != null && registeredPass.equals(pass)) {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });

        bGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, Register.class));
        });
    }
}