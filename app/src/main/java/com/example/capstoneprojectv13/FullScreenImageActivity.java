package com.example.capstoneprojectv13;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class FullScreenImageActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        ImageView receiptIv = findViewById(R.id.receiptIv);
        String receipt = getIntent().getStringExtra("receipt");

        Glide.with(this).load(receipt).into(receiptIv);


    }
}