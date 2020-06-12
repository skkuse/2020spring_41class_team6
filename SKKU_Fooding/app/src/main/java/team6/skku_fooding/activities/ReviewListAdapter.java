package team6.skku_fooding.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;


import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import team6.skku_fooding.R;

public class ReviewListAdapter extends ArrayAdapter<String> {

    ArrayList<String>userid;
    ArrayList<String>title;
    ArrayList<String>modifieddate;
    ArrayList<String> score;
    ArrayList<String> description;
    ArrayList<ArrayList<Bitmap>>images;
    Context c;
    LayoutInflater inflater;


    public ReviewListAdapter( Context context, ArrayList<String>userid,ArrayList<String>title,ArrayList<String>modifieddate,ArrayList<String> score,ArrayList<String> description,ArrayList<ArrayList<Bitmap>>images){

        super(context,0,userid);
        this.c=context;
        this.userid=userid;
        this.title=title;
        this.modifieddate=modifieddate;
        this.score=score;
        this.description=description;
        this.images=images;


    }
    public class ViewHolder{
        TextView Userid;
        TextView title;
        TextView  modifieddate;
        TextView  score;
        TextView  description;
        ImageView  img1;
        ImageView   img2;
        ImageView  img3;
        ImageView  img4;
        ImageView  img5;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){


        final ViewHolder holder=new ViewHolder();

        if(convertView==null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.reviewlist, parent, false);
        }
      /*  Button pics=(Button)convertView.findViewById(R.id.pics);

        pics.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(getContext(), Imagepage.class);
                intent.putParcelableArrayListExtra("imgarray",images.get(position));
                getContext().startActivity(intent);

            }



        });*/



        holder.img1=(ImageView) convertView.findViewById(R.id.img1);
        holder.img2=(ImageView) convertView.findViewById(R.id.img2);
        holder.img3=(ImageView) convertView.findViewById(R.id.img3);
        holder.img4=(ImageView) convertView.findViewById(R.id.img4);
        holder.img5=(ImageView) convertView.findViewById(R.id.img5);

            holder.Userid = (TextView) convertView.findViewById(R.id.userid);

            holder.description = (TextView) convertView.findViewById(R.id.description);
            holder.modifieddate = (TextView) convertView.findViewById(R.id.modifieddate);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.score = (TextView) convertView.findViewById(R.id.score);

            holder.Userid.setText(userid.get(position));

            holder.description.setText(description.get(position));
            holder.modifieddate.setText(modifieddate.get(position));
            holder.title.setText(title.get(position));
            holder.score.setText(score.get(position));
            if(images.get(position).size()==1) {
                System.out.println("aaaaaaaaaaaaaaaaa");
                holder.img1.setImageBitmap(images.get(position).get(0));
            }else if(images.get(position).size()==2){
                System.out.println("bbbbbbbb");
                holder.img1.setImageBitmap(images.get(position).get(0));
                holder.img2.setImageBitmap(images.get(position).get(1));

            }else if(images.get(position).size()==3){
                holder.img1.setImageBitmap(images.get(position).get(0));
                holder.img2.setImageBitmap(images.get(position).get(1));
                holder.img3.setImageBitmap(images.get(position).get(2));

            }else if(images.get(position).size()==4){
                holder.img1.setImageBitmap(images.get(position).get(0));
                holder.img2.setImageBitmap(images.get(position).get(1));
                holder.img3.setImageBitmap(images.get(position).get(2));
                holder.img4.setImageBitmap(images.get(position).get(3));

            }else if(images.get(position).size()==5){
                holder.img1.setImageBitmap(images.get(position).get(0));
                holder.img2.setImageBitmap(images.get(position).get(1));
                holder.img3.setImageBitmap(images.get(position).get(2));
                holder.img4.setImageBitmap(images.get(position).get(3));
                holder.img5.setImageBitmap(images.get(position).get(4));

            }


        return  convertView;
    }
}
