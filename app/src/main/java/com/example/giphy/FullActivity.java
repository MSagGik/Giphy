package com.example.giphy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class FullActivity extends AppCompatActivity {

    private ImageView fullImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full);

        fullImageView = findViewById(R.id.fullImage);

        Intent receiver = getIntent();
        String sourceUrl = receiver.getStringExtra("imageUrl");

        Glide.with(this).load(sourceUrl).into(fullImageView);

    }
}