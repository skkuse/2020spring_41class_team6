package team6.skku_fooding.activities;

import android.content.Context;
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


public class ShoppingCart extends AppCompatActivity {
    DatabaseReference reff;
    String shoppingcart;
    String[]firstdivide;
    String[]seconddivide;

    final ArrayList<Integer>clicked=new ArrayList<Integer>();
    final ArrayList<String>images=new ArrayList<String>();
    final ArrayList<String>productnames=new ArrayList<String>();;
    final ArrayList<String>amount=new ArrayList<String>();;
    final ArrayList<String>prices=new ArrayList<String>();;

    ArrayList<String>selectednames;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shoppingcart);
        reff = FirebaseDatabase.getInstance().getReference().child("product");
        //Put shoppingcart array adapter here
        Intent myIntent = getIntent();

        shoppingcart = myIntent.getStringExtra("shoppingcartvalues");
        System.out.println("ooffffffffffff");
        System.out.println(shoppingcart);
        reff.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                firstdivide = shoppingcart.split("-");
                for (String cart1 : firstdivide) {
                    System.out.println("ooooyyyyyy");
                    seconddivide = cart1.split(":");
                    System.out.println(seconddivide[0]);
                    System.out.println(dataSnapshot.child(seconddivide[0]).child("name").getValue().toString());
                    productnames.add(dataSnapshot.child(seconddivide[0]).child("name").getValue().toString());
                    amount.add(seconddivide[1]);
                    prices.add(dataSnapshot.child(seconddivide[0]).child("price").getValue().toString());
                    images.add(dataSnapshot.child(seconddivide[0]).child("image").getValue().toString());
                    clicked.add(0);
                }

                listing(productnames,amount,clicked,prices);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

        public void listing (ArrayList<String>productnames,ArrayList<String>amount,ArrayList<Integer>clicked,ArrayList<String>prices) {
            ListView lv = (ListView) findViewById(R.id.selectView);




            ShoppingAdapter adapter = new ShoppingAdapter(this, images, productnames, amount,clicked,prices);
            lv.setAdapter(adapter);

            selectednames=adapter.selectednameslist();



        }

        public void orderall(View view){


        }
        public void orderselected(View view){
            System.out.println(selectednames);

        }




    }
