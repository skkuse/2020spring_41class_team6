package team6.skku_fooding.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import team6.skku_fooding.R;
import team6.skku_fooding.models.Ingredient;
import team6.skku_fooding.models.Product;
import team6.skku_fooding.models.Review;


@SuppressLint("SetTextI18n")
public class ReviewActivity extends AppCompatActivity {
    private Product p;
    private LinearLayout lnrImages;
    private FirebaseDatabase db;
    private DatabaseReference userRef;
    private DatabaseReference reviewRef;
    private DatabaseReference productRef;
    private SharedPreferences loginPref;
    public int reviewId;
    public int categoryId;
    public int productId;
    public String title;
    public String description;
    public int userScore;
    public ArrayList<String> b64Imgs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        db = FirebaseDatabase.getInstance();
        userRef = db.getReference("user");
        reviewRef = db.getReference("review");
        productRef = db.getReference("product");
        productId = getIntent().getIntExtra("product_id", -1);
        loginPref = this.getSharedPreferences("user_SP", this.MODE_PRIVATE);
        b64Imgs = new ArrayList<>();


        // This is mock-up data.
        // It will overwrite if query is successfully done.
        new Thread(new Runnable() {
            public void run() {
                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                BitmapFactory.decodeResource(getResources(), R.drawable.test_prod)
                        .compress(Bitmap.CompressFormat.PNG,100, bs);
                p = new Product(
                        100, "Dongwon",
                        Base64.encodeToString(bs.toByteArray(), Base64.DEFAULT),
                        "seafood",
                        "Fishcake(Square, 12 pieces)",
                        12000,
                        (new Date()).toString());
            }
        }).start();

        if (productId != -1) {
            productRef.child(String.valueOf(productId)).addValueEventListener(new ValueEventListener() {
                @Override public void onDataChange(@NonNull DataSnapshot ds) {
                    if (ds.exists()) {
                        ReviewActivity.this.p = ds.getValue(Product.class);
                        Log.d("ReviewActivity", "ProductId: "+ReviewActivity.this.productId+" successfully loaded.");
                    }
                    else {
                        ReviewActivity.this.productId = -1;
                        Log.w("ReviewActivity", "ProductId: "+ReviewActivity.this.productId+" not found. Replace to fallback...");
                    }
                    ReviewActivity.this.refreshProductRelatedViews();
                }
                @Override public void onCancelled(@NonNull DatabaseError de) {
                    ReviewActivity.this.productId = -1;
                    Log.w("ReviewActivity", "ProductId query cancelled.");
                    ReviewActivity.this.refreshProductRelatedViews();
                }
            });
        }
        reviewId = -1; // Initialize later with transaction.
        // TODO: categoryId is missing...
        /*
        * Two ways to determine Category Id:
        * 1. get category id from sharedpreference
        * 2. query from firebase with user_id key
        * */

        lnrImages = (LinearLayout)findViewById(R.id.reviewImageLinearLayout);
        ((Button)findViewById(R.id.addUserImageButton)).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(Intent.createChooser(i, "Select Picutres for review"), 1);
            }
        });
        ((Button)findViewById(R.id.sendButton)).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                ReviewActivity.this.title = ((TextView)findViewById(R.id.reviewTitleView)).getText().toString();
                ReviewActivity.this.description = ((TextView)findViewById(R.id.reviewDetailText)).getText().toString();
                b64Imgs = new ArrayList<>();
                for (int i = 0; i < lnrImages.getChildCount(); i++) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ((BitmapDrawable)((ImageView)lnrImages.getChildAt(i)).getDrawable()).getBitmap().compress(Bitmap.CompressFormat.WEBP, 60, baos);
                    b64Imgs.add(Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT));
                }
                reviewRef.runTransaction(new Transaction.Handler() {
                    @NonNull @Override public Transaction.Result doTransaction(@NonNull MutableData md) {
                        ArrayList<Review> ar = md.getValue(new GenericTypeIndicator<ArrayList<Review>>() {});
                        int rid = -1;
                        if (ar != null) {
                            ar.removeAll(Collections.singleton(null));
                            rid = ar.size() + 1;
                        }
                        else rid = 1;
                        String now = (new Date()).toString();
                        Review r = new Review(
                                rid,
                                getIntent().getStringExtra("user_id"),
                                ReviewActivity.this.productId,
                                now, now,
                                ReviewActivity.this.description,
                                ReviewActivity.this.title,
                                ReviewActivity.this.userScore,
                                b64Imgs);
                        md.child(Integer.toString(rid)).setValue(r);
                        return Transaction.success(md);
                    }

                    @Override public void onComplete(@Nullable DatabaseError de, boolean b, @Nullable DataSnapshot ds) {
                        Log.d("ReviewActivity", "postTransaction:onComplete:" + de);
                        Toast.makeText(ReviewActivity.this, "Review successfully uploaded!", Toast.LENGTH_SHORT).show();
                        ReviewActivity.this.finish();
                    }
                });
                Toast.makeText(ReviewActivity.this, "Review uploading...", Toast.LENGTH_LONG).show();
            }
        });
        this.setStar(findViewById(R.id.fiveStarView));
    }

    @Override protected void onActivityResult(int reqCode, int resCode, Intent data) {
        super.onActivityResult(reqCode, resCode, data);
        if (reqCode == 1 && resCode == RESULT_OK && data != null) {
            // This is from addUserImageButton.
            if (data.getData() != null) {
                Toast.makeText(ReviewActivity.this, "A Image selected.", Toast.LENGTH_SHORT).show();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                Uri imagesPath = data.getData();
                Cursor cur = getContentResolver()
                        .query(imagesPath, filePathColumn, null, null, null);
                cur.moveToFirst();
                Bitmap b = null;
                try {
                    b = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                    int w = b.getWidth();
                    int h = b.getHeight();
                    double scale = (double)w / h;
                    if (w > 1500) {
                        if (w > h) {
                            w = 1500;
                            h = (int)(1500.0 / scale);
                        } else {
                            h = 1500;
                            w = (int)(1500.0 * scale);
                        }
                        b = Bitmap.createScaledBitmap(b, w, h, true);
                    }
                    else if (h > 1500) {
                        h = 1500;
                        w = (int)(1500.0 * scale);
                        b = Bitmap.createScaledBitmap(b, w, h, true);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ImageView iv = new ImageView(ReviewActivity.this);
                iv.setImageBitmap(b);
                iv.setLayoutParams(new LinearLayout.LayoutParams(800,800, 1f));
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        AlertDialog.Builder bu = new AlertDialog.Builder(ReviewActivity.this);
                        bu.setMessage("Delete photo?")
                                .setTitle("Delete")
                                .setNegativeButton(
                                        "No",
                                        new DialogInterface.OnClickListener() {
                                            @Override public void onClick(DialogInterface dialogInterface, int i) {}})
                                .setPositiveButton(
                                        "Yes",
                                        new DialogInterface.OnClickListener() {
                                            @Override public void onClick(DialogInterface dialogInterface, int i) {
                                                int idx = ReviewActivity.this.lnrImages.indexOfChild(v);
                                                ReviewActivity.this.b64Imgs.remove(idx);
                                                ReviewActivity.this.lnrImages.removeView(v);
                                            }});
                        AlertDialog dia = bu.create();
                        dia.show();
                    }
                });
                iv.setClickable(true);
                lnrImages.addView(iv);
                cur.close();
            } else if (data.getClipData() != null) {
                ClipData mcd = data.getClipData();
                Toast.makeText(ReviewActivity.this, mcd.getItemCount() + " Images selected.", Toast.LENGTH_SHORT).show();
                for (int i = 0; i < mcd.getItemCount(); i++) {
                    ClipData.Item it = mcd.getItemAt(i);
                    Bitmap b = null;
                    try {
                        b = MediaStore.Images.Media.getBitmap(this.getContentResolver(), it.getUri());
                        int w = b.getWidth();
                        int h = b.getHeight();
                        double scale = (double)w / h;
                        if (w > 1500) {
                            if (w > h) {
                                w = 1500;
                                h = (int)(1500.0 / scale);
                            } else {
                                h = 1500;
                                w = (int)(1500.0 * scale);
                            }
                            b = Bitmap.createScaledBitmap(b, w, h, true);
                        }
                        else if (h > 1500) {
                            h = 1500;
                            w = (int)(1500.0 * scale);
                            b = Bitmap.createScaledBitmap(b, w, h, true);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ImageView iv = new ImageView(ReviewActivity.this);
                    iv.setImageBitmap(b);
                    iv.setLayoutParams(new LinearLayout.LayoutParams(800,800, 1f));
                    iv.setOnClickListener(new View.OnClickListener() {
                        @Override public void onClick(View v) {
                            AlertDialog.Builder bu = new AlertDialog.Builder(ReviewActivity.this);
                            bu.setMessage("Delete photo?")
                                    .setTitle("Delete")
                                    .setNegativeButton(
                                            "No",
                                            new DialogInterface.OnClickListener() {
                                                @Override public void onClick(DialogInterface dialogInterface, int i) {}})
                                    .setPositiveButton(
                                            "Yes",
                                            new DialogInterface.OnClickListener() {
                                                @Override public void onClick(DialogInterface dialogInterface, int i) {
                                                    int idx = ReviewActivity.this.lnrImages.indexOfChild(v);
                                                    ReviewActivity.this.b64Imgs.remove(idx);
                                                    ReviewActivity.this.lnrImages.removeView(v);
                                                }});
                            AlertDialog dia = bu.create();
                            dia.show();
                        }
                    });
                    iv.setClickable(true);
                    lnrImages.addView(iv);
                }
            }
            else Toast.makeText(ReviewActivity.this, "No Images selected.", Toast.LENGTH_SHORT).show();
        }
    }
    public void setStar(View v) {
        switch (v.getId()) {
            case R.id.oneStarView:
                userScore = 1;
                ((ImageView)findViewById(R.id.oneStarView)).setImageResource(R.drawable.star_yellow);
                ((ImageView)findViewById(R.id.twoStarView)).setImageResource(R.drawable.star_white);
                ((ImageView)findViewById(R.id.threeStarView)).setImageResource(R.drawable.star_white);
                ((ImageView)findViewById(R.id.fourStarView)).setImageResource(R.drawable.star_white);
                ((ImageView)findViewById(R.id.fiveStarView)).setImageResource(R.drawable.star_white);
                break;
            case R.id.twoStarView:
                userScore = 2;
                ((ImageView)findViewById(R.id.oneStarView)).setImageResource(R.drawable.star_yellow);
                ((ImageView)findViewById(R.id.twoStarView)).setImageResource(R.drawable.star_yellow);
                ((ImageView)findViewById(R.id.threeStarView)).setImageResource(R.drawable.star_white);
                ((ImageView)findViewById(R.id.fourStarView)).setImageResource(R.drawable.star_white);
                ((ImageView)findViewById(R.id.fiveStarView)).setImageResource(R.drawable.star_white);
                break;
            case R.id.threeStarView:
                userScore = 3;
                ((ImageView)findViewById(R.id.oneStarView)).setImageResource(R.drawable.star_yellow);
                ((ImageView)findViewById(R.id.twoStarView)).setImageResource(R.drawable.star_yellow);
                ((ImageView)findViewById(R.id.threeStarView)).setImageResource(R.drawable.star_yellow);
                ((ImageView)findViewById(R.id.fourStarView)).setImageResource(R.drawable.star_white);
                ((ImageView)findViewById(R.id.fiveStarView)).setImageResource(R.drawable.star_white);
                break;
            case R.id.fourStarView:
                userScore = 4;
                ((ImageView)findViewById(R.id.oneStarView)).setImageResource(R.drawable.star_yellow);
                ((ImageView)findViewById(R.id.twoStarView)).setImageResource(R.drawable.star_yellow);
                ((ImageView)findViewById(R.id.threeStarView)).setImageResource(R.drawable.star_yellow);
                ((ImageView)findViewById(R.id.fourStarView)).setImageResource(R.drawable.star_yellow);
                ((ImageView)findViewById(R.id.fiveStarView)).setImageResource(R.drawable.star_white);
                break;
            case R.id.fiveStarView:
                userScore = 5;
                ((ImageView)findViewById(R.id.oneStarView)).setImageResource(R.drawable.star_yellow);
                ((ImageView)findViewById(R.id.twoStarView)).setImageResource(R.drawable.star_yellow);
                ((ImageView)findViewById(R.id.threeStarView)).setImageResource(R.drawable.star_yellow);
                ((ImageView)findViewById(R.id.fourStarView)).setImageResource(R.drawable.star_yellow);
                ((ImageView)findViewById(R.id.fiveStarView)).setImageResource(R.drawable.star_yellow);
                break;
            default:
                userScore = 0;
                ((ImageView)findViewById(R.id.oneStarView)).setImageResource(R.drawable.star_white);
                ((ImageView)findViewById(R.id.twoStarView)).setImageResource(R.drawable.star_white);
                ((ImageView)findViewById(R.id.threeStarView)).setImageResource(R.drawable.star_white);
                ((ImageView)findViewById(R.id.fourStarView)).setImageResource(R.drawable.star_white);
                ((ImageView)findViewById(R.id.fiveStarView)).setImageResource(R.drawable.star_white);
        }
    }
    private void refreshProductRelatedViews() {
        byte[] ib = Base64.decode(p.image, Base64.DEFAULT);
        ((ImageView)findViewById(R.id.productImageView)).setImageBitmap(
                BitmapFactory.decodeByteArray(ib, 0, ib.length)
        );
        ((TextView)findViewById(R.id.productTextView)).setText(p.name);
    }
}
