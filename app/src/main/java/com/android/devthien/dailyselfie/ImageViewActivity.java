package com.android.devthien.dailyselfie;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

public class ImageViewActivity extends AppCompatActivity {
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        imageView = findViewById(R.id.image_view_full);
        Intent intent = getIntent();
        Bitmap photo = (Bitmap) intent.getParcelableExtra("bitmap");
        imageView.setImageBitmap(photo);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}