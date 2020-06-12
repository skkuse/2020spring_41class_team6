package team6.skku_fooding.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import team6.skku_fooding.R;
import team6.skku_fooding.models.Review;

@RequiresApi(api = Build.VERSION_CODES.N)
public class ReviewDetailActivity extends AppCompatActivity {
    private Review r;
    private TextView reviewTitleView;
    private TextView reviewDescView;
    private ImageView oneStarView;
    private ImageView twoStarView;
    private ImageView threeStarView;
    private ImageView fourStarView;
    private ImageView fiveStarView;
    private LinearLayout reviewImageLinearLayout;

    private DatabaseReference reviewRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_detail);

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        reviewRef = db.getReference("review");

        reviewTitleView = findViewById(R.id.reviewTitleView);
        reviewDescView = findViewById(R.id.reviewDescView);
        reviewImageLinearLayout = findViewById(R.id.reviewImageLinearLayout);
        oneStarView = findViewById(R.id.oneStarView);
        twoStarView = findViewById(R.id.twoStarView);
        threeStarView = findViewById(R.id.threeStarView);
        fourStarView = findViewById(R.id.fourStarView);
        fiveStarView = findViewById(R.id.fiveStarView);

        new Thread(() -> {
            String reviewId = getIntent().getStringExtra("review_id");
            if (reviewId == null) {
                reviewId = "2";// return;
            }
            reviewRef.child(reviewId).addValueEventListener(new ValueEventListener() {
                @Override public void onDataChange(@NonNull DataSnapshot ds) {
                    if (ds.exists()) { r = ds.getValue(Review.class); }
                    refreshReview();
                }
                @Override public void onCancelled(@NonNull DatabaseError de) {}
            });
        }).start();
    }

    private void refreshReview() {
        reviewTitleView.setText(r.title);
        reviewDescView.setText(r.description);
        oneStarView.setImageResource(R.drawable.star_white);
        twoStarView.setImageResource(R.drawable.star_white);
        threeStarView.setImageResource(R.drawable.star_white);
        fourStarView.setImageResource(R.drawable.star_white);
        fiveStarView.setImageResource(R.drawable.star_white);
        if (r.rate >= 1) oneStarView.setImageResource(R.drawable.star_yellow);
        if (r.rate >= 2) twoStarView.setImageResource(R.drawable.star_yellow);
        if (r.rate >= 3) threeStarView.setImageResource(R.drawable.star_yellow);
        if (r.rate >= 4) fourStarView.setImageResource(R.drawable.star_yellow);
        if (r.rate >= 5) fiveStarView.setImageResource(R.drawable.star_yellow);
        if (r.b64Imgs != null) r.b64Imgs.forEach(s -> {
                byte[] ib = Base64.decode(s, Base64.DEFAULT);
                ImageView iv = new ImageView(this);
                iv.setImageBitmap(BitmapFactory.decodeByteArray(ib, 0, ib.length));
                reviewImageLinearLayout.addView(iv);
        });
    }
}
