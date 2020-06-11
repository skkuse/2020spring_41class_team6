package team6.skku_fooding.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;


import androidx.appcompat.app.AppCompatActivity;

import team6.skku_fooding.R;


public class ShoppingCart extends AppCompatActivity {
    DatabaseReference reff;
    DatabaseReference reff1;
    String shoppingcart;
    String[]firstdivide;
    String[]seconddivide;
    String UID;

    final ArrayList<Integer>clicked=new ArrayList<Integer>();
    final ArrayList<String>images=new ArrayList<String>();
    final ArrayList<String>productnames=new ArrayList<String>();;
    final ArrayList<String>amount=new ArrayList<String>();;
    final ArrayList<String>prices=new ArrayList<String>();;
    final ArrayList<String>productids=new ArrayList<String>();;

    ArrayList<String>selectednames;
    ArrayList<String>selectedprices;
    ArrayList<String>selectedamount;
    ArrayList<String>selectedproductids;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shoppingcart);

        SharedPreferences loginPref;
        loginPref = getSharedPreferences("user_SP", this.MODE_PRIVATE);
        UID=loginPref.getString("UID",null);

        reff = FirebaseDatabase.getInstance().getReference().child("product");
        reff1=FirebaseDatabase.getInstance().getReference().child("user").child(UID);
        //Put shoppingcart array adapter here
        Intent myIntent = getIntent();

        shoppingcart = myIntent.getStringExtra("shoppingcartvalues");
        System.out.println("ooffffffffffff");
        System.out.println(shoppingcart);
        reff1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                shoppingcart=dataSnapshot.child("shopping_cart").getValue().toString();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        reff.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!(shoppingcart.equals("none"))&&!(shoppingcart.equals(""))&&!(shoppingcart.equals("@"))) {

                    firstdivide = shoppingcart.split("-");
                    for (String cart1 : firstdivide) {

                        seconddivide = cart1.split(":");
                        productids.add(seconddivide[0]);
                        productnames.add(dataSnapshot.child(seconddivide[0]).child("name").getValue().toString());
                        amount.add(seconddivide[1]);
                        prices.add(dataSnapshot.child(seconddivide[0]).child("price").getValue().toString());
                        images.add(dataSnapshot.child(seconddivide[0]).child("image").getValue().toString());
                        clicked.add(0);
                    }
                }

                listing(productnames,amount,clicked,prices,productids);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

        public void listing (ArrayList<String>productnames,ArrayList<String>amount,ArrayList<Integer>clicked,ArrayList<String>prices,ArrayList<String>productids) {
            ListView lv = (ListView) findViewById(R.id.selectView);




            ShoppingAdapter adapter = new ShoppingAdapter(this, images, productnames, amount,clicked,prices,productids,shoppingcart,UID);
            lv.setAdapter(adapter);

            selectednames=adapter.selectednameslist();
            selectedamount=adapter.selectedamountlist();
            selectedprices=adapter.selectedpriceslist();
            selectedproductids=adapter.selectedproductidlist();

        }

        public void orderall(View view){

        //Before the below statement shoppingcart will be given with intent
            reff1.child("shopping_cart").setValue("none");



        }
        public void orderselected(View view){
            String createintent="";

            String[]firstdivide;
            String[]secdivide;
            String lastversion="";

            firstdivide = shoppingcart.split("-",0);
            System.out.println(selectedproductids);
            for (String cart1 : firstdivide) {
                System.out.println(cart1);
                secdivide=cart1.split(":",0);
                if(!(selectedproductids.contains(secdivide[0]))){{
                    lastversion=lastversion+cart1+"-";
                }
                }


            }
            if(selectedproductids.size()==0){
                lastversion=shoppingcart;
            }




            reff1.child("shopping_cart").setValue(lastversion);

            for(int i=0; i<selectedproductids.size();i++){
                createintent=createintent+selectedproductids.get(i)+":"+selectedamount.get(i)+":"+selectedprices.get(i)+"-";
            }
            System.out.println(selectedproductids);
            System.out.println("999999999999999");
            System.out.println(createintent);
            System.out.println("999999999999999");
            //need to give createintent
    }
}
