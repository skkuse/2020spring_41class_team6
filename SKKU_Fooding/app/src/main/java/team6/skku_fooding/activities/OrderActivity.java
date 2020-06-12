package team6.skku_fooding.activities;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import team6.skku_fooding.R;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class OrderActivity extends AppCompatActivity {



    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    Calendar c1 = Calendar.getInstance();

    final String strToday = sdf.format(c1.getTime());

    EditText name_et, phonenumber_et, address_et, cardnumber_et, request_et;
    TextView orderinfo_tv, orderinfo_tv2;
    Button order_button;

    Integer count;
    Integer pid;
    String uid;
    Integer id_count = 0;
    String Type;
    Integer totalprice = 0;
    Integer pcount = 0;


    public class pair {
        Integer number;
        Integer price;

    }

    Map<Integer, pair> listmap = new HashMap<Integer, pair>();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        orderinfo_tv = (TextView) findViewById(R.id.orderinfo_tv);
        orderinfo_tv2 = (TextView) findViewById(R.id.orderinfo_tv2);
        name_et = (EditText) findViewById(R.id.name_et);
        phonenumber_et = (EditText) findViewById(R.id.phonenumber_et);
        address_et = (EditText) findViewById(R.id.address_et);
        cardnumber_et = (EditText) findViewById(R.id.cardnumber_et);
        request_et = (EditText) findViewById(R.id.request_et);
        order_button = (Button) findViewById(R.id.order_button);

        SharedPreferences loginPref;
        loginPref = getSharedPreferences("user_SP", this.MODE_PRIVATE);
        uid=loginPref.getString("UID",null);


        Intent intent=getIntent();
        String getting_item=intent.getStringExtra("sending_item");

        /*
        Intent intent = getIntent();
        String Type = intent.getStringExtra("Type");
        if(Type == "Normal") {
            Integer count = intent.getIntExtra("count", 1);
            String name = intent.getStringExtra("name");
        }
        else if (Type == "All") {
            String parse = intent.getStringExtra("parse");
        }


         */
        //Type = "Normal";
        //pid=200;
        //count = 3;  // More intent would be come
        //uid = UID;

        /*
        DatabaseReference mydb = FirebaseDatabase.getInstance().getReference();
        if(Type == "Normal") {
            mydb.child("product").orderByChild("product_id").equalTo(pid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Integer price = 0;
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        price = d.child("price").getValue(Integer.class);
                        String name = d.child("name").getValue(String.class);
                        tv.setText(name + " " + count + "개를 구입합니다.\n" + "총 " + price * count + "원 입니다.");
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        else if(Type=="All") {
            //intent로 값을 받아오면 이 부분은 지우고 밑의 parsingall(parse)를 주석해제하여 실행하면 된다. 임시 데이터셋 테스트용
            mydb.child("user").orderByChild("UID").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot d: dataSnapshot.getChildren()) {
                        String cart = d.child("shopping_cart").getValue(String.class);
                        Log.d("check", "cart : " + cart);
                        parsingall(cart);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            //parsingall(parse); //Algi에게 intent로 shopping_cart 정보를 받아온 것을 parsing
        }
*/
    parsingall(getting_item);
    }
    //문자열 파싱 후 db호출
    public void parsingall(String parse) {

        DatabaseReference parsedb = FirebaseDatabase.getInstance().getReference();

        String[] array = parse.split("-");
        for (String item: array) {
            String[] a = item.split(":");
            pair b = new pair();
            b.number = Integer.parseInt(a[1]);
            b.price = Integer.parseInt(a[2]);
            listmap.put(Integer.parseInt(a[0]), b);
        }

        for (Map.Entry<Integer, pair> entry : listmap.entrySet()) {
            int key = entry.getKey();
            pair set = entry.getValue();
            totalprice = totalprice + (set.number * set.price);


        }

        Map.Entry<Integer, pair> entry = listmap.entrySet().iterator().next();
        int key = entry.getKey();
        final int size = listmap.size();

        parsedb.child("product").orderByChild("product_id").equalTo(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot d: dataSnapshot.getChildren()) {
                    String pname = d.child("name").getValue(String.class);
                    orderinfo_tv.setText(size+ " order(s) including '"+ pname+ "'.");
                    orderinfo_tv2.setText("Total: " + totalprice + "₩");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





    }

    public void onButtonClick(View v) {
        String name = name_et.getText().toString();
        String phonenumber = phonenumber_et.getText().toString();
        String address = address_et.getText().toString();
        String cardnumber = cardnumber_et.getText().toString();
        String request = request_et.getText().toString();
        if (name.length()==0 || phonenumber.length()==0 || address.length() == 0 || cardnumber.length() == 0)
        {
            Toast.makeText(this.getApplicationContext(), "Please fill out the form.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(Type == "Normal") {
            setdb(pid, 0);
        }
        else if(Type == "All") {
            Log.d("check", "list : " + listmap.entrySet());

            for (Map.Entry<Integer, pair> entry1 : listmap.entrySet()) {
                int key = entry1.getKey();
                Log.d("check", "count");
                setdb(key, pcount);
                pcount += 1;

            }
        }

        Toast.makeText(this.getApplicationContext(), "Your order has been received.", Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(OrderActivity.this, SearchActivity.class);
        startActivity(intent);
    }

    public void setdb(final Integer ppid, final Integer ncount) {
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference();
        final String name = name_et.getText().toString();
        final String phonenumber = phonenumber_et.getText().toString();
        final String address = address_et.getText().toString();
        final String cardnumber = cardnumber_et.getText().toString();
        final String request = request_et.getText().toString();

        if (name.length()==0 || phonenumber.length()==0 || address.length() == 0 || cardnumber.length() == 0)
            Toast.makeText(this.getApplicationContext(), "Please fill out the form.", Toast.LENGTH_SHORT).show();
        else
        {

            dbref.child("order").orderByChild("order_id").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot d : dataSnapshot.getChildren()) {

                        id_count = d.child("order_id").getValue(Integer.class) + ncount + 1;
                        DatabaseReference dbref2 = FirebaseDatabase.getInstance().getReference();

                        dbref2.child("order").child("order_id" + id_count).child("counter").setValue(count);
                        dbref2.child("order").child("order_id" + id_count).child("order_id").setValue(id_count);
                        dbref2.child("order").child("order_id" + id_count).child("product_id").setValue(ppid);
                        dbref2.child("order").child("order_id" + id_count).child("date").setValue(strToday);
                        dbref2.child("order").child("order_id" + id_count).child("UID").setValue(uid);
                        dbref2.child("order").child("order_id" + id_count).child("name").setValue(name);
                        dbref2.child("order").child("order_id" + id_count).child("phonenumber").setValue(phonenumber);
                        dbref2.child("order").child("order_id" + id_count).child("address").setValue(address);
                        dbref2.child("order").child("order_id" + id_count).child("cardnumber").setValue(cardnumber);
                        if (request.length() != 0) {
                            dbref2.child("order").child("order_id" + id_count).child("request").setValue(request);
                        }
                        dbref2.child("order").child("order_id" + id_count).child("status").setValue("Delivering");

                        DatabaseReference mydb = FirebaseDatabase.getInstance().getReference();
                        mydb.child("product").orderByChild("product_id").equalTo(pid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Integer price = 0;
                                for (DataSnapshot d : dataSnapshot.getChildren()) {
                                    price = d.child("price").getValue(Integer.class);
                                    String name = d.child("name").getValue(String.class);
                                    DatabaseReference mydb1 = FirebaseDatabase.getInstance().getReference();
                                    mydb1.child("order").child("order_id" + id_count).child("price").setValue(price * count);
                                    mydb1.child("order").child("order_id" + id_count).child("product_name").setValue(name);

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



        }
    }


}



