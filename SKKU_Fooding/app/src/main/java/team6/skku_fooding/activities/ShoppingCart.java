package team6.skku_fooding.activities;

import android.content.Context;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shoppingcart);
        reff = FirebaseDatabase.getInstance().getReference().child("user").child("dummy");
        //Put shoppingcart array adapter here
    }
}