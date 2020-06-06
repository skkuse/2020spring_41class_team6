package team6.skku_fooding.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import team6.skku_fooding.R;
import team6.skku_fooding.models.Ingredient;
import team6.skku_fooding.models.Product;

public class SearchActivity extends AppCompatActivity {

    String search_string;
    Button search_button;
    DataSnapshot dataSnapshot_from_firebase;
    ArrayList<Product> product_array_list;
    Integer sorting_order=0;

    private DatabaseReference dbReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Listview
        ListView listview ;
        ListViewAdapter adapter;
        adapter = new ListViewAdapter() ;

        listview = (ListView) findViewById(R.id._listView);
        listview.setAdapter(adapter);

        product_array_list=new ArrayList<Product>();

        // Firebase data load
        dbReference= FirebaseDatabase.getInstance().getReference().child("product");
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {

                dataSnapshot_from_firebase=dataSnapshot;
                Log.d("TAG", "onChildAdded:" + dataSnapshot_from_firebase.getKey());

                Map<String, Object> currentObject = (Map<String, Object>) dataSnapshot_from_firebase.getValue();
                String name=currentObject.get("name").toString();
                String price=currentObject.get("price").toString();

                // Make product object
                Product new_product=new Product();
                new_product.productName=name;
                new_product.productId=Integer.parseInt(dataSnapshot_from_firebase.getKey());
                new_product.uploadedDate=new_product.stringToDate(currentObject.get("uploaded_date").toString());
                new_product.price=Integer.parseInt(currentObject.get("price").toString());
                new_product.companyName=currentObject.get("company").toString();
                String tmp_str[]=currentObject.get("ingredient").toString().split(",");

                // If there's no category in Ingredient.java, error occurs
                for(String ing:tmp_str){
                    new_product.ingredients.add(Ingredient.valueOf(ing.toUpperCase()));
                }

                // Object log
                Log.d("Product Name", new_product.productName);
                Log.d("Product Id", String.valueOf(new_product.productId));
                Log.d("Product UploadedDate", String.valueOf(new_product.uploadedDate));
                Log.d("Product price", String.valueOf(new_product.price));;
                Log.d("Company Name", new_product.companyName);
                Log.d("Ingredients", String.valueOf(new_product.ingredients));

                // All products are saved into product_array_list
                product_array_list.add(new_product);
                adapter.addItem(ContextCompat.getDrawable(SearchActivity.this,R.drawable.app_icon),
                        name, price);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d("TAG", "onChildChanged:" + dataSnapshot.getKey());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        dbReference.addChildEventListener(childEventListener);

        // Search button
        Button button_search=(Button)findViewById(R.id.button_search);
        EditText edittext_search=(EditText)findViewById(R.id.search_string);
        button_search.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(edittext_search.getText()==null){
                    Log.d("edittext","dismissed");
                }else {
                    search_string = edittext_search.getText().toString();
                    Log.d("edittext", search_string);
                }
        }});

        // Sorting order button
        Button button_search_2=(Button)findViewById(R.id.button_search2);
        button_search_2.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(SearchActivity.this);
                dlg.setTitle("Sorting order");
                final String[] versionArray = new String[] {"Recent","High Price","Low Price", "High Rating"};

                dlg.setSingleChoiceItems(versionArray, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sorting_order=which;
                        Log.d("Sorting order",String.valueOf(sorting_order));
                    }
                });
                dlg.setPositiveButton("Select",new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        // Change button_search_2 text according to the selection
                        if(sorting_order==0){
                            button_search_2.setText("ORDER: RECENT");
                        }else if(sorting_order==1){
                            button_search_2.setText("ORDER: HIGH PRICE");
                            Query high_price_query=dbReference.orderByChild("price");

                            //ListViewAdapter adapter = (ListViewAdapter)listview.getAdapter();
                            ListViewAdapter adapter=new ListViewAdapter();
                            listview.setAdapter(adapter);

                            high_price_query.addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                    Map<String, Object> currentObject = (Map<String, Object>) dataSnapshot.getValue();

                                    Log.d("High: TAG", "onChildAdded:" + dataSnapshot_from_firebase.getKey());
                                    Log.d("High: Product price", currentObject.get("price").toString());

                                    String name=currentObject.get("name").toString();
                                    String price=currentObject.get("price").toString();

                                    adapter.addItem(ContextCompat.getDrawable(SearchActivity.this,R.drawable.app_icon),
                                            name, price);
                                    adapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                }

                                @Override
                                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                                }

                                @Override
                                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }else if(sorting_order==2){
                            button_search_2.setText("ORDER: LOW PRICE");
                        }else if(sorting_order==3){
                            button_search_2.setText("ORDER: HIGH RATING");
                        }
                        Toast.makeText(SearchActivity.this,"Sorting order changed", Toast.LENGTH_SHORT).show();
                    }
                });
                dlg.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
    }

}

class ListViewItem {
    private Drawable iconDrawable;
    private String titleStr;
    private String descStr;

    public void setIcon(Drawable icon) {
        iconDrawable = icon;
    }

    public void setTitle(String title) {
        titleStr = title;
    }

    public void setDesc(String desc) {
        descStr = desc;
    }

    public Drawable getIcon() {
        return this.iconDrawable;
    }

    public String getTitle() {
        return this.titleStr;
    }

    public String getDesc() {
        return this.descStr;
    }
}

class ListViewAdapter extends BaseAdapter {
    private ArrayList<ListViewItem> listViewItemList = new ArrayList<ListViewItem>() ;

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

        iconImageView.setImageDrawable(listViewItem.getIcon());
        titleTextView.setText(listViewItem.getTitle());
        descTextView.setText(listViewItem.getDesc());

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

    public void addItem(Drawable icon, String title, String desc) {
        ListViewItem item = new ListViewItem();

        item.setIcon(icon);
        item.setTitle(title);
        item.setDesc(desc);

        listViewItemList.add(item);
    }
}
