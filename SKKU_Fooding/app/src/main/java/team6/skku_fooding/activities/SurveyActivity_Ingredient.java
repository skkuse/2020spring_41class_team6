package team6.skku_fooding.activities;


import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import team6.skku_fooding.models.Survey;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import team6.skku_fooding.R;

public class SurveyActivity_Ingredient extends AppCompatActivity {
    public static final String ID_NUMBER2 = "categoryid_william.ID_NUMBER";
    private DatabaseReference reff_survey;
    RadioGroup radioGroup;
    RadioButton radioButton;
    int checked;
    int idfinal;
    String UID;
    @Override
    public void onBackPressed(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey__ingredient);
        radioGroup = findViewById(R.id.ingredient_group);

        reff_survey= FirebaseDatabase.getInstance().getReference();

        Intent intent = getIntent();
        String temp = intent.getStringExtra("UID");
        int id = intent.getIntExtra(SurveyActivity_Spicy.ID_NUMBER,0);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width= dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*0.9),(int)(height*0.85));
        Button buttonStartSurvey = findViewById(R.id.ingredient_next_btn);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                checked = radioGroup.indexOfChild(findViewById(checkedId));
            }
        });
        saveID();
        buttonStartSurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checked==0){
                    idfinal = id +1;
                }
                else if(checked==1){
                    idfinal = id +2;
                }
                else if(checked==2){
                    idfinal = id +3;
                }
                else if(checked==3){
                    idfinal = id +4;
                }
                else if(checked==4){
                    idfinal = id +5;
                }
                //이시점에서 category_id==idfinal 생성 완료
                UID=temp;
                //주원씨 signactivity에서 uid 잘 받오면 "IPli1mXAUUYm3npYJ48B43Pp7tQ2" 대신 user_id
                Log.d("UID 잘받았나 보자",""+UID);
                reff_survey.child("user").child(UID).child("category_id").setValue(idfinal);

                startSurvey();
            }
        });
    }
    private void saveID(){

        radioGroup = findViewById(R.id.ingredient_group);
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
        Intent intent =new Intent(SurveyActivity_Ingredient.this,SurveyActivity_Vegetarian.class);
        intent.putExtra(ID_NUMBER2,idfinal);
        intent.putExtra("UID",UID);
        startActivity(intent);
        finish();
    }
}