package com.josephmpo.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.josephmpo.myapplication.databinding.ActivityReviewsBinding;

public class ReviewsActivity extends AppCompatActivity {
    ActivityReviewsBinding arb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        arb = ActivityReviewsBinding.inflate(getLayoutInflater());
        setContentView(arb.getRoot());


    }
}