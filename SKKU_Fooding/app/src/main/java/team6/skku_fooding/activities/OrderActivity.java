package team6.skku_fooding.activities;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import team6.skku_fooding.R;

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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderActivity extends AppCompatActivity {



    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    Calendar c1 = Calendar.getInstance();

    final String strToday = sdf.format(c1.getTime());

    EditText et, et1, et2;
    TextView tv;
    Button button;

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

        tv = (TextView) findViewById(R.id.textView);
        button = (Button) findViewById(R.id.button);
        et = (EditText) findViewById(R.id.address);
        et1 = (EditText) findViewById(R.id.cardnumber);
        et2 = (EditText) findViewById(R.id.comment);








        /*
        Intent intent = getIntent();
        Integer count = intent.getIntExtra("count", 1);
        String name = intent.getStringExtra("name");
        String type = intent.getStringExtra("Type");

         */
        Type = "Normal";
        pid = 200;
        count = 3;
        uid = "9yxJe7BjmAfW0t39O4i5tbUfm4m1"; //임시 데이터셋


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
        }


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
        final int size = listmap.size() - 1;

        parsedb.child("product").orderByChild("product_id").equalTo(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot d: dataSnapshot.getChildren()) {
                    String pname = d.child("name").getValue(String.class);
                    tv.setText(pname+ " 외 " + size + "건의 주문이 접수되었습니다.\n" + "총 결제금액은 " + totalprice + "원 입니다.");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





    }

    public void onButtonClick(View v) {
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



    }

    public void setdb(final Integer ppid, final Integer ncount) {
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference();
        final String address = et.getText().toString();
        final String cardnumber = et1.getText().toString();
        final String comment = et2.getText().toString();

        if (address.length() == 0 || cardnumber.length() == 0) {
            Toast erring1 = Toast.makeText(this.getApplicationContext(), "주소와 카드번호를 제대로 입력해주세요", Toast.LENGTH_SHORT);
            erring1.show();
        } else {

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
                        dbref2.child("order").child("order_id" + id_count).child("address").setValue(address);
                        dbref2.child("order").child("order_id" + id_count).child("cardnumber").setValue(cardnumber);
                        if (comment.length() != 0) {
                            dbref2.child("order").child("order_id" + id_count).child("comment").setValue(comment);
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



