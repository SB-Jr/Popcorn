package com.udacity.sbjr.popcorn.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.udacity.sbjr.popcorn.POJO.Constants;
import com.udacity.sbjr.popcorn.POJO.Movie;
import com.udacity.sbjr.popcorn.R;

public class MovieDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detai_container_activity_layout);
        MovieDetail movieDetail = new MovieDetail();
        if(savedInstanceState==null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.movie_detail_container, movieDetail).commit();
        }
        Intent intent = getIntent();
        Movie movie = intent.getExtras().getParcelable(Constants.MOVIE_PARCELABLE);
        String activityName = intent.getStringExtra(Constants.ActivityName);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.MOVIE_PARCELABLE,movie);
        bundle.putString(Constants.ActivityName, activityName);
        bundle.putInt(Constants.sNUMPANES,1);
        movieDetail.setArguments(bundle);
    }

}
