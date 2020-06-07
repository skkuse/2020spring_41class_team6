package team6.skku_fooding.activities;

import androidx.appcompat.app.AppCompatActivity;
import team6.skku_fooding.R;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class DeliveryActivity extends AppCompatActivity {
    private static final String TAG = "DeliveryActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);
        Log.d(TAG,"onCreate : Started.");
        ListView mlistview = (ListView) findViewById(R.id.list_view_delivery);
        custom_list_item_delivery order1 = new custom_list_item_delivery("123456","20.07.01","Delivering");
        custom_list_item_delivery order2 = new custom_list_item_delivery("123456","20.07.01","Delivering");
        custom_list_item_delivery order3 = new custom_list_item_delivery("123456","20.07.01","Delivering");
        custom_list_item_delivery order4 = new custom_list_item_delivery("123456","20.07.01","Delivering");
        custom_list_item_delivery order5 = new custom_list_item_delivery("123456","20.07.01","Delivering");

        ArrayList<custom_list_item_delivery> item_list_delivery = new ArrayList<>();
        item_list_delivery.add(order1);
        item_list_delivery.add(order2);
        item_list_delivery.add(order3);
        item_list_delivery.add(order4);
        item_list_delivery.add(order5);

        CustomListAdapter_delivery adapter= new CustomListAdapter_delivery(this, R.layout.adapter_view_delivery,item_list_delivery,this);
        mlistview.setAdapter(adapter);
    }
}