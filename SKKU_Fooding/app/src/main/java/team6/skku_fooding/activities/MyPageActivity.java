package team6.skku_fooding.activities;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import team6.skku_fooding.R;

@SuppressLint("SetTextI18n")
public class MyPageActivity extends AppCompatActivity {
    private String uid;
    private ImageView imView;
    private TextView userIDTextView;
    private TextView userNicknameTextView;
    // private LinearLayout mpLinear;
    private Button reopenSurveyButton;
    // private Button userPrefButton;
    private FloatingActionButton logoutButton;

    private FirebaseDatabase db;
    private DatabaseReference userRef;
    private SharedPreferences loginPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);

        this.imView = findViewById(R.id.mpImageView);
        this.userIDTextView = findViewById(R.id.mpUserIdTextView);
        this.userNicknameTextView = findViewById(R.id.mpUserNicknameTextView);
        // this.mpLinear = findViewById(R.id.mpLinearLayout);
        this.reopenSurveyButton = findViewById(R.id.mpReopenSurveyButton);
        // this.userPrefButton = findViewById(R.id.mpUserPrefButton);
        this.logoutButton = findViewById(R.id.mpLogoutButton);

        this.loginPref = this.getSharedPreferences("user_SP", this.MODE_PRIVATE);

        this.uid = this.loginPref.getString("UID", "IPli1mXAUUYm3npYJ48B43Pp7tQ2");
        this.db = FirebaseDatabase.getInstance();
        this.userRef = db.getReference("user").child(this.uid);

        if (this.loginPref.getString("user_id", null) == null) {
            userRef.child("user_id").addValueEventListener(new ValueEventListener() {
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
        } else this.userIDTextView.setText(this.loginPref.getString("user_id", null));
        if (this.loginPref.getString("nickname", null) == null) {
            userRef.child("nickname").addValueEventListener(new ValueEventListener() {
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
        } else this.userNicknameTextView.setText(this.loginPref.getString("nickname", null));


        this.reopenSurveyButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Intent in = new Intent(MyPageActivity.this, SurveyActivity.class);
                in.putExtra(SignupActivity.UID_pass, MyPageActivity.this.uid);
                startActivity(in);
            }
        });
        /*
        this.userPrefButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Intent in = new Intent(MyPageActivity.this, UserPrefActivity.class);
                startActivity(in);
            }
        });
        */
    }
}
