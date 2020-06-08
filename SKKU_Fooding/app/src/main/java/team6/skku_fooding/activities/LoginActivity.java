package team6.skku_fooding.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import team6.skku_fooding.R;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    String UID;
    String user_id;
    String nickname;
    EditText edit_id;
    EditText edit_pw;
    Button signup_button;
    Button login_button;
    SharedPreferences loginPref;
    String shakey;
    private FirebaseAuth mAuth;
    private DatabaseReference dbReference;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edit_id = (EditText) findViewById(R.id.edit_id);
        edit_pw = (EditText) findViewById(R.id.edit_pw);
        login_button = (Button) findViewById(R.id.login_button);
        signup_button = (Button) findViewById(R.id.signup_button);
        mAuth = FirebaseAuth.getInstance();
        loginPref = this.getSharedPreferences("user_SP", this.MODE_PRIVATE);
        dbReference = FirebaseDatabase.getInstance().getReference().child("user");

        // Login SharedPreferences
        final SharedPreferences.Editor editor = loginPref.edit();
        String defaultValue = loginPref.getString("login", null);
        if (defaultValue != null) {
            Toast.makeText(LoginActivity.this, "Start Auto-Login", Toast.LENGTH_SHORT).show();
            intent = new Intent(LoginActivity.this, SearchActivity.class);
            /* 앞으로의 activity에 필요한 intent 넘겨주기*/

            // get user's user_id & nickname from firebase database
            dbReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d("LOG:user ID 11", ":"+user_id);
                    Map<String, Object> user = (Map<String, Object>) dataSnapshot.getValue();
                    for(String childKey: user.keySet()){
                        if(childKey.equals(mAuth.getUid()))
                        {
                            Map<String, Object> currentObject = (Map<String, Object>) user.get(childKey);
                            user_id=currentObject.get("user_id").toString();
                            nickname=currentObject.get("nickname").toString();
                            intent.putExtra("user_id", user_id);
                            intent.putExtra("nickname", nickname);
                            Log.d("LOG:user ID 22", ":"+user_id);
                            startActivity(intent);
                            finish();
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        //signup button
        signup_button.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                edit_id.getText().clear();
                edit_pw.getText().clear();
                intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        //login button
        login_button.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                final String id = edit_id.getText().toString();
                final String pw = edit_pw.getText().toString();
                shakey = id + " " + pw;
                if (!id.equals("") && !pw.equals("")) {
                    mAuth.signInWithEmailAndPassword(id, pw)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Save login information in shared preferences
                                        editor.putString("login", shakey);
                                        editor.commit();
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        Toast.makeText(LoginActivity.this, "Logged in successfully.", Toast.LENGTH_SHORT).show();
                                        intent = new Intent(LoginActivity.this, SearchActivity.class);
                                        /* 앞으로의 activity에 필요한 intent 넘겨주기*/
                                        // get user's user_id & nickname from firebase database
                                        dbReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                Map<String, Object> user = (Map<String, Object>) dataSnapshot.getValue();
                                                for(String childKey: user.keySet()){
                                                    Log.d("LOG: childkey", ":"+childKey);
                                                    Log.d("LOG: mAuth.getUid", ":"+mAuth.getUid());
                                                    if(childKey.equals(mAuth.getUid()))
                                                    {
                                                        Map<String, Object> currentObject = (Map<String, Object>) user.get(childKey);
                                                        user_id=currentObject.get("user_id").toString();
                                                        nickname=currentObject.get("nickname").toString();
                                                        intent.putExtra("user_id", user_id);
                                                        intent.putExtra("nickname", nickname);
                                                        Log.d("LOG:user ID 22", ":"+user_id);
                                                        startActivity(intent);
                                                        finish();
                                                        break;
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                    else {
                                        Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                                        edit_pw.getText().clear();
                                    }
                                }
                            });
                } else
                    Toast.makeText(LoginActivity.this, "Wrong input!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
    }
}
