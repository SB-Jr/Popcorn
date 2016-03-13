package com.udacity.sbjr.popcorn.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.udacity.sbjr.popcorn.POJO.Constants;
import com.udacity.sbjr.popcorn.POJO.Movie;
import com.udacity.sbjr.popcorn.R;

public class MainActivity extends AppCompatActivity implements MovieGrid.onMovieSelectedListener, FavouriteMovies.FavouriteMovieInterface {

    boolean mTwoPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        onOrderChange("popularity.desc");

        if(findViewById(R.id.movie_detail_container)!=null){
            mTwoPane = true;
            if(savedInstanceState==null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.movie_detail_container, new MovieDetail()).commit();
            }
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public void onMovieSelected(Movie movie) {
        if(mTwoPane==true){
            FrameLayout frameLayout = (FrameLayout) findViewById(R.id.movie_detail_container);
            frameLayout.removeAllViews();
            MovieDetail movieDetail = new MovieDetail();
            getSupportFragmentManager().beginTransaction().replace(R.id.movie_detail_container, movieDetail).commit();
            Bundle bundle = new Bundle();
            bundle.putParcelable(Constants.MOVIE_PARCELABLE,movie);
            bundle.putString(Constants.ActivityName, MovieGrid.sActivityName);
            bundle.putInt(Constants.sNUMPANES,2);
            movieDetail.setArguments(bundle);
        }
        else {
            Intent intent = new Intent(getBaseContext(),MovieDetailActivity.class);
            intent.putExtra(Constants.MOVIE_PARCELABLE,movie);
            intent.putExtra(Constants.ActivityName,MovieGrid.sActivityName);
            startActivity(intent);
        }
    }

    @Override
    public void onFavouriteMovieSelected(Movie movie) {
        if(mTwoPane==true){
            FrameLayout frameLayout = (FrameLayout) findViewById(R.id.movie_detail_container);
            frameLayout.removeAllViews();
            MovieDetail movieDetail = new MovieDetail();
            getSupportFragmentManager().beginTransaction().replace(R.id.movie_detail_container, movieDetail).commit();
            Bundle bundle = new Bundle();
            bundle.putParcelable(Constants.MOVIE_PARCELABLE,movie);
            bundle.putString(Constants.ActivityName, FavouriteMovies.sActivityName);
            bundle.putInt(Constants.sNUMPANES,2);
            movieDetail.setArguments(bundle);
        }
        else {
            Intent intent = new Intent(getApplicationContext(),MovieDetailActivity.class);
            intent.putExtra(Constants.MOVIE_PARCELABLE,movie);
            intent.putExtra(Constants.ActivityName,FavouriteMovies.sActivityName);
            startActivity(intent);
        }
    }

    @Override
    public void onOrderChange(String order) {
        if(mTwoPane){
            FrameLayout frameLayout = (FrameLayout) findViewById(R.id.movie_detail_container);
            frameLayout.removeAllViews();
        }
        FrameLayout frameLayout2 = (FrameLayout) findViewById(R.id.movie_grid_container);
        frameLayout2.removeAllViews();
        MovieGrid movieGrid = new MovieGrid();
        getSupportFragmentManager().beginTransaction().replace(R.id.movie_grid_container,movieGrid).commit();
        Bundle bundle = new Bundle();
        bundle.putString(MovieGrid.sOrderType,order);
        movieGrid.setArguments(bundle);
    }

    @Override
    public void onMovieFavourite() {
        if(mTwoPane){
            FrameLayout frameLayout = (FrameLayout) findViewById(R.id.movie_detail_container);
            frameLayout.removeAllViews();
        }
        FrameLayout frameLayout2 = (FrameLayout) findViewById(R.id.movie_grid_container);
        frameLayout2.removeAllViews();
        FavouriteMovies favouriteMovies = new FavouriteMovies();
        getSupportFragmentManager().beginTransaction().replace(R.id.movie_grid_container,favouriteMovies).commit();
    }
}
