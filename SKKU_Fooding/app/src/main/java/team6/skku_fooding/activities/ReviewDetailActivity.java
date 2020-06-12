package team6.skku_fooding.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import team6.skku_fooding.R;

public class ReviewDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_detail);

        String reviewId = getIntent().getStringExtra("review_id");

    }
}
