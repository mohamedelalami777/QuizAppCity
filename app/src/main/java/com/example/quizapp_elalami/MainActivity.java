package com.example.quizapp_elalami;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
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

    private static final int STARTUP_PERMISSIONS_REQUEST_CODE = 101;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    Button btnLocation, btnChooseCity, btnExplorer, btnAI;
    TextView title;
    FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLocation = findViewById(R.id.btnLocation);
        btnChooseCity = findViewById(R.id.btnChooseCity);
        btnExplorer = findViewById(R.id.btnExplorer);
        btnAI = findViewById(R.id.btnAI);
        title = findViewById(R.id.title);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // 🎬 Animations
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);

        title.startAnimation(fadeIn);
        btnLocation.startAnimation(slideUp);
        btnChooseCity.startAnimation(slideUp);
        btnExplorer.startAnimation(slideUp);
        btnAI.startAnimation(slideUp);

        // 📍 Localisation
        btnLocation.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.press_scale));
            askLocationPermission();
        });

        // 🏙️ Choix ville
        btnChooseCity.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.press_scale));
            showCityDialog();
        });

        // 🗺️ Explorer les villes
        btnExplorer.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.press_scale));
            openMoroccoMap();
        });

        // 🤖 Assistant IA
        btnAI.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.press_scale));
            startActivity(new Intent(MainActivity.this, ChatActivity.class));
        });

        // 🛡️ Request Camera & Microphone permissions on launch
        requestStartupPermissions();
    }

    private void openMoroccoMap() {
        Uri gmmIntentUri = Uri.parse("geo:31.7917,-7.0926?q=Morocco");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            // Fallback to web if Maps app is not installed
            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=Morocco"));
            startActivity(webIntent);
        }
    }

    private void requestStartupPermissions() {
        String[] permissions = {
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
        };

        boolean needsRequest = false;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                needsRequest = true;
                break;
            }
        }

        if (needsRequest) {
            ActivityCompat.requestPermissions(this, permissions, STARTUP_PERMISSIONS_REQUEST_CODE);
        }
    }

    // 🔐 Location Permission
    private void askLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);

        } else {
            getLocation();
        }
    }

    // 🔐 Result permissions handling
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == STARTUP_PERMISSIONS_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                Toast.makeText(this, "Permissions Caméra & Micro accordées", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Certaines permissions (Caméra/Micro) ont été refusées", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                Toast.makeText(this, "Permission localisation refusée → default quiz", Toast.LENGTH_SHORT).show();
                startQuiz("default");
            }
        }
    }

    // 📍 Get location logic
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
        i.putExtra("city", selectedCity);
        startActivity(i);
    }
}