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

import java.util.ArrayList;
import java.util.List;

import team6.skku_fooding.R;

public class CustomListAdapter_delivery extends ArrayAdapter<custom_list_item_delivery>{
    private static final String TAG = "CustomListAdapter_delivery";
    private Context mContext;
    int mResource;

    public CustomListAdapter_delivery(@NonNull Context context, int resource, @NonNull ArrayList<custom_list_item_delivery> objects, Context mContext) {
        super(context, resource, objects);
        this.mContext = mContext;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String order_id = getItem(position).getOrder_id();
        String order_date = getItem(position).getDate();
        String order_status = getItem(position).getStatus();
        String order_product_name = getItem(position).getProduct_name();


        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource,parent,false);
        TextView txId =(TextView) convertView.findViewById(R.id.order_id_delivery);
        TextView txDate = (TextView)convertView.findViewById(R.id.order_date_delivery);
        TextView txStatus = (TextView)convertView.findViewById(R.id.order_status_delivery);
        TextView txProductname = (TextView)convertView.findViewById(R.id.order_product_delivery);

        txId.setText(order_id);
        txDate.setText(order_date);
        txStatus.setText(order_status);
        txProductname.setText(order_product_name);
        return convertView;

    }
}

