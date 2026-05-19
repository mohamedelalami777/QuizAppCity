package com.example.quizapp_elalami;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.io.OutputStream;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int STARTUP_PERMISSIONS_REQUEST_CODE = 101;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int CAMERA_REQUEST_CODE = 102;

    Button btnLocation, btnChooseCity, btnExplorer, btnAI, btnCamera, btnViewPhotos;
    TextView title;
    FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Binding views
        btnLocation = findViewById(R.id.btnLocation);
        btnChooseCity = findViewById(R.id.btnChooseCity);
        btnExplorer = findViewById(R.id.btnExplorer);
        btnAI = findViewById(R.id.btnAI);
        btnCamera = findViewById(R.id.btnCamera);
        btnViewPhotos = findViewById(R.id.btnViewPhotos);
        title = findViewById(R.id.title);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Animations
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);

        title.startAnimation(fadeIn);
        btnLocation.startAnimation(slideUp);
        btnChooseCity.startAnimation(slideUp);
        btnExplorer.startAnimation(slideUp);
        btnAI.startAnimation(slideUp);
        btnCamera.startAnimation(slideUp);
        btnViewPhotos.startAnimation(slideUp);

        // Listeners
        btnLocation.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.press_scale));
            askLocationPermission();
        });

        btnChooseCity.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.press_scale));
            showCityDialog();
        });

        btnExplorer.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.press_scale));
            openMoroccoMap();
        });

        btnAI.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.press_scale));
            startActivity(new Intent(MainActivity.this, ChatActivity.class));
        });

        btnCamera.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.press_scale));
            openCamera();
        });

        btnViewPhotos.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.press_scale));
            startActivity(new Intent(MainActivity.this, PhotoGalleryActivity.class));
        });

        requestStartupPermissions();
    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        } else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
            if (imageBitmap != null) {
                Uri savedUri = saveImageToGallery(imageBitmap);
                if (savedUri != null) {
                    Toast.makeText(this, "Photo enregistrée avec succès", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, CameraActivity.class);
                    intent.putExtra("imageUri", savedUri.toString());
                    startActivity(intent);
                }
            }
        }
    }

    private Uri saveImageToGallery(Bitmap bitmap) {
        Uri imageUri = null;
        try {
            String fileName = "QuizCity_" + System.currentTimeMillis() + ".jpg";
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/QuizCityPhotos");
                values.put(MediaStore.Images.Media.IS_PENDING, 1);
            }
            imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (imageUri != null) {
                try (OutputStream out = getContentResolver().openOutputStream(imageUri)) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    values.clear();
                    values.put(MediaStore.Images.Media.IS_PENDING, 0);
                    getContentResolver().update(imageUri, values, null, null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imageUri;
    }

    private void openMoroccoMap() {
        Uri gmmIntentUri = Uri.parse("geo:31.7917,-7.0926?q=Morocco");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=Morocco")));
        }
    }

    private void requestStartupPermissions() {
        String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
        ActivityCompat.requestPermissions(this, permissions, STARTUP_PERMISSIONS_REQUEST_CODE);
    }

    private void askLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STARTUP_PERMISSIONS_REQUEST_CODE) {
            // Basic handling for startup permissions
        } else if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            }
        } else if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            }
        }
    }

    private void getLocation() {
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).addOnSuccessListener(location -> {
            if (location != null) {
                new Thread(() -> {
                    try {
                        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        String city = (addresses != null && !addresses.isEmpty()) ? addresses.get(0).getLocality() : "default";
                        runOnUiThread(() -> startQuiz(city));
                    } catch (Exception e) {
                        runOnUiThread(() -> startQuiz("default"));
                    }
                }).start();
            }
        });
    }

    private void showCityDialog() {
        String[] cities = {"Casablanca", "Rabat", "Tanger", "Marrakech"};
        new AlertDialog.Builder(this).setTitle("Choisir une ville").setItems(cities, (dialog, which) -> startQuiz(cities[which])).show();
    }

    private void startQuiz(String selectedCity) {
        Intent i = new Intent(MainActivity.this, QuizActivity.class);
        i.putExtra("city", selectedCity);
        startActivity(i);
    }
}