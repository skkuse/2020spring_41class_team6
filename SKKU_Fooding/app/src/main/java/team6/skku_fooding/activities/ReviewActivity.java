package team6.skku_fooding.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Layout;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import team6.skku_fooding.R;
import team6.skku_fooding.models.Ingredient;
import team6.skku_fooding.models.Product;
import team6.skku_fooding.models.Review;

public class ReviewActivity extends AppCompatActivity {
    public int userScore;
    public Product p;
    private LinearLayout lnrImages;
    private DatabaseReference dbRef;
    private int cnt;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        dbRef = FirebaseDatabase.getInstance().getReference("review").child("count");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot ds) {
                ReviewActivity.this.cnt = ((Long)ds.getValue()).intValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError de) {
                ReviewActivity.this.cnt = -1;
            }
        });

        if (false) {
            p = new Product(
                    Integer.parseInt(getIntent().getStringExtra("PRODUCT_ID"))
            );
        } else {
            p = new Product();
            p.productId = 100;
            p.companyName = "Dongwon";
            p.uploadedDate = (new Date()).toString();
            p.ingredients = new ArrayList<Ingredient>();
            p.overallScore = 3.72;
            p.productName = "Fishcake(Square, 12 pieces)";
            p.price = 12000;
            ((ImageView)findViewById(R.id.productImageView)).setImageResource(R.drawable.test_prod);
        }
        ((TextView)findViewById(R.id.productTextView)).setText("Write review about " + p.productName + ".");
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
                String title = ((TextView)findViewById(R.id.reviewTitleView)).getText().toString();
                String desc = ((TextView)findViewById(R.id.reviewDetailText)).getText().toString();
                ArrayList<String> b64Imgs = new ArrayList<>();
                for (int i = 0; i < lnrImages.getChildCount(); i++) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ((BitmapDrawable)((ImageView)lnrImages.getChildAt(i)).getDrawable()).getBitmap().compress(Bitmap.CompressFormat.WEBP, 100, baos);
                    b64Imgs.add(Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT));
                }
                dbRef.child("count");

                Review r = new Review(
                        ServerValue.TIMESTAMP.toString(),
                        ServerValue.TIMESTAMP.toString(),
                        desc,
                        title,
                        userScore,
                        b64Imgs);




            }
        });
    }
    @Override protected void onActivityResult(int reqCode, int resCode, Intent data) {
        super.onActivityResult(reqCode, resCode, data);

        if (reqCode == 1 && resCode == RESULT_OK && data != null) {
            // This is from addUserImageButton.
            if (data.getData() != null) {
                Toast.makeText(ReviewActivity.this, "A Image selected.", Toast.LENGTH_LONG).show();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                Uri imagesPath = data.getData();
                Cursor cur = getContentResolver()
                        .query(imagesPath, filePathColumn, null, null, null);
                cur.moveToFirst();
                BitmapFactory.Options op = new BitmapFactory.Options();
                op.inSampleSize = 2;
                Bitmap b = null;
                try {
                    b = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
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
                Toast.makeText(ReviewActivity.this, mcd.getItemCount() + " Images selected.", Toast.LENGTH_LONG).show();
                for (int i = 0; i < mcd.getItemCount(); i++) {
                    ClipData.Item it = mcd.getItemAt(i);
                    BitmapFactory.Options op = new BitmapFactory.Options();
                    op.inSampleSize = 2;
                    // Bitmap b = BitmapFactory.decodeFile(it.getUri().getPath(), op);
                    Bitmap b = null;
                    try {
                        b = MediaStore.Images.Media.getBitmap(this.getContentResolver(), it.getUri());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ImageView iv = new ImageView(ReviewActivity.this);
                    iv.setImageBitmap(b);
                    // iv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f));
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
            else Toast.makeText(ReviewActivity.this, "No Images selected.", Toast.LENGTH_LONG).show();
        }
    }
    public void setStar(View v) {
        switch (v.getId()) {
            case R.id.oneStarView:
                userScore = 1; break;
            case R.id.twoStarView:
                userScore = 2; break;
            case R.id.threeStarView:
                userScore = 3; break;
            case R.id.fourStarView:
                userScore = 4; break;
            case R.id.fiveStarView:
                userScore = 5; break;
            default:
                userScore = 0;
        }
    }
}
