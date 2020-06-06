package team6.skku_fooding.activities;

import android.app.AlertDialog;
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

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Map;

import team6.skku_fooding.R;
import team6.skku_fooding.models.Ingredient;
import team6.skku_fooding.models.Product;

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

    private DatabaseReference dbReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Listview
        adapter = new ListViewAdapter() ;

        listview = (ListView) findViewById(R.id._listView);
        listview.setAdapter(adapter);

        // Firebase data load
        dbReference= FirebaseDatabase.getInstance().getReference().child("product");
        Query query=dbReference.orderByChild("uploaded_date");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Map<String, Object> currentObject = (Map<String, Object>) dataSnapshot.getValue();
                String name=currentObject.get("name").toString();
                String price=currentObject.get("price").toString();

                adapter.addItem(ContextCompat.getDrawable(SearchActivity.this,R.drawable.app_icon),
                        name, price);
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
        Button button_search=(Button)findViewById(R.id.button_search);
        EditText edittext_search=(EditText)findViewById(R.id.search_string);
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
                        sortByOrder(sorting_order);
                        Toast.makeText(SearchActivity.this,"Sorting order changed", Toast.LENGTH_SHORT).show();
                    }
                });
                dlg.show();
            }
        });

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
            }
        });
        delivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("delivery","I'm here");
            }
        });
        mypage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("mypage","I'm here");
            }
        });
    }

    @Override
    public void onBackPressed() {
    }

    void sortByOrder(Integer sorting_order){
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

                if(sorting_order==1) {
                    adapter.addItemIndex(0,ContextCompat.getDrawable(SearchActivity.this, R.drawable.app_icon),
                            name, price);
                    adapter.notifyDataSetChanged();
                }
                else{
                    adapter.addItem(ContextCompat.getDrawable(SearchActivity.this, R.drawable.app_icon),
                            name, price);
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
                tmp.addListViewItem(adapter.listViewItemList.get((i)));
            }
        }
        listview.setAdapter(tmp);
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

        iconImageView.setImageDrawable(listViewItem.getIcon());
        titleTextView.setText(listViewItem.getTitle());
        descTextView.setText(listViewItem.getDesc());

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

    public void addItem(Drawable icon, String title, String desc) {
        ListViewItem item = new ListViewItem();

        item.setIcon(icon);
        item.setTitle(title);
        item.setDesc(desc);

        listViewItemList.add(item);
    }
    public void addItemIndex(Integer index, Drawable icon, String title, String desc){
        ListViewItem item = new ListViewItem();

        item.setIcon(icon);
        item.setTitle(title);
        item.setDesc(desc);

        listViewItemList.add(index, item);
    }
    public void addListViewItem(ListViewItem item){
        listViewItemList.add(item);
    }
}
