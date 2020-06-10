package team6.skku_fooding.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import team6.skku_fooding.R;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


public class DeliveryActivity extends AppCompatActivity implements CustomListAdapter_delivery.ListBtnClickListener {
    String temp,temp2,temp3,temp4,temp5;
    List<String> list = new ArrayList<>();
    private static final String TAG = "DeliveryActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);
        ListView mlistview = (ListView) findViewById(R.id.list_view_delivery);
        Log.d(TAG,"onCreate : Started.");
        ArrayList<custom_list_item_delivery> item_list_delivery = new ArrayList<>();

        SharedPreferences loginPref;
        loginPref = getSharedPreferences("user_SP", this.MODE_PRIVATE);
        String UID=loginPref.getString("UID",null);

        DatabaseReference mdatabase = FirebaseDatabase.getInstance().getReference();
        mdatabase.child("order").orderByChild("user_id").equalTo(UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               for(DataSnapshot shot : dataSnapshot.getChildren()) {
                   Integer product_id = shot.child("product_id").getValue(Integer.class);
                   String date = shot.child("date").getValue(String.class);
                   String status = shot.child("status").getValue(String.class);
                   Integer order_id = shot.child("order_id").getValue(Integer.class);
                   String name = shot.child("product_name").getValue(String.class);
                   Log.d("list", ""  + date + status + order_id + name+ "  " + product_id);

                   temp=Integer.toString(order_id);
                   temp2=date;
                   temp3=status;
                   temp4=name;
                   temp5=Integer.toString(product_id);
                   Log.d("asdfasdfasdfasdfsadf", "" + temp + temp2 + temp3 + temp4 +temp5);
                   custom_list_item_delivery order1 = new custom_list_item_delivery(temp,temp2,temp3,temp4,temp5);
                   item_list_delivery.add(order1);
                   list.add(temp5);
                   CustomListAdapter_delivery adapter= new CustomListAdapter_delivery(DeliveryActivity.this, R.layout.adapter_view_delivery,item_list_delivery, DeliveryActivity.this,DeliveryActivity.this);
                   mlistview.setAdapter(adapter);
               }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });



        Log.d("ASDFASDFASDF", "" + temp + temp2 + temp3 + temp4);



    }
    @Override
    public void onListBtnClick(int position){
        String current_product_id = list.get(position).toString();
        int product_id =Integer.parseInt(current_product_id);
        Log.d("GET TEXT", "" + product_id);
        Intent intent =new Intent(DeliveryActivity.this,ReviewActivity.class);
        intent.putExtra("product_id",product_id);
        startActivity(intent);
    }
}