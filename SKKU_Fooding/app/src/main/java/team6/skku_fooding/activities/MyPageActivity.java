package team6.skku_fooding.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import team6.skku_fooding.R;

@SuppressLint("SetTextI18n")
public class MyPageActivity extends AppCompatActivity {
    private String uid;
    private TextView userIDTextView;
    private TextView userNicknameTextView;
    Button mpLogoutButton;
    private FirebaseAuth mAuth;
    SharedPreferences loginPref;
    // private Button userPrefButton;
    BottomNavigationView bottomNavigationView;

    @Override
    public void onBackPressed(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);

        this.userIDTextView = findViewById(R.id.mpUserIdTextView);
        this.userNicknameTextView = findViewById(R.id.mpUserNicknameTextView);
        mpLogoutButton = findViewById(R.id.mpLogoutButton);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navi);
        bottomNavigationView.setSelectedItemId(R.id.mypage_menu);
        // this.mpLinear = findViewById(R.id.mpLinearLayout);
        // private LinearLayout mpLinear;
        // this.userPrefButton = findViewById(R.id.mpUserPrefButton);
        mAuth = FirebaseAuth.getInstance();
        loginPref = getSharedPreferences("user_SP", MODE_PRIVATE);

        this.uid = loginPref.getString("UID", null);
        FirebaseDatabase db = FirebaseDatabase.getInstance();

        if (loginPref.getString("user_id", null) == null) {
            db.getReference("user")
                    .child(this.uid)
                    .child("user_id")
                    .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot ds) {
                    Log.d("MyPageActivity", "DB read for id");
                    String s = ds.getValue(String.class);
                    if (s == null) s = "User ID Here (Wrong UID)";
                    MyPageActivity.this.userIDTextView.setText(s);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError de) {
                    Toast.makeText(MyPageActivity.this, "Database error occured.", Toast.LENGTH_LONG).show();
                    MyPageActivity.this.finish();
                }
            });
        } else this.userIDTextView.setText(loginPref.getString("user_id", null));
        if (loginPref.getString("nickname", null) == null) {
            db.getReference("user")
                    .child(this.uid)
                    .child("nickname")
                    .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot ds) {
                    Log.d("MyPageActivity", "DB read for nickname");
                    String s = ds.getValue(String.class);
                    if (s == null) s = "User Nickname Here (Wrong UID)";
                    MyPageActivity.this.userNicknameTextView.setText(s);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError de) {
                    Toast.makeText(MyPageActivity.this, "Database error occured.", Toast.LENGTH_LONG).show();
                    MyPageActivity.this.finish();
                }
            });
        } else this.userNicknameTextView.setText(loginPref.getString("nickname", null));


        findViewById(R.id.mpReopenSurveyButton).setOnClickListener(v ->
                startActivity(new Intent(MyPageActivity.this, SurveyActivity.class)
                        .putExtra("UID", MyPageActivity.this.uid)));
        /*
        this.userPrefButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Intent in = new Intent(MyPageActivity.this, UserPrefActivity.class);
                startActivity(in);
            }
        });
        */
        mpLogoutButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(MyPageActivity.this, LoginActivity.class);
                startActivity(intent);
                SharedPreferences.Editor editor = loginPref.edit();
                editor.clear();
                editor.commit();
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Intent intent;
                        switch(item.getItemId()) {
                            case R.id.home_menu:
                                Log.d("LOG:", "SELECT:HOME");
                                intent = new Intent(MyPageActivity.this, SearchActivity.class);
                                startActivity(intent);
                                return true;
                            case R.id.recommendation_menu:
                                intent = new Intent(MyPageActivity.this, RecommendationActivity.class);
                                startActivity(intent);
                                return true;
                            case R.id.delivery_menu:
                                intent = new Intent(MyPageActivity.this, DeliveryActivity.class);
                                startActivity(intent);
                                return true;
                            case R.id.mypage_menu:
                                Log.d("LOG:", "SELECT:MYPAGE");
                                return false;
                        }
                        return false;
                    }
                }
        );

        bottomNavigationView.setOnNavigationItemReselectedListener(
                new BottomNavigationView.OnNavigationItemReselectedListener() {
                    @Override
                    public void onNavigationItemReselected(@NonNull MenuItem item) {
                        Intent intent;
                        switch(item.getItemId()) {
                            case R.id.home_menu:
                                Log.d("LOG:", "RESELECT:HOME");
                                intent = new Intent(MyPageActivity.this, SearchActivity.class);
                                startActivity(intent);
                            case R.id.recommendation_menu:
                                intent = new Intent(MyPageActivity.this, RecommendationActivity.class);
                                startActivity(intent);
                            case R.id.delivery_menu:
                                intent = new Intent(MyPageActivity.this, DeliveryActivity.class);
                                startActivity(intent);
                            case R.id.mypage_menu:
                                Log.d("LOG:", "RESELECT:MYPAGE");
                        }
                    }
                }
        );
    }

}
