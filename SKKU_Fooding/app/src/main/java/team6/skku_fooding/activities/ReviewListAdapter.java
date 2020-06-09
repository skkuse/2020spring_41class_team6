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
/*
public class ReviewListAdapter extends ArrayAdapter<Review> {



    public ReviewListAdapter( Context context, ArrayList<Review>mReviewlist){

        super(context,R.layout.reviewlist,mReviewlist);

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        System.out.println("llllllllllllllllll");

        Review review=getItem(position);

        if(convertView==null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.reviewlist, parent, false);
        }


            TextView userid = (TextView) convertView.findViewById(R.id.userid);

            TextView description = (TextView) convertView.findViewById(R.id.description);
            TextView modifiedDate = (TextView) convertView.findViewById(R.id.modifieddate);
            TextView title = (TextView) convertView.findViewById(R.id.title);
            TextView score = (TextView) convertView.findViewById(R.id.score);
            System.out.println(review.title);
            userid.setText(review.reviewId);

            description.setText(review.description);
            modifiedDate.setText(review.modifiedDate.toString());
            title.setText(review.title);
            score.setText(review.score);


        return  convertView;
    }
}*/
