package team6.skku_fooding.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import team6.skku_fooding.R;
import team6.skku_fooding.models.User;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    String user_id;
    String pw;
    String nickname;
    String UID;
    EditText edit_signup_id;
    EditText edit_signup_pw;
    EditText edit_pw_check;
    EditText edit_signup_name;
    Button check_button;
    Button send_button;
    Button verify_button;
    Button survey_button;
    Button signup_button;
    TextView dup_tv;
    TextView verify_tv;
    boolean valid_id;
    boolean duplicate_checked = false;
    boolean is_duplicate;
    boolean is_sent = false;
    boolean is_verified=false;
    boolean form_complete = false;
    boolean survey_complete = false;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference dbReference;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        edit_signup_id = (EditText) findViewById(R.id.edit_signup_id);
        edit_signup_pw = (EditText) findViewById(R.id.edit_signup_pw);
        edit_pw_check = (EditText) findViewById(R.id.edit_pw_check);
        edit_signup_name = (EditText) findViewById(R.id.edit_signup_name);
        check_button = (Button) findViewById(R.id.check_button);
        send_button = (Button) findViewById(R.id.send_button);
        verify_button = (Button) findViewById(R.id.verify_button);
        survey_button = (Button) findViewById(R.id.survey_button);
        signup_button = (Button) findViewById(R.id.signup_enter);
        dup_tv = (TextView) findViewById(R.id.dup_tv);
        verify_tv = (TextView) findViewById(R.id.verify_tv);
        mAuth = FirebaseAuth.getInstance();
        dbReference = FirebaseDatabase.getInstance().getReference();

        // Check ID
        check_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_id = edit_signup_id.getText().toString();
                duplicate_checked = true;

                // Check if ID is email
                String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
                if (!user_id.matches(regex)) {
                    valid_id = false;
                    duplicate_checked = false;
                    edit_signup_id.getText().clear();
                    dup_tv.setText("ID is not an email address.");
                    dup_tv.setTextColor(Color.RED);
                    return;
                } else
                    valid_id = true;

                // Check duplicate ID
                mAuth.fetchSignInMethodsForEmail(user_id).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        if (task.getResult().getSignInMethods().isEmpty()) {
                            is_duplicate = false;
                            dup_tv.setText("This ID can be used.");
                            dup_tv.setTextColor(Color.BLUE);
                            edit_signup_id.setEnabled(false);
                        } else {
                            duplicate_checked = false;
                            is_duplicate = true;
                            dup_tv.setText("This ID is already in use.");
                            dup_tv.setTextColor(Color.RED);
                        }
                    }
                });
            }
        });

        // Click send email button
        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_id = edit_signup_id.getText().toString();
                pw = edit_signup_pw.getText().toString();
                String pw_check = edit_pw_check.getText().toString();
                nickname = edit_signup_name.getText().toString();

                // filled signup form
                if(user_id.equals("")){
                    Toast.makeText(SignupActivity.this, "Please enter your ID.", Toast.LENGTH_SHORT).show();
                    return;
                }

                else if(pw.equals(""))
                {
                    Toast.makeText(SignupActivity.this, "Please enter your password.", Toast.LENGTH_SHORT).show();
                    return;
                }

                else if(pw_check.equals(""))
                {
                    Toast.makeText(SignupActivity.this, "Please enter your confirm password.", Toast.LENGTH_SHORT).show();
                    return;
                }

                else if (nickname.equals("")) {
                    Toast.makeText(SignupActivity.this, "Please enter your nickname.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(is_verified==true){
                    Toast.makeText(SignupActivity.this, "This email is already verified.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (valid_id == false) {
                    Toast.makeText(SignupActivity.this, "This ID is not valid.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (is_duplicate == true) {
                    Toast.makeText(SignupActivity.this, "This ID is already in use.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (duplicate_checked == false) {
                    //dup_tv.setText("Please complete duplicate ID check.");
                    //.setTextColor(Color.BLACK);
                    Toast.makeText(SignupActivity.this, "Duplicate ID check is incomplete.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (pw.length() < 6 || pw.length() >= 14) {
                    edit_signup_pw.getText().clear();
                    edit_pw_check.getText().clear();
                    Toast.makeText(SignupActivity.this, "Password is not valid.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!pw.equals(pw_check)) {
                    edit_pw_check.getText().clear();
                    Toast.makeText(SignupActivity.this, "Confirm password is different from your password.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!user_id.equals("") && !pw.equals("")) {
                    is_sent = true;
                    mAuth.createUserWithEmailAndPassword(user_id, pw)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        UID = mAuth.getUid();
                                        user = mAuth.getCurrentUser();
                                        if(!user.isEmailVerified()) {
                                            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    verify_tv.setText("Verification email is sent.");
                                                }
                                            });
                                        }
                                    }
                                }
                            });
                }
            }

        });

        // Click verify email button
        verify_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_id = edit_signup_id.getText().toString();

                if(user_id.equals(""))
                {
                    Toast.makeText(SignupActivity.this, "Please enter your ID.", Toast.LENGTH_SHORT).show();
                    return;
                }

                else if(!duplicate_checked)
                {
                    Toast.makeText(SignupActivity.this, "Duplicate ID check is incomplete.", Toast.LENGTH_SHORT).show();
                    return;
                }

                else if(!is_sent)
                {
                    Toast.makeText(SignupActivity.this, "Please click send email button.", Toast.LENGTH_SHORT).show();
                    return;
                }

                else if(is_verified)
                {
                    Toast.makeText(SignupActivity.this, "This email is already verified.", Toast.LENGTH_SHORT).show();
                    return;
                }

                verify_tv.setText("Please wait a second...");
                mAuth.getCurrentUser().reload();
                Handler delayHandler = new Handler();
                delayHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(user.isEmailVerified()) {
                            verify_tv.setText("Verification (Complete).");
                            verify_tv.setTextColor(Color.BLUE);
                            is_verified=true;
                            edit_signup_id.setEnabled(false);
                            edit_signup_pw.setEnabled(false);
                            edit_pw_check.setEnabled(false);
                            edit_signup_name.setEnabled(false);
                            postFirebaseDatabase(true);
                            form_complete = true;
                        }
                        else{
                            verify_tv.setText("Verification (Incomplete).");
                            verify_tv.setTextColor(Color.RED);
                        }
                    }
                }, 3000);

            }
        });

        // Click survey button
        survey_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!form_complete)
                {
                    Toast.makeText(SignupActivity.this, "Signup form is not completely filled.", Toast.LENGTH_SHORT).show();
                    return;
                }
                survey_complete = true;
                intent = new Intent(SignupActivity.this, SurveyActivity.class);
                intent.putExtra("UID", UID);
                startActivity(intent);
            }
        });

        // Click sign up button
        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_id = edit_signup_id.getText().toString();
                pw = edit_signup_pw.getText().toString();
                String pw_check = edit_pw_check.getText().toString();
                nickname = edit_signup_name.getText().toString();

                if(!form_complete)
                    Toast.makeText(SignupActivity.this, "Signup form is not completely filled.", Toast.LENGTH_SHORT).show();
                else if (!survey_complete)
                    Toast.makeText(SignupActivity.this, "Preference survey is incomplete.", Toast.LENGTH_SHORT).show();

                else
                {
                    Toast.makeText(SignupActivity.this, "Signed up successfully.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                    startActivity(intent);
                    clearET();
                }
            }

        });
    }

    public void postFirebaseDatabase(boolean add) {
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;
        if (add) {
            User new_user = new User(user_id, pw, nickname, UID);
            postValues = new_user.toMap();
        }
        childUpdates.put("/user/" + UID, postValues);
        dbReference.updateChildren(childUpdates);
    }

    public void clearET() {
        edit_signup_id.setText("");
        edit_signup_pw.setText("");
        edit_pw_check.setText("");
        edit_signup_name.setText("");
        user_id = "";
        pw="";
        nickname = "";
        UID="";
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(SignupActivity.this, "Cancel Sign up", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
