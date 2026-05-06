package com.example.quizapp_elalami;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Button btnLocation, btnChooseCity;
    TextView title;
    FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLocation = findViewById(R.id.btnLocation);
        btnChooseCity = findViewById(R.id.btnChooseCity);
        title = findViewById(R.id.title);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // 🎬 Animations
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);

        title.startAnimation(fadeIn);
        btnLocation.startAnimation(slideUp);
        btnChooseCity.startAnimation(slideUp);

        // 📍 Localisation
        btnLocation.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.press_scale));
            askPermission();
        });

        // 🏙️ Choix ville
        btnChooseCity.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.press_scale));
            showCityDialog();
        });
    }

    // 🔐 Permission
    private void askPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        } else {
            getLocation();
        }
    }

    // 🔐 Result permission
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1 && grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            getLocation();

        } else {
            Toast.makeText(this, "Permission refusée → default quiz", Toast.LENGTH_SHORT).show();
            startQuiz("default");
        }
    }

    // 📍 Get location
    private void getLocation() {
        Toast.makeText(this, "Localisation en cours...", Toast.LENGTH_SHORT).show();

        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(location -> {

                    if (location != null) {

                        new Thread(() -> {
                            try {
                                Geocoder geocoder = new Geocoder(this, Locale.getDefault());

                                List<Address> addresses = geocoder.getFromLocation(
                                        location.getLatitude(),
                                        location.getLongitude(),
                                        1
                                );

                                String city = "default";

                                if (addresses != null && !addresses.isEmpty()) {
                                    city = addresses.get(0).getLocality();
                                }

                                String finalCity = (city != null) ? city.toLowerCase().trim() : "default";

                                runOnUiThread(() -> startQuiz(finalCity));

                            } catch (Exception e) {
                                runOnUiThread(() -> startQuiz("default"));
                            }
                        }).start();

                    } else {
                        startQuiz("default");
                    }
                });
    }

    // 🏙️ Dialog choix ville
    private void showCityDialog() {

        String[] cities = {"Casablanca", "Rabat", "Tanger", "Marrakech"};

        new AlertDialog.Builder(this)
                .setTitle("Choisir une ville")
                .setItems(cities, (dialog, which) -> {

                    String selectedCity = cities[which].toLowerCase().trim();

                    startQuiz(selectedCity);
                })
                .show();
    }

    // 🚀 Start Quiz
    private void startQuiz(String selectedCity) {

        Intent i = new Intent(MainActivity.this, QuizActivity.class);

        // 🔥 FIX IMPORTANT
        i.putExtra("city", selectedCity);

        startActivity(i);
    }
}