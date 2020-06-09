package team6.skku_fooding.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import team6.skku_fooding.R;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.google.firebase.database.*;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

public class RecommendationActivity extends AppCompatActivity {

    public class Review {

        Double rate;
        Integer product_id;

        public Review() {

        }

        public Double getRate() {
            return rate;
        }

        public Integer getProduct_id() {
            return product_id;
        }
    }

    public class dub {
        Double rate;
        Integer count;

        public dub(){

        }

        public Double getRate() {
            return rate;
        }

        public Integer getCount() {
            return count;
        }
    }

    public class Product {
        Integer product_id;
        Double avgrate;

        public Product() {

        }

        public Integer getProduct_id() {
            return product_id;
        }

        public Double getAvgrate() {
            return avgrate;
        }
    }

    public class information {

        String name;
        String image;
        String ingredient;
        Double rate;
        String company;
        String price;

    }

    private DatabaseReference dbref = FirebaseDatabase.getInstance().getReference();
    ArrayList<Review> rlist = new ArrayList<Review>();

    // For listview
    ListView listview;
    ListViewAdapter adapter;
    SharedPreferences loginPref;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation);

        // Listview
        adapter = new ListViewAdapter() ;
        listview = (ListView) findViewById(R.id._listView);
        listview.setAdapter(adapter);

        loginPref = getSharedPreferences("user_SP", this.MODE_PRIVATE);

        getcid();

        Button button = (Button)findViewById(R.id.change);
        Button setrate = (Button)findViewById(R.id.criteria);

        setrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show();


            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), SurveyActivity.class);

                intent.putExtra("name","R");

                startActivity(intent);
            }
        });




    }

    public void show() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Change rate criteria");
        alert.setMessage("Input your rate criteria");


        final EditText criteria = new EditText(this);
        alert.setView(criteria);

        alert.setPositiveButton("save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String str_cri = criteria.getText().toString();
                Double cri = 0.0;
                boolean flag = true;
                try {
                    cri = Double.parseDouble(str_cri);
                }
                catch(Exception e) {
                    errshow();
                    flag = false;

                }
                if(cri > 5.0 || cri < 0.0) {
                    errshow();
                    flag = false;
                }
                if(flag) {
                    DatabaseReference d = FirebaseDatabase.getInstance().getReference();
                    d.child("user").child("dummy").child("criteria").setValue(cri);
                    Intent in1 = new Intent(getApplicationContext(), RecommendationActivity.class);
                    startActivity(in1);
                }


            }
        });


        alert.show();
    }

    public void errshow() {
        Toast erring = Toast.makeText(this.getApplicationContext(), "소수로 입력해주세요 ex) 3.5", Toast.LENGTH_SHORT);
        erring.show();
    }

    public void getcid() {
        String UID=loginPref.getString("UID",null);
        dbref.child("user").child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer category_id = dataSnapshot.child("category_id").getValue(Integer.class);

                /* 강제종료 등의 이유로 survey가 skip되었을 때
                if (category_id == null) {
                    Intent gosurvey = new Intent(getApplicationContext(), SurveyActivity.class);
                    gosurvey.putExtra("name", "R");
                    startActivity(gosurvey);
                }

                 */


                Double criteria = dataSnapshot.child("criteria").getValue(Double.class);
                String filter = dataSnapshot.child("filter").getValue(String.class);
                Log.d("value", "category_id" + category_id);
                getreview(category_id, criteria, filter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        });


    }

    public void getreview(Integer cid, final Double criteria, final String filter) {
        dbref.child("review").orderByChild("category_id").equalTo(cid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot shot : dataSnapshot.getChildren()) {
                    Review review1 = new Review();
                    review1.rate = shot.child("rate").getValue(Double.class);
                    review1.product_id = shot.child("product_id").getValue(Integer.class);
                    rlist.add(review1);

                    adapter.addItem(null, "aaa","aaa","aaa");

                }
                Map<Integer, dub> map = new HashMap<Integer, dub>();
                map.clear();
                for (Review r : rlist) {
                    if (map.containsKey(r.product_id)) {
                        dub gets = new dub();
                        gets = map.get(r.product_id);
                        gets.rate = (r.rate + gets.rate * gets.count) / (1+gets.count);
                        gets.count = gets.count + 1;
                        map.put(r.product_id, gets);
                    }
                    else {
                        dub puts = new dub();
                        puts.rate = r.rate;
                        puts.count = 1;
                        map.put(r.product_id, puts);
                    }

                }
                List<Product> plist = new ArrayList<Product>();
                plist.clear();
                for (Map.Entry<Integer, dub> entry : map.entrySet()) {
                    if (entry.getValue().rate > criteria) {
                        Product product = new Product();
                        product.avgrate = entry.getValue().rate;
                        product.product_id = entry.getKey();
                        plist.add(product);

                    }
                }
                for (Product p : plist) {
                    Log.d("List", "recommend_id : "+p.product_id + " rate : " +p.avgrate);
                }
                getProduct(plist, filter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        });
    }

    public void getProduct(List<Product> productList, final String filter) {
        Log.d("Test", "1");
        Log.d("Test", "product"+productList.size());
        for (final Product p : productList) {
            Log.d("Test", "2");
            final Double r = p.avgrate;
            dbref.child("product").orderByChild("product_id").equalTo(p.product_id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot shot : dataSnapshot.getChildren()) {
                        information i = new information();

                        i.ingredient = shot.child("ingredient").getValue(String.class);
                        if (applyfilter(filter, i.ingredient)) {
                            i.image = shot.child("image").getValue(String.class);
                            i.name = shot.child("name").getValue(String.class);
                            i.company = shot.child("company").getValue(String.class);
                            i.price = shot.child("price").getValue(String.class);
                            i.rate = r;

                            Bitmap decodedImage;

                            byte[]imageBytes = Base64.decode(i.image, Base64.DEFAULT);
                            decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);


                            //리스트뷰 추가 시점
                            adapter.addItem(decodedImage,i.name,i.price,i.ingredient);
                        }
                    }
                    adapter.notifyDataSetChanged();


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

    public boolean applyfilter(String f, String i) {//f = filter 문자열, i = ingredient 문자열
        String[] array1 = f.split(",");
        String[] array2 = i.split(",");
        for (String a : array1) {
            for (String b : array2) {
                if(a.equals(b)) {
                    return false; //1개라도 일치하면 false
                }
            }
        }
        return true;
    }
}
/*
class ListViewItem {
    private Bitmap iconDrawable;
    private String titleStr;
    private String descStr;
    private String ingredient;

    public void setIcon(Bitmap icon) {
        iconDrawable = icon;
    }

    public void setTitle(String title) {
        titleStr = title;
    }

    public void setDesc(String desc) {
        descStr = desc;
    }

    public void setIngredient(String ing){ingredient=ing;}

    public Bitmap getIcon() {
        return this.iconDrawable;
    }

    public String getTitle() {
        return this.titleStr;
    }

    public String getDesc() {
        return this.descStr;
    }

    public String getIngredient(){return this.ingredient;}
}

class ListViewAdapter extends BaseAdapter {
    public ArrayList<ListViewItem> listViewItemList = new ArrayList<ListViewItem>() ;

    public ListViewAdapter() {

    }

    @Override
    public int getCount() {
        return listViewItemList.size() ;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.activity_search_layout, parent, false);
        };

        ImageView iconImageView = (ImageView) convertView.findViewById(R.id.imageView1) ;
        TextView titleTextView = (TextView) convertView.findViewById(R.id.textView1) ;
        TextView descTextView = (TextView) convertView.findViewById(R.id.textView2) ;

        ListViewItem listViewItem = listViewItemList.get(position);

        titleTextView.setText(listViewItem.getTitle());
        descTextView.setText(listViewItem.getDesc());
        iconImageView.setImageBitmap(listViewItem.getIcon());

        iconImageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // To Product List
                Log.d("image",listViewItemList.get(position).getTitle());
            }
        });
        titleTextView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // To Product List
                Log.d("title",listViewItemList.get(position).getTitle());
            }
        });
        descTextView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // To Product List
                Log.d("desc",listViewItemList.get(position).getTitle());
            }
        });

        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position ;
    }

    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position) ;
    }

    public void addItem(Bitmap icon, String title, String desc, String ingredient) {
        ListViewItem item = new ListViewItem();

        item.setIcon(icon);
        item.setTitle(title);
        item.setDesc(desc);
        item.setIngredient(ingredient);

        listViewItemList.add(item);
    }

    public void addItemIndex(Integer index, Bitmap icon, String title, String desc, String ingredient){
        ListViewItem item = new ListViewItem();

        item.setIcon(icon);
        item.setTitle(title);
        item.setDesc(desc);
        item.setIngredient(ingredient);

        listViewItemList.add(index, item);
    }
    public void addListViewItem(ListViewItem item){
        listViewItemList.add(item);
    }
}*/