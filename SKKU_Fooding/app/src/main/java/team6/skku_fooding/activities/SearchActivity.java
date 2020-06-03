package team6.skku_fooding.activities;

import android.content.Context;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Map;

import team6.skku_fooding.R;

public class SearchActivity extends AppCompatActivity {

    String search_string;
    Button search_button;
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


        // Firebase data load
        dbReference= FirebaseDatabase.getInstance().getReference().child("product");
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d("TAG", "onChildAdded:" + dataSnapshot.getKey());

                Map<String, Object> currentObject = (Map<String, Object>) dataSnapshot.getValue();
                String name=currentObject.get("name").toString();
                String price=currentObject.get("price").toString();

                adapter.addItem(ContextCompat.getDrawable(SearchActivity.this,R.drawable.app_icon),
                        name, price);

                /*
                for(String child: user.keySet()) {
                    Map<String, Object> currentObject = (Map<String, Object>) user.get(child);
                    String name=currentObject.get("name").toString();
                    String price=currentObject.get("price").toString();

                    adapter.addItem(ContextCompat.getDrawable(SearchActivity.this,R.drawable.app_icon),
                            name, price);
                }*/
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
                search_string=edittext_search.getText().toString();
                Log.d("edittext", search_string);
        }});
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