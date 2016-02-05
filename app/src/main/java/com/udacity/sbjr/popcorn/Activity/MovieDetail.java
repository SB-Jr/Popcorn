package com.udacity.sbjr.popcorn.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.sbjr.popcorn.POJO.Constants;
import com.udacity.sbjr.popcorn.POJO.Movie;
import com.udacity.sbjr.popcorn.R;

public class MovieDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.movie_detail);

        Intent intent = getIntent();
        Movie movie = intent.getExtras().getParcelable(Constants.MOVIE_PARCELABLE);

        TextView title = (TextView) findViewById(R.id.movie_title);
        TextView yor = (TextView) findViewById(R.id.movie_yor);
        TextView rating = (TextView) findViewById(R.id.movie_rating);
        TextView synopsis = (TextView) findViewById(R.id.movie_synopsis);
        ImageView poster = (ImageView) findViewById(R.id.movie_poster);

        title.setText(movie.getTitle());
        yor.setText("Release Date:"+movie.getReleaseDate());
        rating.setText("Rating:"+movie.getRating());
        synopsis.setText("Synopsis:"+movie.getSynopsis());

        Picasso.with(getApplicationContext()).load("http://image.tmdb.org/t/p/w342" + movie.getPosterPath()).into(poster);
    }
}
