package team6.skku_fooding.activities;


import team6.skku_fooding.R;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
public class SurveyActivity_Spicy extends AppCompatActivity {
    public static final String ID_NUMBER = "categoryid_william.ID_NUMBER";
    public static final String USER_ID = "survey_william.USER_ID";
    RadioGroup radioGroup;
    RadioButton radioButton;
    int checked;
    int id;
    int user_id;
    @Override
    public void onBackPressed(){

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey__spicy);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Intent intent = getIntent();
        int temp = intent.getIntExtra(SurveyActivity.USER_ID,0);
        int width= dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*0.9),(int)(height*0.85));
        Button buttonStartSurvey = findViewById(R.id.spicy_next_btn);
        saveID();
        buttonStartSurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_id=temp;
                startSurvey();
            }
        });
    }

    private void saveID(){
        radioGroup = findViewById(R.id.spicy_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                checked = radioGroup.indexOfChild(findViewById(checkedId));
            }
        });
        int radioId = radioGroup.getCheckedRadioButtonId();
        radioButton=findViewById(radioId);

    }
    private void startSurvey(){
        Intent intent =new Intent(SurveyActivity_Spicy.this,SurveyActivity_Ingredient.class);
        if(checked==0){
            id=10;
        }
        else if(checked==1){
            id=20;
        }
        else if(checked==2){
            id=30;
        }
        intent.putExtra(ID_NUMBER,id);
        intent.putExtra(USER_ID,user_id);
        startActivity(intent);
        finish();
    }
}