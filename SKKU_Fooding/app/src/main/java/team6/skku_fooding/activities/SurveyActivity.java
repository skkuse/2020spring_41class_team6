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

    Intent intent;
    Button next_button;
    RadioGroup radioGroup;
    RadioButton radioButton;
    int checked;
    @Override
    public void onBackPressed(){

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

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
                startSurvey();
            }
            if(checked==1){
                startSurvey2();
            }
            if(checked==2){
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
        startActivity(intent);
    }

    private void startSurvey2(){
        Intent intent =new Intent(SurveyActivity.this,SurveyActivity_Sweet.class);
        startActivity(intent);
    }
    private void startSurvey3(){
        Intent intent =new Intent(SurveyActivity.this,SurveyActivity_Salty.class);
        startActivity(intent);
    }

}
    /*

    설문조사가 완료되지 않았으면 뒤로가기 안되게,
    설문조사가 완료되었으면 뒤로가기 되게! (다시 login activity로 돌아올 수 있게)
    @Override
    public void onBackPressed() {

    }
    */

