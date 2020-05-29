package team6.skku_fooding.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import team6.skku_fooding.R;

public class SurveyActivity extends AppCompatActivity {

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

    }

    /*

    설문조사가 완료되지 않았으면 뒤로가기 안되게,
    설문조사가 완료되었으면 뒤로가기 되게! (다시 login activity로 돌아올 수 있게)
    @Override
    public void onBackPressed() {

    }
    */
}
