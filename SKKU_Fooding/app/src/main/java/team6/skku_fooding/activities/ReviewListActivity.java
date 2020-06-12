package team6.skku_fooding.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import team6.skku_fooding.R;
import team6.skku_fooding.models.Product;
import team6.skku_fooding.models.Review;

@SuppressLint("SetTextI18n")
@RequiresApi(api = Build.VERSION_CODES.N)
public class ReviewListActivity extends AppCompatActivity {
    private int productId;
    private String productName;
    private ArrayList<Review> reviews;
    private LinearLayout reviewLinearLayout;
    private DatabaseReference reviewRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_list);
        productId = getIntent().getIntExtra("product_id", -1);
        productName = getIntent().getStringExtra("product_name");

        reviewLinearLayout = findViewById(R.id.reviewLinearLayout);

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        reviewRef = db.getReference("review");
        productId = 500;
        productName = "Yes?";

        new Thread(() -> {
            String now = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.KOREA).format(new Date());
            if (productId != -1 && productName != null) {
                reviewRef.addValueEventListener(new ValueEventListener() {
                    @Override public void onDataChange(@NonNull DataSnapshot ds) {
                        if (ds.exists()) {
                            ArrayList<Review> ar = ds.getValue(new GenericTypeIndicator<ArrayList<Review>>() {});

                            if (ar != null) {
                                ReviewListActivity.this.reviews = (ArrayList<Review>) ar
                                        .stream()
                                        .filter(Objects::nonNull)
                                        .filter(r -> r.productId == productId)
                                        .collect(Collectors.toList());
                                Log.d("ReviewListActivity", "Total Reviews: " + ReviewListActivity.this.reviews.size() + ".");
                            } else {
                                Log.w("ReviewListActivity", "Review not found. Set it to null.");
                            }
                        } else {
                            Log.w("ReviewListActivity", "Review query failed. Set it to null.");
                        }
                        ReviewListActivity.this.refreshReviewList();
                    }
                    @Override public void onCancelled(@NonNull DatabaseError de) {
                        Log.w("ReviewListActivity", "Review query cancelled.");
                        ReviewListActivity.this.refreshReviewList();
                    }
                });
            }
        }).start();
    }
    private void refreshReviewList() {
        if (reviews != null && !reviews.isEmpty()) {
            reviews.forEach(r -> {
                View v = LayoutInflater.from(this).inflate(R.layout.product_detail_review, reviewLinearLayout, false);

                if (r.title.length() > 20) ((TextView)v.findViewById(R.id.reviewTitleView)).setText(r.title.substring(0, 17) + "...");
                else ((TextView)v.findViewById(R.id.reviewTitleView)).setText(r.title);
                if (r.description.length() > 50) ((TextView)v.findViewById(R.id.reviewDescView)).setText(r.description.substring(0, 47) + "...");
                else ((TextView)v.findViewById(R.id.reviewDescView)).setText(r.description);
                if (r.rate >= 1) ((ImageView)v.findViewById(R.id.oneStarView)).setImageResource(R.drawable.star_yellow);
                if (r.rate >= 2) ((ImageView)v.findViewById(R.id.twoStarView)).setImageResource(R.drawable.star_yellow);
                if (r.rate >= 3) ((ImageView)v.findViewById(R.id.threeStarView)).setImageResource(R.drawable.star_yellow);
                if (r.rate >= 4) ((ImageView)v.findViewById(R.id.fourStarView)).setImageResource(R.drawable.star_yellow);
                if (r.rate >= 5) ((ImageView)v.findViewById(R.id.fiveStarView)).setImageResource(R.drawable.star_yellow);
                if (r.b64Imgs != null) r.b64Imgs.forEach(s -> {
                        byte[] ib = Base64.decode(s, Base64.DEFAULT);
                        ImageView iv = new ImageView(this);
                        iv.setImageBitmap(BitmapFactory.decodeByteArray(ib, 0, ib.length));
                        ((LinearLayout)v.findViewById(R.id.reviewLinearLayout)).addView(iv);
                });

                reviewLinearLayout.addView(v);
            });
        }
    }
}
