package team6.skku_fooding.activities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class ShoppingAdapter extends ArrayAdapter<String> {

    ArrayList<String>images;
    ArrayList<String>names;
    ArrayList<String>amount;
    ArrayList<String>prices;
    Context c;
    LayoutInflater inflater;
    ArrayList<Integer> clicked;

    ArrayList<String>selectednames=new ArrayList<String>();
    ArrayList<String>selectedamount=new ArrayList<String>();;
    ArrayList<String>selectedprices=new ArrayList<String>();;




    public ShoppingAdapter( Context context, ArrayList<String>images,ArrayList<String>names,ArrayList<String>amount,ArrayList<Integer> clicked,ArrayList<String>prices){

        super(context,0,names);
        this.c=context;
        this.names=names;
        this.amount=amount;
        this.images=images;
        this.clicked=clicked;
        this.prices=prices;



    }
    public String  Retrieveamount(int position){

        return amount.get(position);

    }
    public int  ClickedInfo(int position){

        return clicked.get(position);

    }
    public void setClickedInfo(int position,int clickinfo){
        clicked.set(position,clickinfo);
    }

   /* public ArrayList<String> amountlist(){
        return amount;
    }
    public ArrayList<String> priceslist(){
        return prices;
    }
    public ArrayList<String> nameslist(){
        return names;
    }*/

    public ArrayList<String> selectedamountlist(){
        return selectedamount;
    }
    public ArrayList<String> selectedpriceslist(){
        return selectedprices;
    }
    public ArrayList<String> selectednameslist(){
        return selectednames;
    }


    public class ViewHolder{
    ImageView img;
     TextView amount;
     TextView  title;
     TextView prices;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){




        if(convertView==null) {
            inflater=(LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.shopping,null);
        }
        final ViewHolder holder=new ViewHolder();
        Button b1 = (Button) convertView.findViewById(R.id.selectbutton);

        b1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {


                if(clicked.get(position)==0) {
                    parent.getChildAt(position).setBackgroundColor(Color.rgb(0, 178, 178));
                    clicked.set(position, 1);
                    Toast.makeText(getContext(),"AddedtoList",Toast.LENGTH_SHORT).show();
                    System.out.println(amount.get(position));
                    System.out.println(prices.get(position));
                    System.out.println(names.get(position));
                    selectedamount.add(amount.get(position).toString());
                    selectedprices.add(prices.get(position).toString());
                    selectednames.add(names.get(position).toString());


                }else{
                    parent.getChildAt(position).setBackgroundColor(Color.rgb(255, 255, 255));
                    clicked.set(position, 0);
                    Toast.makeText(getContext(),"RemovedfromList",Toast.LENGTH_SHORT).show();

                    selectedamount.remove(amount.get(position));
                    selectedprices.remove(prices.get(position));
                    selectednames.remove(names.get(position));
                }
                ShoppingAdapter.this.notifyDataSetChanged();

            }
        });
        Button b2 = (Button) convertView.findViewById(R.id.removebutton);

        b2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                images.remove(position);
                names.remove(position);
                amount.remove(position);
                clicked.remove(position);
                prices.remove(position);
                selectedamount.remove(amount.get(position));
                selectednames.remove(names.get(position));
                selectedprices.remove(prices.get(position));
                ShoppingAdapter.this.notifyDataSetChanged();

            }
        });

       holder.title=(TextView)convertView.findViewById(R.id.titleshop);
        holder.prices=(TextView)convertView.findViewById(R.id.prices);
       holder.amount=(TextView)convertView.findViewById(R.id.amount);
       holder.img=(ImageView)convertView.findViewById(R.id.imgshop);
        byte[]imageBytes = Base64.decode(images.get(position), Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        holder.img.setImageBitmap(decodedImage);
        holder.title.setText(names.get(position));
        holder.amount.setText(amount.get(position));
        holder.prices.setText(prices.get(position));


        return  convertView;
    }
}
