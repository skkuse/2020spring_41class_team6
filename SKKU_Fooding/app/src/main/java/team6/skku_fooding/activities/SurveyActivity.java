package team6.skku_fooding.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import team6.skku_fooding.R;
import team6.skku_fooding.models.Survey;

public class SurveyActivity extends AppCompatActivity {
    public static final String USER_ID = "survey_william.USER_ID";
    Intent intent;
    Button next_button;
    RadioGroup radioGroup;
    RadioButton radioButton;
    int checked;
    int user_id=5858585;
    @Override
    public void onBackPressed(){

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);
        //주원씨한테서 user_id 받아오기
        //int temp = intent.getIntExtra(SignupActivity.name);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        radioGroup = findViewById(R.id.radio_group);
        Button button_next =findViewById(R.id.taste_next_btn);
        int width= dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width*0.9),(int)(height*0.85));

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                checked = radioGroup.indexOfChild(findViewById(checkedId));
            }
        });
        button_next.setOnClickListener((v) -> {
            int radioId = radioGroup.getCheckedRadioButtonId();
            radioButton=findViewById(radioId);
            if(checked==0){
               // user_id=temp; // 주원씨 한테서 잘 받아오면 해제
                startSurvey();
            }
            if(checked==1){
               // user_id=temp;
                startSurvey2();
            }
            if(checked==2){
              //  user_id=temp;
                startSurvey3();
            }
        });
    }
    public void checkButton(View v){
        int radioId = radioGroup.getCheckedRadioButtonId();
        radioButton=findViewById(radioId);
    }
    private void startSurvey(){
        Intent intent =new Intent(SurveyActivity.this,SurveyActivity_Spicy.class);
        intent.putExtra(USER_ID,user_id);
        startActivity(intent);
        finish();
    }

    private void startSurvey2(){
        Intent intent =new Intent(SurveyActivity.this,SurveyActivity_Sweet.class);
        intent.putExtra(USER_ID,user_id);
        startActivity(intent);
        finish();
    }
    private void startSurvey3(){
        Intent intent =new Intent(SurveyActivity.this,SurveyActivity_Salty.class);
        intent.putExtra(USER_ID,user_id);
        startActivity(intent);
        finish();
    }

}

