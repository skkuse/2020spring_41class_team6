package team6.skku_fooding.activities;


import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import team6.skku_fooding.R;

public class SurveyActivity_Allergen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey__allergen);
        Button buttonfinishSurvey = findViewById(R.id.allergen_next_btn);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width= dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*0.9),(int)(height*0.85));
        buttonfinishSurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnBtn = new Intent(SurveyActivity_Allergen.this,SignupActivity.class);
                returnBtn.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(returnBtn);
            }
        });
    }
    private void startSurvey(){
        finish();
    }
}