package com.example.quizapp_elalami;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class CameraActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        ImageView ivCapturedPhoto = findViewById(R.id.ivCapturedPhoto);
        Button btnBack = findViewById(R.id.btnBack);

        // Récupérer l'URI de l'image ou le Bitmap depuis l'intent
        String imageUriString = getIntent().getStringExtra("imageUri");
        Bitmap photo = getIntent().getParcelableExtra("data");

        if (imageUriString != null) {
            // Affichage via URI (recommandé pour les images sauvegardées)
            ivCapturedPhoto.setImageURI(Uri.parse(imageUriString));
        } else if (photo != null) {
            // Fallback pour le Bitmap direct
            ivCapturedPhoto.setImageBitmap(photo);
        }

        btnBack.setOnClickListener(v -> finish());
    }
}