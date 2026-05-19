package com.example.quizapp_elalami;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PhotoGalleryActivity extends AppCompatActivity {

    private RecyclerView rvGallery;
    private PhotoAdapter adapter;
    private List<Uri> photoUris;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_gallery);

        rvGallery = findViewById(R.id.rvGallery);
        photoUris = new ArrayList<>();

        // Setup RecyclerView with a grid of 3 columns
        rvGallery.setLayoutManager(new GridLayoutManager(this, 3));
        
        adapter = new PhotoAdapter(this, photoUris, uri -> {
            // Open full screen preview
            Intent intent = new Intent(PhotoGalleryActivity.this, FullScreenPhotoActivity.class);
            intent.putExtra("imageUri", uri.toString());
            startActivity(intent);
        });
        
        rvGallery.setAdapter(adapter);

        // Add layout animation for smooth entry
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_fall_down);
        rvGallery.setLayoutAnimation(animation);

        loadPhotos();
    }

    private void loadPhotos() {
        Uri collection;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
            collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }

        String[] projection = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.RELATIVE_PATH
        };

        // Filter to show only photos from our folder
        String selection = MediaStore.Images.Media.RELATIVE_PATH + " LIKE ?";
        String[] selectionArgs = new String[]{"%QuizCityPhotos%"};
        String sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC";

        try (Cursor cursor = getContentResolver().query(collection, projection, selection, selectionArgs, sortOrder)) {
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);

            while (cursor.moveToNext()) {
                long id = cursor.getLong(idColumn);
                Uri contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                photoUris.add(contentUri);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        adapter.notifyDataSetChanged();
        rvGallery.scheduleLayoutAnimation();
    }
}