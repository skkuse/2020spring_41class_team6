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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import team6.skku_fooding.R;
import java.util.ArrayList;
import java.util.List;

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


        return  convertView;
    }
}
