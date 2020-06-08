package team6.skku_fooding.activities;


import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import team6.skku_fooding.R;

public class SurveyActivity_Allergen extends AppCompatActivity {
    public static final String ID_NUMBER3 = "filter_william.ID_NUMBER";
    String filtering_final;
    private CheckBox milk;
    private CheckBox egg;
    private CheckBox flour;
    private CheckBox nuts;
    private CheckBox shellfish;
    private CheckBox fish;
    int milk_check=0;
    int egg_check=0;
    int flour_check=0;
    int nuts_check=0;
    int shellfish_check=0;
    int fish_check=0;
    private DatabaseReference reff_survey;
    @Override
    public void onBackPressed(){

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reff_survey= FirebaseDatabase.getInstance().getReference();
        setContentView(R.layout.activity_survey__allergen);
        milk = (CheckBox) findViewById(R.id.allergen_milk);
        egg = (CheckBox)findViewById(R.id.allergen_egg);
        flour = (CheckBox)findViewById(R.id.allergen_flour);
        nuts = (CheckBox)findViewById(R.id.allergen_nuts);
        shellfish=(CheckBox)findViewById(R.id.allergen_shellfish);
        fish = (CheckBox)findViewById(R.id.allergen_fish);
        Button buttonfinishSurvey = findViewById(R.id.allergen_next_btn);
        Intent intent = getIntent();
        String filtering = intent.getStringExtra(SurveyActivity_Vegetarian.FILTER_STRING);
        String temp = intent.getStringExtra(SurveyActivity.USER_ID);
        filtering_final=filtering;
        //병서씨한테서 recommendationactivitiy string 받아오기
        //String get_from_recommendation = intent.getStringExtra(RecommandationActivity.name);
        //TextView textView = (TextView) findViewById(R.id.test_survey2);
        //textView.setText(" "+temp);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width= dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*0.9),(int)(height*0.85));

        milk.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    milk_check=1;
                }
                else{
                    milk_check=0;
                }
            }
        });
        egg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    egg_check=1;
                }
                else{
                    egg_check=0;
                }
            }
        });
        flour.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    flour_check=1;
                }
                else{
                    flour_check=0;
                }
            }
        });
        nuts.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    nuts_check=1;
                }
                else{
                    nuts_check=0;
                }
            }
        });
        shellfish.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    shellfish_check=1;
                }
                else{
                    shellfish_check=0;
                }
            }
        });
        fish.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    fish_check=1;
                }
                else{
                    fish_check=0;
                }
            }
        });
        buttonfinishSurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(milk_check==1){
                    filtering_final=filtering_final+","+"milk";
                }
                if(egg_check==1){
                    filtering_final=filtering_final+","+"egg";
                }
                if(flour_check==1){
                    filtering_final=filtering_final+","+"flour";
                }
                if(shellfish_check==1){
                    filtering_final=filtering_final+","+"shellfish";
                }
                if(fish_check==1){
                    filtering_final=filtering_final+","+"fish";
                }
                //이시점에서 filtering_final == filtering_list 생성 완료,
                Log.d("sakfjlaskf",""+filtering_final);
                //주원씨 signactivity에서 uid 잘 받오면 "IPli1mXAUUYm3npYJ48B43Pp7tQ2" 대신 temp
                reff_survey.child("user").child("IPli1mXAUUYm3npYJ48B43Pp7tQ2").child("filter").setValue(filtering_final);
                startSurvey();
            }
        });
    }
    private void startSurvey(){
        //recommendation에서 갱신할 경우, intent 받아오고 그게 'r'일때
        //if(get_from_recommendation=='r'){
       //     Intent intent =new Intent(SurveyActivity_Allergen.this,RecommendationActivity.class);
       //     startActivity(intent);
       // }

      // 테스트용 Intent intent =new Intent(SurveyActivity_Allergen.this,DeliveryActivity.class);
        //startActivity(intent);
        finish();
    }
}