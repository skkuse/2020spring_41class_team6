package team6.skku_fooding.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

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

    private View head;
    private View body;
    private View rev;
    private View prefRev;

    private DatabaseReference userRef;
    private DatabaseReference productRef;
    private DatabaseReference reviewRef;
    private int categoryId;
    private int productId;
    private View.OnClickListener addCartDialog;

    private SimpleDateFormat sdf;

    private SharedPreferences loginPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        mLinearLayout = findViewById(R.id.lnrLayout);

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        userRef = db.getReference("user");
        productRef = db.getReference("product");
        reviewRef = db.getReference("review");
        productId = Integer.parseInt(getIntent().getStringExtra("product_id"));
        loginPref = getSharedPreferences("user_SP", MODE_PRIVATE);
        final SharedPreferences.Editor editor = loginPref.edit();
        uid = loginPref.getString("UID", "IPli1mXAUUYm3npYJ48B43Pp7tQ2");

        lnrParams = new LinearLayout.LayoutParams(800, 800, 1f);

        head = LayoutInflater.from(this).inflate(R.layout.product_detail_head, mLinearLayout, false);
        body = LayoutInflater.from(this).inflate(R.layout.product_detail_body, mLinearLayout, false);

        addCartDialog = v -> {
            int cnt = Integer.parseInt((String)((TextView)head.findViewById(R.id.countTextView)).getText());
            String s = loginPref.getString("cart_item", null);
            if (s != null) s += "-" + productId + ":" + cnt + ":" + p.price;
            else s = productId + ":" + cnt + ":" + p.price;
            editor.putString("cart_item", s).apply();
            new AlertDialog
                .Builder(ProductDetailActivity.this)
                .setMessage("Item added in cart.\nWould you want to see shopping cart?")
                .setTitle("Add to Cart")
                .setNegativeButton("No", (dialogInterface, i) -> {})
                .setPositiveButton("Yes", (dialogInterface, i) -> {

                    startActivity(new Intent(getApplicationContext(), ShoppingCartActivity.class));
                    ProductDetailActivity.this.finish();
                }).create()
                .show();
            };

        String now = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(new Date());
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
                        Log.w("ProductDetailActivity", "ProductId: " + ProductDetailActivity.this.productId + " not found.");
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
            userRef.child(uid).child("category_id").addValueEventListener(new ValueEventListener() {
                @Override public void onDataChange(@NonNull DataSnapshot ds) {
                    if (ds.exists()) {
                        categoryId = ds.getValue(Integer.class);
                        Log.d("ProductDetailActivity", "user categoryId: " + categoryId + " successfully loaded.");
                        reviewRef.addValueEventListener(new ValueEventListener() {
                            @Override public void onDataChange(@NonNull DataSnapshot ds) {
                                if (ds.exists()) {
                                    ArrayList<Review> ar = ds.getValue(new GenericTypeIndicator<ArrayList<Review>>() {});
                                    if (ar != null) {
                                        Log.d("ProductDetailActivity", "Reviews: " + ar.size() + " successfully loaded.");
                                        ArrayList<Review> overall = (ArrayList<Review>) ar
                                                .stream()
                                                .filter(Objects::nonNull)
                                                .filter(it -> it.productId == productId)
                                                .sorted((o1, o2) -> {
                                                    try {
                                                        String[] s1, s2;
                                                        s1 = o1.writtenDate.split("-");
                                                        s2 = o2.writtenDate.split("-");
                                                        if (Integer.parseInt(s1[0]) < Integer.parseInt(s2[0]))
                                                            return -1;
                                                        else if (Integer.parseInt(s1[0]) > Integer.parseInt(s2[0]))
                                                            return 1;
                                                        else {
                                                            if (Integer.parseInt(s1[1]) < Integer.parseInt(s2[1]))
                                                                return -1;
                                                            else if (Integer.parseInt(s1[1]) > Integer.parseInt(s2[1]))
                                                                return 1;
                                                            else {
                                                                if (Integer.parseInt(s1[2]) < Integer.parseInt(s2[2]))
                                                                    return -1;
                                                                else if (Integer.parseInt(s1[2]) > Integer.parseInt(s2[2]))
                                                                    return 1;
                                                            }
                                                        }
                                                    } catch (NullPointerException e) {
                                                        e.printStackTrace();
                                                    }
                                                    return 0;
                                                }).collect(Collectors.toList());
                                        ArrayList<Review> pref = (ArrayList<Review>) ar
                                                .stream()
                                                .filter(Objects::nonNull)
                                                .filter(it -> it.productId == productId)
                                                .filter(it -> it.categoryId == categoryId)
                                                .sorted((o1, o2) -> {
                                                    try {
                                                        String[] s1, s2;
                                                        s1 = o1.writtenDate.split("-");
                                                        s2 = o2.writtenDate.split("-");
                                                        if (Integer.parseInt(s1[0]) < Integer.parseInt(s2[0]))
                                                            return -1;
                                                        else if (Integer.parseInt(s1[0]) > Integer.parseInt(s2[0]))
                                                            return 1;
                                                        else {
                                                            if (Integer.parseInt(s1[1]) < Integer.parseInt(s2[1]))
                                                                return -1;
                                                            else if (Integer.parseInt(s1[1]) > Integer.parseInt(s2[1]))
                                                                return 1;
                                                            else {
                                                                if (Integer.parseInt(s1[2]) < Integer.parseInt(s2[2]))
                                                                    return -1;
                                                                else if (Integer.parseInt(s1[2]) > Integer.parseInt(s2[2]))
                                                                    return 1;
                                                            }
                                                        }
                                                    } catch (NullPointerException e) {
                                                        e.printStackTrace();
                                                    }
                                                    return 0;
                                                }).collect(Collectors.toList());
                                        mLinearLayout.addView(head);
                                        mLinearLayout.addView(body);
                                        Review o = null;
                                        if (!overall.isEmpty()) {
                                            o = overall.get(overall.size()-1);
                                            if (o.b64Imgs == null) rev = LayoutInflater.from(ProductDetailActivity.this)
                                                        .inflate(R.layout.product_detail_review_noimage, mLinearLayout, false);
                                            else rev = LayoutInflater.from(ProductDetailActivity.this)
                                                        .inflate(R.layout.product_detail_review, mLinearLayout, false);
                                            rev.findViewById(R.id.readMoreButton).setOnClickListener(v -> startActivity(
                                                    new Intent(getApplicationContext(), ReviewListActivity.class)
                                                            .putExtra("product_id", productId)
                                                            .putExtra("pref", false)));
                                        } else rev = LayoutInflater.from(ProductDetailActivity.this)
                                            .inflate(R.layout.product_detail_reviewless, mLinearLayout, false);
                                        ((TextView)rev.findViewById(R.id.reviewSubjectView)).setText("All Reviews");
                                        mLinearLayout.addView(rev);
                                        ProductDetailActivity.this.refreshReviewViews(rev, o);
                                        Review pr = null;
                                        if (!pref.isEmpty()) {
                                            pr = pref.get(pref.size()-1);
                                            if (pr.b64Imgs == null) prefRev = LayoutInflater.from(ProductDetailActivity.this)
                                                    .inflate(R.layout.product_detail_review_noimage, mLinearLayout, false);
                                            else prefRev = LayoutInflater.from(ProductDetailActivity.this)
                                                    .inflate(R.layout.product_detail_review, mLinearLayout, false);
                                            prefRev.findViewById(R.id.readMoreButton).setOnClickListener(v -> startActivity(
                                                    new Intent(getApplicationContext(), ReviewListActivity.class)
                                                            .putExtra("product_id", productId)
                                                            .putExtra("pref", true)
                                                            .putExtra("category_id", categoryId)));
                                        } else prefRev = LayoutInflater.from(ProductDetailActivity.this)
                                            .inflate(R.layout.product_detail_reviewless, mLinearLayout, false);
                                        ((TextView)prefRev.findViewById(R.id.reviewSubjectView)).setText("Preference Reviews");
                                        mLinearLayout.addView(prefRev);
                                        ProductDetailActivity.this.refreshReviewViews(prefRev, pr);
                                    }
                                }
                            }
                            @Override public void onCancelled(@NonNull DatabaseError de) { Log.w("ProductDetailActivity", "review query cancelled."); }
                        });
                    } else {
                        Toast.makeText(ProductDetailActivity.this, "Login required.", Toast.LENGTH_LONG).show();
                        ProductDetailActivity.this.finish();
                    }
                }
                @Override public void onCancelled(@NonNull DatabaseError de) { Log.w("ProductDetailActivity", "user query cancelled."); }
            });
        }

        ((TextView)head.findViewById(R.id.countTextView)).setText("1");
        (head.findViewById(R.id.plusCountButton)).setOnClickListener(v -> plus());
        (head.findViewById(R.id.minusCountButton)).setOnClickListener(v -> minus());
        (head.findViewById(R.id.addCartButton)).setOnClickListener(addCartDialog);
    }
    private void plus() {
        int cnt = Integer.parseInt((String)((TextView)head.findViewById(R.id.countTextView)).getText());
        ((TextView)head.findViewById(R.id.countTextView)).setText(String.valueOf(cnt+1));
    }
    private void minus() {
        int cnt = Integer.parseInt((String)((TextView)head.findViewById(R.id.countTextView)).getText());
        if (cnt > 1) ((TextView)head.findViewById(R.id.countTextView)).setText(String.valueOf(cnt-1));
    }
    private void refreshProductViews() {
        byte[] ib = Base64.decode(p.image, Base64.DEFAULT);

        ((ImageView)head.findViewById(R.id.productImageView)).setImageBitmap(BitmapFactory.decodeByteArray(ib, 0, ib.length));
        ((TextView)body.findViewById(R.id.productCompanyView)).setText(p.company);
        ((TextView)body.findViewById(R.id.productIngTextView)).setText(p.ingredient);
        ((TextView)head.findViewById(R.id.productTitleView)).setText(p.name);
        ((TextView)head.findViewById(R.id.productPriceView)).setText("Price: " + p.price+"₩");
    }
    private void refreshReviewViews(View v, Review r) {
        if (r != null) {
            if (r.title.length() > 20) {
                ((TextView)v.findViewById(R.id.reviewTitleView)).setText(r.title.substring(0, 17) + "...");
            }
            else {
                ((TextView)v.findViewById(R.id.reviewTitleView)).setText(r.title);
            }
            if (r.description.length() > 50) {
                ((TextView)v.findViewById(R.id.reviewDescView)).setText(r.description.substring(0, 47) + "...");
            }
            else {
                ((TextView)v.findViewById(R.id.reviewDescView)).setText(r.description);
            }
            if (r.b64Imgs != null) r.b64Imgs.forEach(s -> {
                byte[] ib = Base64.decode(s, Base64.DEFAULT);
                ImageView iv = new ImageView(this);
                iv.setImageBitmap(BitmapFactory.decodeByteArray(ib, 0, ib.length));
                ((LinearLayout)v.findViewById(R.id.reviewLinearLayout)).addView(iv);
            });
            ((ImageView)v.findViewById(R.id.oneStarView)).setImageResource(R.drawable.star_white);
            ((ImageView)v.findViewById(R.id.twoStarView)).setImageResource(R.drawable.star_white);
            ((ImageView)v.findViewById(R.id.threeStarView)).setImageResource(R.drawable.star_white);
            ((ImageView)v.findViewById(R.id.fourStarView)).setImageResource(R.drawable.star_white);
            ((ImageView)v.findViewById(R.id.fiveStarView)).setImageResource(R.drawable.star_white);
            if (r.rate >= 1) ((ImageView)v.findViewById(R.id.oneStarView)).setImageResource(R.drawable.star_yellow);
            if (r.rate >= 2) ((ImageView)v.findViewById(R.id.twoStarView)).setImageResource(R.drawable.star_yellow);
            if (r.rate >= 3) ((ImageView)v.findViewById(R.id.threeStarView)).setImageResource(R.drawable.star_yellow);
            if (r.rate >= 4) ((ImageView)v.findViewById(R.id.fourStarView)).setImageResource(R.drawable.star_yellow);
            if (r.rate >= 5) ((ImageView)v.findViewById(R.id.fiveStarView)).setImageResource(R.drawable.star_yellow);
        }
    }
    private String convertBitmapToBase64(Bitmap b) {
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.WEBP, 80, bs);
        return Base64.encodeToString(bs.toByteArray(), Base64.DEFAULT);
    }
}
