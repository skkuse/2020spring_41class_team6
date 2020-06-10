package team6.skku_fooding.activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import team6.skku_fooding.R;

public class CustomListAdapter_delivery extends ArrayAdapter<custom_list_item_delivery> implements View.OnClickListener {

    public interface ListBtnClickListener{
        void onListBtnClick (int position);
    }
    private Context mContext;
    int mResource;
    private ListBtnClickListener listBtnClickListener;


    public CustomListAdapter_delivery(@NonNull Context context, int resource, @NonNull ArrayList<custom_list_item_delivery> objects, Context mContext,ListBtnClickListener clickListener) {
        super(context, resource, objects);
        this.mContext = mContext;
        mResource = resource;
        this.listBtnClickListener =clickListener;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final int pos = position;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource,parent,false);
        final custom_list_item_delivery listViewItem = (custom_list_item_delivery) getItem(position);

        String order_id = getItem(position).getOrder_id();
        String order_date = getItem(position).getDate();
        String order_status = getItem(position).getStatus();
        String order_product_name = getItem(position).getProduct_name();
        String order_product_id =getItem(position).getProduct_id();


        TextView txId =(TextView) convertView.findViewById(R.id.order_id_delivery);
        TextView txDate = (TextView)convertView.findViewById(R.id.order_date_delivery);
        TextView txStatus = (TextView)convertView.findViewById(R.id.order_status_delivery);
        TextView txProductname = (TextView)convertView.findViewById(R.id.order_product_delivery);
        TextView txProductId = (TextView)convertView.findViewById(R.id.product_id_delivery);
        Button txButton = (Button) convertView.findViewById(R.id.order_review_delivery);

        txId.setText("Order ID: "+order_id);
        txDate.setText(order_date);
        txStatus.setText(order_status);
        txProductname.setText(order_product_name);
        txProductId.setText("Product ID: "+order_product_id);
        //txButton.setOnClickListener(getItem(position).OnClickListener);

        txButton.setTag(position);
        txButton.setOnClickListener(this);
        return convertView;

    }
    public void onClick(View v) {
        // ListBtnClickListener(MainActivity)의 onListBtnClick() 함수 호출.
        if (this.listBtnClickListener != null) {
            this.listBtnClickListener.onListBtnClick((int)v.getTag()) ;
        }
    }

}

