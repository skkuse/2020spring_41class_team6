package team6.skku_fooding.activities;


import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import team6.skku_fooding.R;

public class SurveyActivity_Vegetarian extends AppCompatActivity {
    @Override
    public void onBackPressed(){

    }
    String filtering;
    public static final String FILTER_STRING = "filtering_william.FILTER_STRING";
    public static final String USER_ID = "survey_william.USER_ID";
    RadioGroup radioGroup;
    RadioButton radioButton;
    int checked;
    int user_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey__vegetarian);
        Intent intent = getIntent();
        int temp = intent.getIntExtra(SurveyActivity.USER_ID,0);
        //테스트용
        //int id = intent.getIntExtra(SurveyActivity_Ingredient.ID_NUMBER2,0);
       // TextView textView = (TextView) findViewById(R.id.test_survey);
        //textView.setText("" + id);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width= dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*0.9),(int)(height*0.85));
        Button buttonStartSurvey = findViewById(R.id.vegan_next_btn);
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
        radioGroup = findViewById(R.id.vegan_group);
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
        Intent intent =new Intent(SurveyActivity_Vegetarian.this,SurveyActivity_Allergen.class);
        if(checked==0){
            filtering=" ";
        }
        else if(checked==1){
            filtering="pork,beef,egg,milk,fish,poultry,shellfish";
        }
        else if(checked==2){
            filtering="pork,beef,milk,fish,poultry,shellfish";
        }
        else if(checked==3){
            filtering="pork,beef,egg,fish,poultry,shellfish";
        }
        else if(checked==4){
            filtering="pork,beef,fish,poultry,shellfish";
        }
        else if(checked==5){
            filtering="pork,beef,poultry,shellfish";
        }
        else if(checked==5){
            filtering="pork,beef,shellfish";
        }
        intent.putExtra(FILTER_STRING,filtering);
        intent.putExtra(USER_ID,user_id);
        startActivity(intent);
        finish();
    }
}