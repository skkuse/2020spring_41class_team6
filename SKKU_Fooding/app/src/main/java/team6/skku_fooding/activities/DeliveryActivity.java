package team6.skku_fooding.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import team6.skku_fooding.R;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import team6.skku_fooding.models.Survey;


public class DeliveryActivity extends AppCompatActivity {
    String temp,temp2,temp3,temp4;
    private static final String TAG = "DeliveryActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);
        ListView mlistview = (ListView) findViewById(R.id.list_view_delivery);
        Log.d(TAG,"onCreate : Started.");
        ArrayList<custom_list_item_delivery> item_list_delivery = new ArrayList<>();
        //uid intent로 받아와야됨
        //Intent intent = getIntent();
        //String temp_Uid = intent.getStringExtra(//여기받아온acvitivity.FILTER_STRING);
        DatabaseReference mdatabase = FirebaseDatabase.getInstance().getReference();
        mdatabase.child("order").orderByChild("user_id").equalTo("IPli1mXAUUYm3npYJ48B43Pp7tQ2").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               for(DataSnapshot shot : dataSnapshot.getChildren()) {
                   Integer count = shot.child("counter").getValue(Integer.class);
                   String date = shot.child("order_date").getValue(String.class);
                   String status = shot.child("status").getValue(String.class);
                   Integer order_id = shot.child("order_id").getValue(Integer.class);
                   String name = shot.child("product_name").getValue(String.class);
                   Log.d("list", "" + count + date + status + order_id + name);
                   temp=Integer.toString(order_id);
                   temp2=date;
                   temp3=status;
                   temp4=name;
                   Log.d("asdfasdfasdfasdfsadf", "" + temp + temp2 + temp3 + temp4);
                   custom_list_item_delivery order1 = new custom_list_item_delivery(temp,temp2,temp3,temp4);
                   item_list_delivery.add(order1);
                   CustomListAdapter_delivery adapter= new CustomListAdapter_delivery(DeliveryActivity.this, R.layout.adapter_view_delivery,item_list_delivery,DeliveryActivity.this);
                   mlistview.setAdapter(adapter);
               }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

        Log.d("ASDFASDFASDFASDF", "" + temp + temp2 + temp3 + temp4);



    }
}