package team6.skku_fooding.activities;


import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import team6.skku_fooding.R;

public class SurveyActivity_Sweet extends AppCompatActivity {
    public static final String ID_NUMBER = "categoryid_william.ID_NUMBER";
    RadioGroup radioGroup;
    RadioButton radioButton;
    int checked;
    int id;
    String UID;
    @Override
    public void onBackPressed(){

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey__sweet);
        radioGroup = findViewById(R.id.sweet_group);


        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width= dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*0.9),(int)(height*0.85));
        Intent intent = getIntent();
        String temp = intent.getStringExtra("UID");
        Button buttonStartSurvey = findViewById(R.id.sweet_next_btn);
        saveID();
        buttonStartSurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UID=temp;
                startSurvey();
            }
        });
    }

    private void saveID(){
        radioGroup = findViewById(R.id.sweet_group);
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

        Intent intent =new Intent(SurveyActivity_Sweet.this,SurveyActivity_Ingredient.class);
        if(checked==0){
            id=40;
        }
        else if(checked==1){
            id=50;
        }
        else if(checked==2){
            id=60;
        }
        intent.putExtra(ID_NUMBER,id);
        intent.putExtra("UID",UID);
        startActivity(intent);
        finish();
    }
}