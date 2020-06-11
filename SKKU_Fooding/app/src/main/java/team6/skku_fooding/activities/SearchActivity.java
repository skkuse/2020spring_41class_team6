package team6.skku_fooding.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

import team6.skku_fooding.R;

public class SearchActivity extends AppCompatActivity {

    String search_string;
    Button button_sorting;
    Integer sorting_order=0;
    ListView listview ;
    ListViewAdapter adapter;
    TextView home;
    TextView recommendation;
    TextView delivery;
    TextView mypage;
    Integer search_filter=0;
    String filter;
    Intent intent;
    SharedPreferences loginPref;

    private DatabaseReference dbReference;
    private DatabaseReference dbref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Listview
        adapter = new ListViewAdapter() ;

        listview = (ListView) findViewById(R.id._listView);
        listview.setAdapter(adapter);


        // Firebase user data load
        dbref= FirebaseDatabase.getInstance().getReference();
        loginPref = getSharedPreferences("user_SP", this.MODE_PRIVATE);
        String UID=loginPref.getString("UID",null);
        dbref.child("user").child(UID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                filter = dataSnapshot.child("filter").getValue(String.class);
                Log.d("Test",filter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }});

        // Firebase data load
        dbReference= FirebaseDatabase.getInstance().getReference().child("product");
        Query query=dbReference.orderByChild("uploaded_date");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Map<String, Object> currentObject = (Map<String, Object>) dataSnapshot.getValue();
                String name=currentObject.get("name").toString();
                String price=currentObject.get("price").toString();
                String ingredient=currentObject.get("ingredient").toString();
                String product_id=currentObject.get("product_id").toString();

                String imageString;
                Bitmap decodedImage;
                if(currentObject.get("image")!=null) {
                    imageString = currentObject.get("image").toString();
                    byte[]imageBytes = Base64.decode(imageString, Base64.DEFAULT);
                    decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                }else{
                    decodedImage=null;
                }

                if((search_filter==0)&&(filter!=null)){
                    Integer flag=0;
                    String[] ingredients_parsed=ingredient.split(",");
                    for (String ing:ingredients_parsed){
                        Log.d("Test_wrong",filter);
                        if(filter.toLowerCase().contains(ing)){
                            flag=1;
                            break;
                        }
                    }
                    if(flag==0)
                        adapter.addItem(product_id, decodedImage, name, price, ingredient);
                }
                else{
                    adapter.addItem(product_id, decodedImage, name, price, ingredient);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        // Search button
        Button button_search=findViewById(R.id.button_search);
        EditText edittext_search=findViewById(R.id.search_string);
        button_search.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(edittext_search.getText()==null){
                    Log.d("edittext","dismissed");
                }else {
                    search_string = edittext_search.getText().toString();
                    search(search_string);
                    Log.d("edittext", search_string);
                }
        }});

        // Sorting order button
        button_sorting =(Button)findViewById(R.id.button_search2);
        button_sorting.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(SearchActivity.this);
                dlg.setTitle("Sorting order");
                final String[] versionArray = new String[] {"Recent","High Price","Low Price"};
                dlg.setSingleChoiceItems(versionArray, sorting_order, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sorting_order=which;
                    }
                });
                dlg.setPositiveButton("Select",new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        // Change button_search_2 text according to the selection
                        listviewRebuild(sorting_order);
                        Toast.makeText(SearchActivity.this,"Sorting order changed", Toast.LENGTH_SHORT).show();
                    }
                });
                dlg.show();
            }
        });

        // Filtering button
        Button filter_onoff_button=(Button)findViewById(R.id.button_search3);
        filter_onoff_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(SearchActivity.this);
                dlg.setTitle("Filter ON/OFF");
                final String[] versionArray = new String[] {"ON","OFF"};
                dlg.setSingleChoiceItems(versionArray, search_filter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        search_filter=which;
                    }
                });
                dlg.setPositiveButton("Select",new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        // Change button_search_3 text according to the selection
                        if(search_filter==0){
                            filter_onoff_button.setText("FILTER: ON");
                            listviewRebuild(sorting_order);
                            Toast.makeText(SearchActivity.this,"Filter ON", Toast.LENGTH_SHORT).show();
                        }else if(search_filter==1){
                            filter_onoff_button.setText("FILTER: OFF");
                            listviewRebuild(sorting_order);
                            Toast.makeText(SearchActivity.this,"Filter OFF", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dlg.show();
            }
        });

        // Bottom menu bar
        home=(TextView)findViewById(R.id.home);
        recommendation=(TextView)findViewById(R.id.recommendation);
        delivery=(TextView)findViewById(R.id.delivery);
        mypage=(TextView)findViewById(R.id.mypage);

        home.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Connect to home (now)
            }
        });
        recommendation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("recommendation","I'm here");
                intent = new Intent(SearchActivity.this, RecommendationActivity.class);
                startActivity(intent);
            }
        });
        delivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("delivery","I'm here");
                intent = new Intent(SearchActivity.this, DeliveryActivity.class);
                startActivity(intent);
            }
        });
        mypage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("mypage","I'm here");
                intent = new Intent(SearchActivity.this, MyPageActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
    }

    void listviewRebuild(Integer sorting_order){
        Query query;
        if(sorting_order==0){
            button_sorting.setText("ORDER: RECENT");
            query=dbReference.orderByChild("uploaded_date");
        }else if(sorting_order==1){
            button_sorting.setText("ORDER: HIGH PRICE");
            query=dbReference.orderByChild("price");
        }else if(sorting_order==2){
            button_sorting.setText("ORDER: LOW PRICE");
            query=dbReference.orderByChild("price");
        }else{
            query=dbReference.orderByChild("uploaded_date");
        }

        ListViewAdapter adapter=new ListViewAdapter();
        listview.setAdapter(adapter);

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Map<String, Object> currentObject = (Map<String, Object>) dataSnapshot.getValue();
                String name=currentObject.get("name").toString();
                String price=currentObject.get("price").toString();
                String ingredient=currentObject.get("ingredient").toString();
                String product_id=currentObject.get("product_id").toString();

                String imageString;
                Bitmap decodedImage;
                if(currentObject.get("image")!=null) {
                    imageString = currentObject.get("image").toString();
                    byte[]imageBytes = Base64.decode(imageString, Base64.DEFAULT);
                    decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                }else{
                    imageString=null; decodedImage=null;
                }

                if(sorting_order==1) {
                    if(search_filter==0){
                        Integer flag=0;
                        String[] ingredients_parsed=ingredient.split(",");
                        for (String ing:ingredients_parsed){
                            if(filter.toLowerCase().contains(ingredient)){
                                flag=1;
                                break;
                            }
                        }
                        if(flag==0)
                            adapter.addItemIndex(product_id,0,decodedImage, name, price, ingredient);
                    }
                    else{
                        adapter.addItemIndex(product_id,0,decodedImage, name, price, ingredient);
                    }
                    adapter.notifyDataSetChanged();
                }
                else{
                    if(search_filter==0){
                        Integer flag=0;
                        String[] ingredients_parsed=ingredient.split(",");
                        for (String ing:ingredients_parsed){
                            if(filter.toLowerCase().contains(ingredient)){
                                flag=1;
                                break;
                            }
                        }
                        if(flag==0)
                            adapter.addItem(product_id,decodedImage, name, price, ingredient);
                    }
                    else{
                        adapter.addItem(product_id,decodedImage, name, price, ingredient);
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    void search(String text){
        ListViewAdapter tmp=new ListViewAdapter();
        Integer count=adapter.getCount();

        for(int i=0;i<count;i++){
            if(adapter.listViewItemList.get(i).getTitle().toLowerCase().contains(text.toLowerCase())){
                if(search_filter==0){
                    Integer flag=0;
                    String[] ingredients_parsed=adapter.listViewItemList.get(i).getIngredient().split(",");
                    for (String ing:ingredients_parsed){
                        if(filter.toLowerCase().contains(ing)){
                            flag=1;
                            break;
                        }
                    }
                    if(flag==0)
                        tmp.addListViewItem(adapter.listViewItemList.get((i)));
                }
                else{
                    tmp.addListViewItem(adapter.listViewItemList.get((i)));
                }
            }
        }

        listview.setAdapter(tmp);
    }
}

class ListViewItem {
    private Bitmap iconDrawable;
    private String titleStr;
    private String descStr;
    private String ingredient;
    private String product_id;

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

    public void setProductId(String pid){product_id=pid;}

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

    public String getProductId(){return this.product_id;}
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

        View finalConvertView = convertView;
        iconImageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // To Product List
                Intent intent = new Intent(finalConvertView.getContext(), Product_detail.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("product_id",listViewItem.getProductId());
                context.startActivity(intent);
            }
        });
        titleTextView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // To Product List
                Intent intent = new Intent(finalConvertView.getContext(), Product_detail.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("product_id",listViewItem.getProductId());
                context.startActivity(intent);
            }
        });
        descTextView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // To Product List
                Intent intent = new Intent(finalConvertView.getContext(), Product_detail.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("product_id",listViewItem.getProductId());
                context.startActivity(intent);
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

    public void addItem(String product_id, Bitmap icon, String title, String desc, String ingredient) {
        ListViewItem item = new ListViewItem();

        item.setIcon(icon);
        item.setTitle(title);
        item.setDesc(desc);
        item.setIngredient(ingredient);
        item.setProductId(product_id);

        listViewItemList.add(item);
    }

    public void addItemIndex(String product_id, Integer index, Bitmap icon, String title, String desc, String ingredient){
        ListViewItem item = new ListViewItem();

        item.setIcon(icon);
        item.setTitle(title);
        item.setDesc(desc);
        item.setIngredient(ingredient);
        item.setProductId(product_id);

        listViewItemList.add(index, item);
    }
    public void addListViewItem(ListViewItem item){
        listViewItemList.add(item);
    }
}
