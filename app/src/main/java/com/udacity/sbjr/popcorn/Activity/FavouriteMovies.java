package com.udacity.sbjr.popcorn.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.udacity.sbjr.popcorn.Adapter.FavouriteMovieAdapter;
import com.udacity.sbjr.popcorn.Database.FavouriteMovieDBHelper;
import com.udacity.sbjr.popcorn.POJO.Constants;
import com.udacity.sbjr.popcorn.POJO.Movie;
import com.udacity.sbjr.popcorn.R;

import java.util.ArrayList;

public class FavouriteMovies extends AppCompatActivity {


    public static final String sActivityName="FavouriteMovies";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_movies);

        FavouriteMovieDBHelper dbHelper = new FavouriteMovieDBHelper(getApplicationContext());
        final ArrayList<Movie> favMovie = dbHelper.getFavouriteMovies();

        GridView grid = (GridView) findViewById(R.id.favourite_movie_grid);
        FavouriteMovieAdapter adapter = new FavouriteMovieAdapter(getApplicationContext(),R.layout.moviegrid,favMovie);
        adapter.setNotifyOnChange(true);
        grid.setAdapter(adapter);

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(),MovieDetail.class);
                intent.putExtra(Constants.MOVIE_PARCELABLE, favMovie.get(position));
                intent.putExtra(Constants.ActivityName,sActivityName);
                startActivity(intent);
            }
        });
    }
}
