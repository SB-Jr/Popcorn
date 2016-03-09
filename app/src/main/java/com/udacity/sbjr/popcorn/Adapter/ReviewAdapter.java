package com.udacity.sbjr.popcorn.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.udacity.sbjr.popcorn.R;

import java.util.List;

/**
 * Created by sbjr on 3/9/16.
 */
public class ReviewAdapter extends ArrayAdapter<String[]> {


    Context context=null;
    List<String[]> reviews = null;
    public ReviewAdapter(Context context,int resId,List<String[]> data){
        super(context,0,data);
        this.context = context;
        this.reviews = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if(v==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.review_list,null);
        }

        String data[] = reviews.get(position);

        TextView author = (TextView) v.findViewById(R.id.review_list_author);
        TextView content = (TextView) v.findViewById(R.id.review_list_review);

        author.setText(data[0]);
        content.setText(data[1]);

        return v;
    }
}
