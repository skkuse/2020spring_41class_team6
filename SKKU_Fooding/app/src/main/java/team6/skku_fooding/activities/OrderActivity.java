package team6.skku_fooding.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class OrderActivity extends AppCompatActivity {



    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    Calendar c1 = Calendar.getInstance();

    final String strToday = sdf.format(c1.getTime());

    EditText et;
    TextView tv;
    Button button;

    Integer count;
    String name;
    String uid;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        tv = (TextView) findViewById(R.id.textView);
        button = (Button) findViewById(R.id.button);
        et = (EditText) findViewById(R.id.editText1);





        /*
        Intent intent = getIntent();
        Integer count = intent.getIntExtra("count", 1);
        String name = intent.getStringExtra("name");
        String uid = intent.getStringExtra("uid"); //can be replaced shared preference

         */

        name = "A noodle";
        count = 3;
        uid = "IPli1mXAUUYm3npYJ48B43Pp7tQ2"; //임시 데이터셋

        tv.setText(name + " " + count + "개를 구입합니다.");

        /* 이미지
        DatabaseReference mdatabase = FirebaseDatabase.getInstance().getReference();

        mdatabase.child("product").orderByChild("name").equalTo(name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot d : dataSnapshot.getChildren()) {
                    String path = d.child("image").getValue(String.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

         */


    }

    public void onButtonClick(View v) {
        final DatabaseReference dbref = FirebaseDatabase.getInstance().getReference();
        final String address = et.getText().toString();

        if(address.length() == 0) {
            Toast erring1 = Toast.makeText(this.getApplicationContext(), "주소를 제대로 입력해주세요", Toast.LENGTH_SHORT);
            erring1.show();
        }
        else {

            dbref.child("order").orderByChild("order_id").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Integer id_count = 0;
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        id_count = d.child("order_id").getValue(Integer.class) + 1;
                    }
                    DatabaseReference dbref2 = FirebaseDatabase.getInstance().getReference();
                    dbref2.child("order").child("order_id" + id_count).child("counter").setValue(count);
                    dbref2.child("order").child("order_id" + id_count).child("order_id").setValue(id_count);
                    dbref2.child("order").child("order_id" + id_count).child("product_name").setValue(name);
                    dbref2.child("order").child("order_id" + id_count).child("date").setValue(strToday);
                    dbref2.child("order").child("order_id" + id_count).child("user_id").setValue(uid);
                    dbref2.child("order").child("order_id" + id_count).child("address").setValue(address);
                    dbref2.child("order").child("order_id" + id_count).child("status").setValue("Delivering");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }


}

