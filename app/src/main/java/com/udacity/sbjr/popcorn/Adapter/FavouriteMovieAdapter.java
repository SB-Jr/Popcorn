package com.udacity.sbjr.popcorn.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.udacity.sbjr.popcorn.Database.FavouriteMovieDBHelper;
import com.udacity.sbjr.popcorn.POJO.Movie;
import com.udacity.sbjr.popcorn.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sbjr on 3/11/16.
 */
public class FavouriteMovieAdapter extends ArrayAdapter<Movie> {

    Context context = null;
    ArrayList<Movie> favMovie=null;

    public FavouriteMovieAdapter(Context context,int resId, ArrayList<Movie> data){
        super(context,0,data);
        favMovie = data;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if(v==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.moviegrid,null);
        }

        TextView title = (TextView) v.findViewById(R.id.movie_title);
        TextView rating = (TextView) v.findViewById(R.id.movie_popularity);
        ImageView poster = (ImageView) v.findViewById(R.id.movie_image);

        title.setText(favMovie.get(position).getTitle());
        rating.setText(favMovie.get(position).getRating());
        poster.setImageBitmap(favMovie.get(position).getPoster());

        return v;
    }
}
