package team6.skku_fooding.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import team6.skku_fooding.R;
import team6.skku_fooding.models.Ingredient;
import team6.skku_fooding.models.Product;

public class ReviewActivity extends AppCompatActivity {
    public int userScore;
    public Product p;
    public String title;
    public String description;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        if (false) {
            p = new Product(
                    Integer.parseInt(
                            getIntent().getStringExtra("PRODUCT_ID")
                    )
            );
        } else {
            p = new Product();
            p.productId = 100;
            p.companyName = "Dongwon";
            p.uploadedDate = new Date();
            p.ingredients = new ArrayList<Ingredient>();
            p.overallScore = 3.72;
            p.productName = "Fishcake(Square, 12 pieces)";
            p.price = 12000;
            ((ImageView)findViewById(R.id.productImageView)).setImageResource(R.drawable.test_prod);
        }
        ((TextView)findViewById(R.id.productTextView)).setText("Write review about " + p.productName + ".");
        ((ImageView)findViewById(R.id.oneStarView)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReviewActivity.this.userScore = 1;
            }
        });
        ((ImageView)findViewById(R.id.twoStarView)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReviewActivity.this.userScore = 2;
            }
        });
        ((ImageView)findViewById(R.id.threeStarView)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReviewActivity.this.userScore = 3;
            }
        });
        ((ImageView)findViewById(R.id.fourStarView)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReviewActivity.this.userScore = 4;
            }
        });
        ((ImageView)findViewById(R.id.fiveStarView)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReviewActivity.this.userScore = 5;
            }
        });

        ((Button)findViewById(R.id.sendButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: do click actions.
            }
        });
    }
}
