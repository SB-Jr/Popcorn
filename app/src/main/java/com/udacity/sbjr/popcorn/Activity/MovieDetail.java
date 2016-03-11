package com.udacity.sbjr.popcorn.Activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.udacity.sbjr.popcorn.Adapter.ReviewAdapter;
import com.udacity.sbjr.popcorn.Adapter.TrailerAdapter;
import com.udacity.sbjr.popcorn.Database.FavouriteMovieDBHelper;
import com.udacity.sbjr.popcorn.POJO.Constants;
import com.udacity.sbjr.popcorn.POJO.Movie;
import com.udacity.sbjr.popcorn.POJO.MovieListBuilder;
import com.udacity.sbjr.popcorn.R;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MovieDetail extends AppCompatActivity {


    ListView trailers = null;
    ListView reviews = null;
    List<String> trailerList = null;
    ArrayList<String[]> reviewList = null;
    String mId = null;
    String mTitle = null;
    String mYor = null;
    String mRating = null;
    String mSynopsis = null;
    Bitmap mPoster = null;
    TrailerAdapter trailerAdapter = null;
    ReviewAdapter reviewAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.movie_detail);

        if(savedInstanceState==null){
            getSupportFragmentManager().beginTransaction().add(R.id.movie_detail_layout,).commit();
        }

        Intent intent = getIntent();
        Movie movie = intent.getExtras().getParcelable(Constants.MOVIE_PARCELABLE);

        trailers = (ListView) findViewById(R.id.movie_trailer);
        reviews = (ListView) findViewById(R.id.movie_review);

        TextView title = (TextView) findViewById(R.id.movie_title);
        TextView yor = (TextView) findViewById(R.id.movie_yor);
        TextView rating = (TextView) findViewById(R.id.movie_rating);
        TextView synopsis = (TextView) findViewById(R.id.movie_synopsis);
        ImageView poster = (ImageView) findViewById(R.id.movie_poster);

        mId = movie.getId();
        mTitle = movie.getTitle();
        mRating = movie.getRating();
        mSynopsis = movie.getSynopsis();
        mYor = movie.getReleaseDate();

        title.setText(movie.getTitle());
        yor.setText("Release Date:"+movie.getReleaseDate());
        rating.setText("Rating:" + movie.getRating());
        synopsis.setText("Synopsis:" + movie.getSynopsis());

        getSupportActionBar().setTitle(movie.getTitle());



        String activityName = intent.getStringExtra(Constants.ActivityName);
        if(activityName.compareTo(MainActivity.sActivityName)==0) {
        /*
        * this part is for setting the adapters for review and trailer list only if called by the main activity
        * */
            Picasso.with(getApplicationContext()).load("http://image.tmdb.org/t/p/w342" + movie.getPosterPath()).into(poster);
            trailerList = new ArrayList<>();
            new TrailerLoader().execute();
            trailerAdapter = new TrailerAdapter(getApplicationContext(), R.layout.trailer_list, trailerList);
            trailerAdapter.setNotifyOnChange(true);
            trailers.setAdapter(trailerAdapter);
            trailers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent  = new Intent(Intent.ACTION_VIEW, Uri.parse(trailerList.get(position)));
                    startActivity(intent);
                }
            });


            reviewList = new ArrayList<>();
            new ReviewLoader().execute();
            reviewAdapter = new ReviewAdapter(getApplicationContext(), R.layout.review_list, reviewList);
            reviewAdapter.setNotifyOnChange(true);
            reviews.setAdapter(reviewAdapter);
        }
        else{
            poster.setImageBitmap(movie.getPoster());
        }
        /*
        * this part uses the Palette library to get the most used color out of the
        * poster and applies it to the action bar of the activity
        * It first extracts the bitmap from the poster image view and then
        * passes it to the library to get the color in #RGB form
        */
        Palette.PaletteAsyncListener paletteAsyncListener = new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                int primary=0xB0ABA0;
                int darkprimary = 0x181712;
                int maxColor = palette.getMutedColor(primary);
                //int darkvibrant = palette.getDarkVibrantColor(darkprimary);

                ColorDrawable cd= new ColorDrawable(maxColor);
                getSupportActionBar().setBackgroundDrawable(cd);

                Window window = getWindow();
                window.setStatusBarColor(maxColor-0x0A0A0A);

            }
        };
        Bitmap bitmap = ((BitmapDrawable) poster.getDrawable()).getBitmap();
        mPoster = bitmap;
        Palette.from(bitmap).generate(paletteAsyncListener);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putStringArrayList();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id){
            case R.id.movie_detail_favourite:
                int res = addMovieToFavourite();
                if(res == 1)
                    Toast.makeText(getApplicationContext(),mTitle+" added to favourite list",Toast.LENGTH_LONG).show();
                return true;

            case R.id.movie_detail_share:
                shareMovie();
                return true;

            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_movie_detail,menu);
        return super.onCreateOptionsMenu(menu);
    }

    /*
            * this runs in background to populate the trailer list view
            * */
    class TrailerLoader extends AsyncTask<Void,Void,Void>{

        String trailerJson = null;

        @Override
        protected Void doInBackground(Void... params) {

            String trailerUrl = "http://api.themoviedb.org/3/movie/"+ mId +"/videos?api_key="+Constants.mApiKey;

            try{
                trailerJson = getJson(trailerUrl);
            }catch (MalformedURLException e){
                Toast.makeText(getApplicationContext(), "URL Error Occured..Cant fetch Trailer", Toast.LENGTH_LONG).show();
            }
            catch (IOException e){
                Toast.makeText(getApplicationContext(), "Error Occured..Cant fetch Trailer", Toast.LENGTH_LONG).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                trailerList.addAll(new MovieListBuilder(trailerJson).buildTrailerList());
                trailerAdapter.notifyDataSetChanged();
            }
            catch (JSONException e){
                Toast.makeText(getApplicationContext(), "Error Occured..Cant fetch Review", Toast.LENGTH_LONG).show();
            }
        }
    }

    /*
    * this runs in background to populate the review list view
    * */
    class ReviewLoader extends AsyncTask<Void,Void,Void>{

        String reviewJson = null;

        @Override
        protected Void doInBackground(Void... params) {

            String reviewUrl = "http://api.themoviedb.org/3/movie/"+ mId +"/reviews?api_key="+Constants.mApiKey;
            try{
                reviewJson = getJson(reviewUrl);
            }catch (MalformedURLException e){
                Toast.makeText(getApplicationContext(), "URL Error Occured..Cant fetch Review", Toast.LENGTH_LONG).show();
            }
            catch (IOException e){
                Toast.makeText(getApplicationContext(), "Error Occured..Cant fetch Review", Toast.LENGTH_LONG).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                reviewList.addAll(new MovieListBuilder(reviewJson).buildReviewList());
                reviewAdapter.notifyDataSetChanged();
            }
            catch (JSONException e){
                Toast.makeText(getApplicationContext(), "Error Occured..Cant fetch Review", Toast.LENGTH_LONG).show();
            }
        }
    }

    /*
     *this method establishes a url connection to fetch the desired json and returns it to the calling function
     */
    public String getJson(String ApiUrl) throws MalformedURLException, IOException {
        String json = "";

        URL url = new URL(ApiUrl);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setDoInput(true);
        urlConnection.connect();
        InputStream inputStream = urlConnection.getInputStream();

        if(inputStream==null){
            return null;
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

        String s;
        while((s=br.readLine())!=null){
            json = json + s;
        }

        if(json.length()==0){
            return null;
        }
        return json;
    }


    /*
    * share the first trailer url with the help of share intent
    * */
    public void shareMovie(){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, trailerList.get(0));
        intent.setType("text/plain");
        startActivity(intent);
    }

    /*
    * adds the movie title, id, synopsis, rating, poster to the favourite database
    * */
    public int addMovieToFavourite(){
        FavouriteMovieDBHelper dbHelper = new FavouriteMovieDBHelper(getApplicationContext());
        return dbHelper.addMovie(mId,mTitle,mRating,mYor,mSynopsis,mPoster);
    }


}
