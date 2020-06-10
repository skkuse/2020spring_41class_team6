package team6.skku_fooding.activities;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

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
    Context c;
    LayoutInflater inflater;


    public ReviewListAdapter( Context context, ArrayList<String>userid,ArrayList<String>title,ArrayList<String>modifieddate,ArrayList<String> score,ArrayList<String> description){

        super(context,0,userid);
        this.c=context;
        this.userid=userid;
        this.title=title;
        this.modifieddate=modifieddate;
        this.score=score;
        this.description=description;


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
