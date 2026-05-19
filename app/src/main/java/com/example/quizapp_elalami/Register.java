package com.example.quizapp_elalami;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Register extends AppCompatActivity {

    EditText etMail, etPassword, etPassword1;
    Button bRegister, bBackToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etMail = findViewById(R.id.etMail);
        etPassword = findViewById(R.id.etPassword);
        etPassword1 = findViewById(R.id.etPassword1);
        bRegister = findViewById(R.id.bRegister);
        bBackToLogin = findViewById(R.id.bBackToLogin);

        bRegister.setOnClickListener(v -> {
            String mail = etMail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String passwordConfirm = etPassword1.getText().toString().trim();

            if (TextUtils.isEmpty(mail) || TextUtils.isEmpty(password) || TextUtils.isEmpty(passwordConfirm)) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(passwordConfirm)) {
                Toast.makeText(this, "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save user locally (SharedPreferences) - Restored offline behavior
            SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(mail, password);
            editor.apply();

            Toast.makeText(Register.this, "Compte créé avec succès !", Toast.LENGTH_SHORT).show();
            
            Intent i = new Intent(Register.this, LoginActivity.class);
            startActivity(i);
            finish();
        });

        bBackToLogin.setOnClickListener(v -> {
            startActivity(new Intent(Register.this, LoginActivity.class));
            finish();
        });
    }
}