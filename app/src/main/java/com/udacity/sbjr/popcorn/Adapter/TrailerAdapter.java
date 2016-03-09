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
public class TrailerAdapter extends ArrayAdapter<String> {

    List<String> trailerList = null;
    Context context = null;

    public TrailerAdapter(Context context,int resId,List<String> data){
        super(context,0,data);
        this.trailerList = data;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if(v==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.trailer_list,null);
        }
        TextView tv = (TextView) v.findViewById(R.id.trailer_list_text);
        tv.setText("Trailer "+(position+1));

        return v;
    }
}
