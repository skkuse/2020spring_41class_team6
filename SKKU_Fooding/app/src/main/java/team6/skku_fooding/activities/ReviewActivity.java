package team6.skku_fooding.activities;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import team6.skku_fooding.R;
import team6.skku_fooding.models.Product;
import team6.skku_fooding.models.Review;


@SuppressLint("SetTextI18n")
@RequiresApi(api = Build.VERSION_CODES.N)
public class ReviewActivity extends AppCompatActivity {
    private Product p;
    private LinearLayout lnrImages;
    private LinearLayout.LayoutParams lnrParams;

    private DatabaseReference userRef;
    private DatabaseReference reviewRef;
    private DatabaseReference productRef;
    public int reviewId;
    public int categoryId;
    public int productId;
    public String uid;
    public String title;
    public String description;
    public int userScore;
    public ArrayList<String> b64Imgs;

    private View.OnClickListener openDeleteDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        reviewRef = db.getReference("review");
        productRef = db.getReference("product");
        userRef = db.getReference("user");
        productId = getIntent().getIntExtra("product_id", -1);
        b64Imgs = new ArrayList<>();

        this.uid = this.getSharedPreferences("user_SP", MODE_PRIVATE)
                .getString("UID", null);

        lnrParams = new LinearLayout.LayoutParams(800,800, 1f);
        openDeleteDialog = v -> new AlertDialog
                .Builder(ReviewActivity.this)
                .setMessage("Delete photo?")
                .setTitle("Delete")
                .setNegativeButton("No", (dialogInterface, i) -> {})
                .setPositiveButton("Yes", (dialogInterface, i) -> ReviewActivity.this.lnrImages.removeView(v))
                .create()
                .show();

        // This is mock-up data.
        // It will overwrite if query is successfully done.
        p = new Product(
                100, "Dongwon",
                convertBitmapToBase64(BitmapFactory.decodeResource(getResources(), R.drawable.test_prod)),
                "seafood",
                "Fishcake(Square, 12 pieces)",
                12000,
                new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.KOREA).format(new Date()));

        if (productId != -1) {
            productRef.child(String.valueOf(productId)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot ds) {
                    if (ds.exists()) {
                        ReviewActivity.this.p = ds.getValue(Product.class);
                        Log.d("ReviewActivity", "ProductId: " + ReviewActivity.this.productId + " successfully loaded.");
                    } else {
                        Log.w("ReviewActivity", "ProductId: " + ReviewActivity.this.productId + " not found. Replace to fallback...");
                        ReviewActivity.this.productId = -1;
                    }
                    ReviewActivity.this.refreshProductRelatedViews();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError de) {
                    ReviewActivity.this.productId = -1;
                    Log.w("ReviewActivity", "ProductId query cancelled.");
                    ReviewActivity.this.refreshProductRelatedViews();
                }
            });
        }
        categoryId = 0;
        userRef.child(uid).child("category_id").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot ds) {
                if (ds.exists()) {
                    ReviewActivity.this.categoryId = ds.getValue(Integer.class);
                    Log.d("ReviewActivity", "categoryId: " + ReviewActivity.this.categoryId + " successfully loaded.");
                } else Log.w("ReviewActivity", "categoryId not found. set it as 0.");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError de) {
                ReviewActivity.this.categoryId = 0;
                Log.w("ReviewActivity", "categoryId query cancelled.");
                ReviewActivity.this.refreshProductRelatedViews();
            }
        });

        reviewId = -1; // Initialize later with transaction.

        lnrImages = findViewById(R.id.reviewImageLinearLayout);
        findViewById(R.id.addUserImageButton).setOnClickListener(v -> startActivityForResult(
                Intent.createChooser(
                        new Intent(Intent.ACTION_GET_CONTENT)
                                .setType("image/*")
                                .putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true),
                        "Select pictures for review"),
                1));
        findViewById(R.id.sendButton).setOnClickListener(v -> {
                ReviewActivity.this.title = ((TextView)findViewById(R.id.reviewTitleView)).getText().toString();
                ReviewActivity.this.description = ((TextView)findViewById(R.id.reviewDetailText)).getText().toString();
                ArrayList<Bitmap> arr = new ArrayList<>();

                if (ReviewActivity.this.lnrImages.getChildCount() > 5) {
                    Toast.makeText(ReviewActivity.this, "You can't upload more than 5 images. Please delete some images.", Toast.LENGTH_LONG).show();
                    return;
                }
                if (ReviewActivity.this.title.length() < 1) {
                    Toast.makeText(ReviewActivity.this, "You can't upload review without title.", Toast.LENGTH_LONG).show();
                    return;
                }
                if (ReviewActivity.this.title.length() > 50) {
                    Toast.makeText(ReviewActivity.this, "Title should no more than 50 letters.", Toast.LENGTH_LONG).show();
                    return;
                }
                if (ReviewActivity.this.description.length() < 1) {
                    Toast.makeText(ReviewActivity.this, "You can't upload review without description.", Toast.LENGTH_LONG).show();
                    return;
                }
                if (ReviewActivity.this.description.length() > 500) {
                    Toast.makeText(ReviewActivity.this, "Title should no more than 500 letters.", Toast.LENGTH_LONG).show();
                    return;
                }


                for (int i = 0; i < lnrImages.getChildCount(); i++) arr.add(((BitmapDrawable)((ImageView)lnrImages.getChildAt(i)).getDrawable()).getBitmap());
                arr.parallelStream()
                        .map(this::convertBitmapToBase64)
                        .filter(Objects::nonNull)
                        .sequential()
                        .forEachOrdered(s -> b64Imgs.add(s));
                reviewRef.runTransaction(new Transaction.Handler() {
                        @NonNull @Override public Transaction.Result doTransaction(@NonNull MutableData md) {
                            ArrayList<Review> ar = md.getValue(new GenericTypeIndicator<ArrayList<Review>>() {});
                            int rid = 1;
                            if (ar != null) {
                                ar.removeAll(Collections.singleton(null));
                                rid = ar.size() + 1;
                            }

                            String now = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.KOREA).format(new Date());
                            Review r = new Review(
                                    rid,
                                    ReviewActivity.this.uid,
                                    ReviewActivity.this.productId,
                                    ReviewActivity.this.categoryId,
                                    now, now,
                                    ReviewActivity.this.description,
                                    ReviewActivity.this.title,
                                    ReviewActivity.this.userScore,
                                    b64Imgs);
                            md.child(String.valueOf(rid)).setValue(r);
                            return Transaction.success(md);
                        }

                        @Override public void onComplete(@Nullable DatabaseError de, boolean b, @Nullable DataSnapshot ds) {
                            Log.d("ReviewActivity", "postTransaction:onComplete:" + de);
                            if (de == null) {
                                Toast.makeText(ReviewActivity.this, "Review successfully uploaded!", Toast.LENGTH_SHORT).show();
                                ReviewActivity.this.finish();
                            } else {
                                Toast.makeText(ReviewActivity.this, "Review not uploaded: "+de.getDetails(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    Toast.makeText(ReviewActivity.this, "Review uploading...", Toast.LENGTH_LONG).show();
        });
        this.setStar(findViewById(R.id.fiveStarView));
    }
    @Override protected void onActivityResult(int reqCode, int resCode, Intent data) {
        super.onActivityResult(reqCode, resCode, data);

        if (reqCode == 1 && resCode == RESULT_OK && data != null) {
            // This is from addUserImageButton.
            if (data.getData() != null) {
                Toast.makeText(ReviewActivity.this, "A Image selected.", Toast.LENGTH_SHORT).show();
                    new Thread(() -> {
                        ImageView iv = new ImageView(ReviewActivity.this);
                        iv.setImageBitmap(ReviewActivity.this.convertURItoBitmap(data));
                        iv.setLayoutParams(ReviewActivity.this.lnrParams);
                        iv.setOnClickListener(ReviewActivity.this.openDeleteDialog);
                        iv.setClickable(true);
                        ReviewActivity.this.lnrImages.post(() -> ReviewActivity.this.lnrImages.addView(iv));
                    }).start();
            } else if (data.getClipData() != null) {
                ClipData mcd = data.getClipData();
                Toast.makeText(ReviewActivity.this, mcd.getItemCount() + " Images selected.", Toast.LENGTH_SHORT).show();
                ArrayList<ClipData.Item> arr = new ArrayList<>();
                for (int i = 0; i < mcd.getItemCount(); i++) arr.add(mcd.getItemAt(i));
                arr.parallelStream()
                        .map(this::convertURItoBitmap)
                        .filter(Objects::nonNull)
                        .forEachOrdered(b -> {
                            ImageView iv = new ImageView(ReviewActivity.this);
                            iv.setImageBitmap(b);
                            iv.setLayoutParams(ReviewActivity.this.lnrParams);
                            iv.setOnClickListener(ReviewActivity.this.openDeleteDialog);
                            iv.setClickable(true);
                            ReviewActivity.this.lnrImages.post(() -> ReviewActivity.this.lnrImages.addView(iv));
                        });
            }
            else Toast.makeText(ReviewActivity.this, "No Images selected.", Toast.LENGTH_SHORT).show();
        }
    }
    public void setStar(View v) {

        ((ImageView)findViewById(R.id.oneStarView)).setImageResource(R.drawable.star_white);
        ((ImageView)findViewById(R.id.twoStarView)).setImageResource(R.drawable.star_white);
        ((ImageView)findViewById(R.id.threeStarView)).setImageResource(R.drawable.star_white);
        ((ImageView)findViewById(R.id.fourStarView)).setImageResource(R.drawable.star_white);
        ((ImageView)findViewById(R.id.fiveStarView)).setImageResource(R.drawable.star_white);
        switch (v.getId()) {
            case R.id.oneStarView: userScore = 1; break;
            case R.id.twoStarView: userScore = 2; break;
            case R.id.threeStarView: userScore = 3; break;
            case R.id.fourStarView: userScore = 4; break;
            case R.id.fiveStarView: userScore = 5; break;
            default: userScore = 0;
        }
        if (userScore >= 1) ((ImageView)findViewById(R.id.oneStarView)).setImageResource(R.drawable.star_yellow);
        if (userScore >= 2) ((ImageView)findViewById(R.id.twoStarView)).setImageResource(R.drawable.star_yellow);
        if (userScore >= 3) ((ImageView)findViewById(R.id.threeStarView)).setImageResource(R.drawable.star_yellow);
        if (userScore >= 4) ((ImageView)findViewById(R.id.fourStarView)).setImageResource(R.drawable.star_yellow);
        if (userScore >= 5) ((ImageView)findViewById(R.id.fiveStarView)).setImageResource(R.drawable.star_yellow);
    }
    private void refreshProductRelatedViews() {
        byte[] ib = Base64.decode(p.image, Base64.DEFAULT);
        ((ImageView)findViewById(R.id.productImageView)).setImageBitmap(
                BitmapFactory.decodeByteArray(ib, 0, ib.length)
        );
        ((TextView)findViewById(R.id.productTextView)).setText(p.name);
    }
    private String convertBitmapToBase64(Bitmap b) {
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.WEBP, 80, bs);
        return Base64.encodeToString(bs.toByteArray(), Base64.DEFAULT);
    }
    private Bitmap convertURItoBitmap(Object it) {
        Bitmap b;
        try {
            if (it instanceof ClipData.Item) b = MediaStore.Images.Media.getBitmap(this.getContentResolver(), ((ClipData.Item)it).getUri());
            else if (it instanceof Intent) b = MediaStore.Images.Media.getBitmap(this.getContentResolver(), ((Intent)it).getData());
            else throw new IllegalArgumentException();

            int w = b.getWidth();
            int h = b.getHeight();
            double scale = (double) w / h;
            if (w > 800) {
                if (w > h) {
                    w = 800;
                    h = (int) (800.0 / scale);
                } else {
                    h = 800;
                    w = (int) (800.0 * scale);
                }
                return Bitmap.createScaledBitmap(b, w, h, true);
            } else if (h > 800) {
                h = 800;
                w = (int) (800.0 * scale);
                return Bitmap.createScaledBitmap(b, w, h, true);
            }
            return b;
        }
        catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }
}
