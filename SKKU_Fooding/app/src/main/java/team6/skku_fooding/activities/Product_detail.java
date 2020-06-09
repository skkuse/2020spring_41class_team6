package team6.skku_fooding.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
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


public class Product_detail extends AppCompatActivity {
TextView company,name,ingredient,price,uploadeddate;
TextView specificavg,generalavg;
ImageView image;
TextView amountindicator;
double specificaverage;
double generalaverage;
int countspecific;
int countgeneral;

    DatabaseReference reff;
    DatabaseReference reff1;

    ArrayList<Review> rewsspecific = new ArrayList<>();
    ArrayList<Review> rewsgeneral = new ArrayList<>();
    String finallastversion;
    String[]forthefirstamount;
    String[]forthefirstamountsecondsplit;
    String productprice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.productdetail);
        FirebaseApp.initializeApp(this);
        amountindicator=(TextView)findViewById(R.id.showamount);



        //The previous intent need to give below info
        //then manually written numbers in if statement need to be changed according to that
        //Intent myIntent = getIntent(); // gets the previously created intent
        //productid = myIntent.getStringExtra("productid");
        //userid= myIntent.getStringExtra("userid");
        //categoryid= Integer.parseInt(myIntent.getStringExtra("categoryid"));


        company=findViewById(R.id.company);
        image=findViewById(R.id.image);
        name=findViewById(R.id.name);
        ingredient=findViewById(R.id.ingredient);
        price= findViewById(R.id.price);
        uploadeddate=findViewById(R.id.uploaded_date);
        reff= FirebaseDatabase.getInstance().getReference().child("product").child("200");
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String comp=dataSnapshot.child("company").getValue().toString();
                String img=dataSnapshot.child("image").getValue().toString();
                String ing=dataSnapshot.child("ingredient").getValue().toString();
                String nm=dataSnapshot.child("name").getValue().toString();
                String prc=dataSnapshot.child("price").getValue().toString();
                productprice=prc;
                String up_date=dataSnapshot.child("uploaded_date").getValue().toString();
                byte[]imageBytes = Base64.decode(img, Base64.DEFAULT);
                Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                image.setImageBitmap(decodedImage);
                company.setText(comp);
                name.setText(nm);
                ingredient.setText(ing);
                price.setText(prc);
                uploadeddate.setText(up_date);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        reff1= FirebaseDatabase.getInstance().getReference().child("user").child("X7u2ls7ro9PlL4JJTKFnukUpyAk1");


        reff1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                finallastversion=dataSnapshot.child("shopping_cart").getValue().toString();
                forthefirstamount=finallastversion.split("-",0);
                for(String values1:forthefirstamount){
                    forthefirstamountsecondsplit=values1.split(":",0);

                      if(forthefirstamountsecondsplit[0].equals("200")){

                          amountindicator.setText(forthefirstamountsecondsplit[1].toString());
                      }

                }




            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


      reviews();


    }


    public void reviews(){
        ArrayList<String>userid;
        ArrayList<String>modifieddate;
        ArrayList<String>title;
        ArrayList<String>score;
        ArrayList<String>description;

        System.out.println("baslingic");
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference reviewsRef = rootRef.child("review");
        System.out.println("devam");

        reviewsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                System.out.println("girdik");
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    Review forproductreviewspecific=new Review();
                    Review forproductreviewgeneral=new Review();
                    //Reviews going tobe set here and then add to list
                    int ratespecific=0;
                    int rategeneral=0;
                    if(ds.child("categoryId").exists()) {
                        if (Integer.parseInt(ds.child("categoryId").getValue().toString()) == 0 && Integer.parseInt(ds.child("productId").getValue().toString()) == 200) {
                            System.out.println("varmis");
                            forproductreviewspecific.userId ="RId: "+ds.child("reviewId").getValue().toString();
                            forproductreviewspecific.score = "Score: "+ds.child("rate").getValue().toString();
                            forproductreviewspecific.modifiedDate =  (ds.child("modifiedDate").getValue()).toString();
                            forproductreviewspecific.title = "Title: "+ds.child("title").getValue().toString();
                            forproductreviewspecific.description = ds.child("description").getValue().toString();

                            ratespecific = Integer.parseInt(ds.child("rate").getValue().toString());
                            specificaverage = specificaverage + ratespecific;
                            rewsspecific.add(forproductreviewspecific);

                            System.out.println(rewsspecific);
                            countspecific++;
                        }
                        if (Integer.parseInt(ds.child("productId").getValue().toString()) == 200) {
                            forproductreviewgeneral.userId = ds.child("reviewId").getValue().toString();

                            forproductreviewgeneral.score = ds.child("rate").getValue().toString();
                            forproductreviewgeneral.modifiedDate =  (ds.child("modifiedDate").getValue()).toString();
                            forproductreviewgeneral.title = ds.child("title").getValue().toString();
                            forproductreviewgeneral.description = ds.child("description").getValue().toString();
                            rategeneral = Integer.parseInt(ds.child("rate").getValue().toString());
                            generalaverage = generalaverage + rategeneral;
                            rewsgeneral.add(forproductreviewgeneral);
                            countgeneral++;
                        }
                    }

                    System.out.println("bakbakalim");
                }
                calculations(rewsspecific,rewsgeneral);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }

    public void order(View view){
        //The one who have the activity of order will fill here.
       Intent ordernow = new Intent();
       startActivity(ordernow);


    }

    public void remove(View view){
        reff= FirebaseDatabase.getInstance().getReference().child("user").child("X7u2ls7ro9PlL4JJTKFnukUpyAk1");
        String shoppingcart;
        String finalcart;
        String productnum;
        productnum="200";
        reff.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String [] firstdivide;
                String lastversion="";


                String shoppingcart=dataSnapshot.child("shopping_cart").getValue().toString();
                firstdivide=shoppingcart.split("-",0);
                for(String cart:firstdivide) {
                    if(!(cart.startsWith(productnum))){
                        lastversion=lastversion+cart+"-";
                    }

                }

                finallastversion=lastversion;
                reff.child("shopping_cart").setValue(lastversion);
                Context context = getApplicationContext();
                CharSequence text = " Removed from Shoppingcart";
                amountindicator=(TextView)findViewById(R.id.showamount);
                amountindicator.setText("0");
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }


    public void shoppingcartadd(View view){

        reff= FirebaseDatabase.getInstance().getReference().child("user").child("X7u2ls7ro9PlL4JJTKFnukUpyAk1");

        reff.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String[]firstdivide;
                String[]seconddivide;
                String lastversion="";
                int howmanyproduct=0;
                Boolean already=false;
                String productnum="200";

                String shoppingcart=dataSnapshot.child("shopping_cart").getValue().toString();

                firstdivide=shoppingcart.split("-",0);
                System.out.println(firstdivide);
                for(String cart:firstdivide) {

                    if (!(shoppingcart.equals("none"))) {
                        seconddivide = cart.split(":", 0);
                        System.out.println(seconddivide[0]);

                        if (seconddivide[0].equals(productnum)) {

                            already = true;
                            howmanyproduct=Integer.parseInt(seconddivide[1]) + 1;
                            lastversion = lastversion + seconddivide[0] + ":" + howmanyproduct +":"+seconddivide[2]+ "-";

                        } else {
                            lastversion = lastversion + seconddivide[0] + ":" + seconddivide[1]+":" +seconddivide[2]+ "-";
                        }

                    }
                }
                if(already==false){
                    lastversion = lastversion + productnum + ":" + 1+":"+productprice+"-";
                    howmanyproduct=1;
                }
                finallastversion=lastversion;
                reff.child("shopping_cart").setValue(lastversion);
                Context context = getApplicationContext();

                amountindicator.setText(Integer.toString(howmanyproduct));

                CharSequence text = howmanyproduct+" Number of Product are in Shoppingcart";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void shoppingcartdelete(View view){
        reff= FirebaseDatabase.getInstance().getReference().child("user").child("X7u2ls7ro9PlL4JJTKFnukUpyAk1");
        reff.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String[]firstdivide;
                String[]seconddivide;
                String lastversion="";
                int howmanyproduct=0;
                Boolean already=false;
                String productnum="200";

                String shoppingcart=dataSnapshot.child("shopping_cart").getValue().toString();

                firstdivide=shoppingcart.split("-",0);
                System.out.println(firstdivide);
                for(String cart:firstdivide) {

                    if (!(shoppingcart.equals("none"))) {

                        System.out.println("bbbbbbbbb");
                        seconddivide = cart.split(":", 0);
                        System.out.println(seconddivide[0]);
                        if (seconddivide[0].equals(productnum)) {

                            already = true;
                            System.out.println("offfff");
                            if(!(Integer.parseInt(seconddivide[1]) - 1==0)) {
                                howmanyproduct = Integer.parseInt(seconddivide[1]) - 1;
                                lastversion = lastversion + seconddivide[0] + ":" + howmanyproduct + ":"+seconddivide[2]+ "-";
                            }

                        } else {
                            lastversion = lastversion + seconddivide[0] + ":" + seconddivide[1] +":"+seconddivide[2]+ "-";
                        }

                    }
                }

                if(lastversion==""){
                    lastversion="none";
                }
                finallastversion=lastversion;
                reff.child("shopping_cart").setValue(lastversion);
                Context context = getApplicationContext();

                amountindicator.setText(Integer.toString(howmanyproduct));
                CharSequence text = howmanyproduct+" Number of Product are in Shoppingcart";

                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }
    public void seeshoppingcart(View view){


        Intent gocart=new Intent(this,ShoppingCart.class);
        gocart.putExtra("shoppingcartvalues",finallastversion);

        System.out.println(finallastversion);
        startActivity(gocart);

    }

    public void calculations(ArrayList<Review>reviewlistspecific,ArrayList<Review>rewsgeneral){
        ArrayList<String>useridspefic=new ArrayList<String>();
        ArrayList<String>modifieddatespecific=new ArrayList<String>();
        ArrayList<String>scorespecific=new ArrayList<String>();
        ArrayList<String>titlespecific=new ArrayList<String>();
        ArrayList<String>descriptionspecific=new ArrayList<String>();

        ArrayList<String>useridgeneral=new ArrayList<String>();
        ArrayList<String>modifieddategeneral=new ArrayList<String>();
        ArrayList<String>scoregeneral=new ArrayList<String>();
        ArrayList<String>titlegeneral=new ArrayList<String>();
        ArrayList<String>descriptiongeneral=new ArrayList<String>();


        specificavg=findViewById(R.id.s);
        generalavg=findViewById(R.id.g);
        System.out.println(generalaverage);
        System.out.println(specificaverage);
        System.out.println(countgeneral);
        System.out.println(countspecific);

        if(countgeneral!=0) {
            double countgen=(generalaverage / countgeneral);
            String result = String.format("%.2f", countgen);
            generalavg.setText(result);
        }
        if(countspecific!=0) {
            double countspec=(specificaverage / countspecific);
            String result = String.format("%.2f", countspec);
            specificavg.setText(result);
        }

        generalaverage=0;
        specificaverage=0;
        countgeneral=0;
        countspecific=0;
        System.out.println("yazdirma");
        System.out.println(reviewlistspecific);
        System.out.println(rewsgeneral);


        ListView mListView=(ListView) findViewById(R.id.listview);
        for(Review a:reviewlistspecific){
            useridspefic.add(a.userId);
            modifieddatespecific.add(a.modifiedDate.toString());
            scorespecific.add(a.score);
            titlespecific.add(a.title);
            descriptionspecific.add(a.description);

        }
        ListView m2ListView=(ListView) findViewById(R.id.listview2);
        for(Review b:rewsgeneral){
            useridgeneral.add(b.userId);
            modifieddategeneral.add(b.modifiedDate.toString());
            scoregeneral.add(b.score);
            titlegeneral.add(b.title);
            descriptiongeneral.add(b.description);

        }

        ReviewListAdapter adapter1 = new ReviewListAdapter(this,useridspefic,titlespecific,modifieddatespecific,scorespecific,descriptionspecific);
        mListView.setAdapter(adapter1);
        ReviewListAdapter adapter2 = new ReviewListAdapter(this,useridgeneral,titlegeneral,modifieddategeneral,scoregeneral,descriptiongeneral);
        m2ListView.setAdapter(adapter2);
        System.out.println("ppppppp");
        rewsspecific.clear();
        rewsgeneral.clear();
    }



}



