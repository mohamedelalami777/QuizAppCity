package com.example.quizapp_elalami;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class FullScreenPhotoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_photo);

        ImageView ivFullScreen = findViewById(R.id.ivFullScreen);
        ImageButton btnClose = findViewById(R.id.btnCloseFull);

        String imageUriString = getIntent().getStringExtra("imageUri");
        if (imageUriString != null) {
            ivFullScreen.setImageURI(Uri.parse(imageUriString));
        }

        btnClose.setOnClickListener(v -> finish());
    }
}