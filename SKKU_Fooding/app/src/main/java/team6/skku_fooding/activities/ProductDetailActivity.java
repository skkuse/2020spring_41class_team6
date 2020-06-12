package team6.skku_fooding.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import team6.skku_fooding.R;
import team6.skku_fooding.models.Product;
import team6.skku_fooding.models.Review;

@SuppressLint("SetTextI18n")
@RequiresApi(api = Build.VERSION_CODES.N)
public class ProductDetailActivity extends AppCompatActivity {
    private Product p;
    private Review r;
    private String uid;
    private ViewGroup mLinearLayout;

    private LinearLayout.LayoutParams lnrParams;

    private ImageView productImageView;
    private TextView productTitleView;
    private TextView productPriceView;
    private ImageButton plusButton;
    private ImageButton minusButton;
    private TextView countView;
    private Button addCartButton;

    private TextView productDescView;
    private TextView productIngView;

    private TextView reviewTitleView;
    private ImageView oneStarView;
    private ImageView twoStarView;
    private ImageView threeStarView;
    private ImageView fourStarView;
    private ImageView fiveStarView;
    private TextView reviewDescView;
    private LinearLayout reviewLinearLayout;

    private DatabaseReference reviewRef;
    private DatabaseReference productRef;
    private int productId;
    private View.OnClickListener addCartDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        mLinearLayout = findViewById(R.id.lnrLayout);

        View head = LayoutInflater.from(this).inflate(R.layout.product_detail_head, mLinearLayout, false);
        View body = LayoutInflater.from(this).inflate(R.layout.product_detail_body, mLinearLayout, false);
        View rev = LayoutInflater.from(this).inflate(R.layout.product_detail_review, mLinearLayout, false);

        productImageView = head.findViewById(R.id.productImageView);
        productTitleView = head.findViewById(R.id.productTitleView);
        productPriceView = head.findViewById(R.id.productPriceView);
        plusButton = head.findViewById(R.id.plusCountButton);
        minusButton = head.findViewById(R.id.minusCountButton);
        countView = head.findViewById(R.id.countTextView);
        addCartButton = head.findViewById(R.id.addCartButton);

        productDescView = body.findViewById(R.id.productDescTextView);
        productIngView = body.findViewById(R.id.productIngTextView);

        reviewTitleView = rev.findViewById(R.id.reviewTitleView);
        reviewDescView = rev.findViewById(R.id.reviewDescView);
        oneStarView = rev.findViewById(R.id.oneStarView);
        twoStarView = rev.findViewById(R.id.twoStarView);
        threeStarView = rev.findViewById(R.id.threeStarView);
        fourStarView = rev.findViewById(R.id.fourStarView);
        fiveStarView = rev.findViewById(R.id.fiveStarView);
        reviewLinearLayout = rev.findViewById(R.id.reviewLinearLayout);
        rev.setOnClickListener(v -> startActivity(
                new Intent(getApplicationContext(), ReviewListActivity.class)
                        .putExtra("product_id", p.productId)
                        .putExtra("product_name", p.name)));

        mLinearLayout.addView(head);
        mLinearLayout.addView(body);
        mLinearLayout.addView(rev);

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        reviewRef = db.getReference("review");
        productRef = db.getReference("product");
        productId = getIntent().getIntExtra("product_id", 500);
        //productId = getIntent().getIntExtra("product_id", -1);

        uid = this.getSharedPreferences("user_SP", MODE_PRIVATE)
            .getString("UID", "IPli1mXAUUYm3npYJ48B43Pp7tQ2");

        lnrParams = new LinearLayout.LayoutParams(800, 800, 1f);
        addCartDialog = v -> new AlertDialog
                .Builder(ProductDetailActivity.this)
                .setMessage("Item added in cart.\nWould you want to see shopping cart?")
                .setTitle("Add to Cart")
                .setNegativeButton("No", (dialogInterface, i) -> {})
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    startActivity(new Intent(getApplicationContext(), ShoppingCartActivity.class));
                    ProductDetailActivity.this.finish();
                });
        new Thread(() -> {
            String now = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.KOREA).format(new Date());
            p = new Product(
                    100, "Dongwon",
                    convertBitmapToBase64(BitmapFactory.decodeResource(getResources(), R.drawable.test_prod)),
                    "seafood",
                    "Fishcake(Square, 12 pieces)",
                    12000,
                    now);

            if (productId != -1) {
                productRef.child(String.valueOf(productId)).addValueEventListener(new ValueEventListener() {
                    @Override public void onDataChange(@NonNull DataSnapshot ds) {
                        if (ds.exists()) {
                            ProductDetailActivity.this.p = ds.getValue(Product.class);
                            Log.d("ProductDetailActivity", "ProductId: " + ProductDetailActivity.this.productId + " successfully loaded.");
                        } else {
                            Log.w("ProductDetailActivity", "ProductId: " + ProductDetailActivity.this.productId + " not found. Replace to fallback...");
                            ProductDetailActivity.this.productId = -1;
                        }
                        ProductDetailActivity.this.refreshProductViews();
                    }
                    @Override public void onCancelled(@NonNull DatabaseError de) {
                        Log.w("ReviewActivity", "ProductId query cancelled.");
                        ProductDetailActivity.this.productId = -1;
                        ProductDetailActivity.this.refreshProductViews();
                    }
                });
                reviewRef.equalTo(productId, "productId")
                        .orderByChild("modifiedDate")
                        .addValueEventListener(new ValueEventListener() {
                            @Override public void onDataChange(@NonNull DataSnapshot ds) {
                                if (ds.exists()) {
                                    ArrayList<Review> ar = ds.getValue(new GenericTypeIndicator<ArrayList<Review>>() {});
                                    if (ar != null) {
                                        ar.removeAll(Collections.singleton(null));
                                        ProductDetailActivity.this.r = ar.get(ar.size() - 1);
                                        Log.d("ProductDetailActivity", "Review " + ProductDetailActivity.this.r.reviewId + " successfully loaded.");
                                    } else {
                                        Log.w("ProductDetailActivity", "Review not found. Set it to null.");
                                        ProductDetailActivity.this.r = null;
                                    }
                                } else {
                                    Log.w("ProductDetailActivity", "Review query failed. Set it to null.");
                                    ProductDetailActivity.this.r = null;
                                }
                                ProductDetailActivity.this.refreshReviewViews();
                            }
                            @Override public void onCancelled(@NonNull DatabaseError de) {
                                Log.w("ProductDetailActivity", "Review query cancelled.");
                                ProductDetailActivity.this.r = null;
                                ProductDetailActivity.this.refreshReviewViews();
                            }
                        });
            }
        }).start();
    }
    private void refreshProductViews() {
        byte[] ib = Base64.decode(p.image, Base64.DEFAULT);
        productImageView.setImageBitmap(BitmapFactory.decodeByteArray(ib, 0, ib.length));
        productTitleView.setText(p.name);
        productPriceView.setText("Price: "+String.valueOf(p.price));
        countView.setText("1");
    }
    private void refreshReviewViews() {
        if (r != null) {
            if (r.title.length() > 20) reviewTitleView.setText(r.title.substring(0, 17) + "...");
            else reviewTitleView.setText(r.title);
            if (r.description.length() > 50)
                reviewTitleView.setText(r.description.substring(0, 47) + "...");
            else reviewTitleView.setText(r.description);
            r.b64Imgs.forEach(s -> {
                byte[] ib = Base64.decode(s, Base64.DEFAULT);
                ImageView iv = new ImageView(this);
                iv.setImageBitmap(BitmapFactory.decodeByteArray(ib, 0, ib.length));
                reviewLinearLayout.addView(iv);
            });
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
            // Toast.makeText(this, "haha!", Toast.LENGTH_LONG).show();
        }
    }
    private String convertBitmapToBase64(Bitmap b) {
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.WEBP, 80, bs);
        return Base64.encodeToString(bs.toByteArray(), Base64.DEFAULT);
    }
}
