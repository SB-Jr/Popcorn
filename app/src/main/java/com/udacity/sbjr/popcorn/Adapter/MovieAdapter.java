package com.udacity.sbjr.popcorn.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.sbjr.popcorn.POJO.Movie;
import com.udacity.sbjr.popcorn.R;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by sbjr on 2/2/16.
 */
public class MovieAdapter extends ArrayAdapter<Movie> {

    List<Movie> movieList;

    public MovieAdapter(Context context,int resId,List<Movie> data){
        super(context,0,data);
        this.movieList = data;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Movie movie = movieList.get(position);

        View v = convertView;

        if(v==null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.moviegrid,null);
        }

        TextView title = (TextView) v.findViewById(R.id.movie_title);
        TextView poplarity = (TextView) v.findViewById(R.id.movie_popularity);
        title.setText(movie.getTitle());
        poplarity.setText(movie.getRating());

        ImageView poster = (ImageView) v.findViewById(R.id.movie_image);
        Picasso.with(getContext()).load("http://image.tmdb.org/t/p/w342"+movie.getPosterPath()).into(poster);

        return v;
    }
}
