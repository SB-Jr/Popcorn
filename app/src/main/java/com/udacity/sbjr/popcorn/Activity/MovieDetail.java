package com.udacity.sbjr.popcorn.Activity;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.udacity.sbjr.popcorn.Adapter.ReviewAdapter;
import com.udacity.sbjr.popcorn.Adapter.TrailerAdapter;
import com.udacity.sbjr.popcorn.Database.FavouriteMovieContract;
import com.udacity.sbjr.popcorn.Database.FavouriteMovieDBHelper;
import com.udacity.sbjr.popcorn.POJO.Constants;
import com.udacity.sbjr.popcorn.POJO.Movie;
import com.udacity.sbjr.popcorn.POJO.MovieListBuilder;
import com.udacity.sbjr.popcorn.R;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MovieDetail extends android.support.v4.app.Fragment {


    private final static String mMOVIEKEY="MOVIEKEY";
    private final static String mPOSTERKEY="POSTERKEY";
    private final static String mTRAILERKEY="TRAILERKEY";

    public final static String PROVIDER_NAME="com.udacity.sbjr.popcorn.Database.FavouriteMovieContentProvider";
    public final static Uri CONTENT_URI = Uri.parse("content://"+PROVIDER_NAME+"/movies");


    ListView trailers = null;
    ListView reviews = null;
    ArrayList<String> trailerList = null;
    ArrayList<String[]> reviewList = null;
    String mId = null;
    String mTitle = null;
    String mYor = null;
    String mRating = null;
    String mSynopsis = null;
    Bitmap mPoster = null;
    TrailerAdapter trailerAdapter = null;
    ReviewAdapter reviewAdapter = null;
    AppCompatActivity mParentActivity=null;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState!=null){
            trailerList = savedInstanceState.getStringArrayList(mTRAILERKEY);
            byte blob[] = savedInstanceState.getByteArray(mPOSTERKEY);
            mPoster = BitmapFactory.decodeByteArray(blob,0,blob.length);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.movie_detail,container,false);
    }

    @Override
    public void onStart() {
        super.onStart();

        View v = getView();

        Bundle bundle = getArguments();
        if(bundle!=null) {
            Movie movie = bundle.getParcelable(Constants.MOVIE_PARCELABLE);

            if(bundle.getInt(Constants.sNUMPANES)==1){
                mParentActivity = (MovieDetailActivity) getActivity();
            }
            else {
                mParentActivity = (MainActivity) getActivity();
            }

            trailers = (ListView) v.findViewById(R.id.movie_trailer);
            reviews = (ListView) v.findViewById(R.id.movie_review);

            TextView title = (TextView) v.findViewById(R.id.movie_title);
            TextView yor = (TextView) v.findViewById(R.id.movie_yor);
            TextView rating = (TextView) v.findViewById(R.id.movie_rating);
            TextView synopsis = (TextView) v.findViewById(R.id.movie_synopsis);
            ImageView poster = (ImageView) v.findViewById(R.id.movie_poster);

            mId = movie.getId();
            mTitle = movie.getTitle();
            mRating = movie.getRating();
            mSynopsis = movie.getSynopsis();
            mYor = movie.getReleaseDate();

            title.setText(movie.getTitle());
            yor.setText("Release Date:" + movie.getReleaseDate());
            rating.setText("Rating:" + movie.getRating());
            synopsis.setText("Synopsis:" + movie.getSynopsis());

            mParentActivity.getSupportActionBar().setTitle(movie.getTitle());


            String activityName = bundle.getString(Constants.ActivityName);
            if (activityName.compareTo(MovieGrid.sActivityName) == 0) {
        /*
        * this part is for setting the adapters for review and trailer list only if called by the main activity
        * */
                Picasso.with(getActivity().getApplicationContext()).load("http://image.tmdb.org/t/p/w342" + movie.getPosterPath()).into(poster);
                trailerList = new ArrayList<>();
                new TrailerLoader().execute();
                trailerAdapter = new TrailerAdapter(getActivity().getApplicationContext(), R.layout.trailer_list, trailerList);
                trailerAdapter.setNotifyOnChange(true);
                trailers.setAdapter(trailerAdapter);
                trailers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(trailerList.get(position)));
                        startActivity(intent);
                    }
                });


                reviewList = new ArrayList<>();
                new ReviewLoader().execute();
                reviewAdapter = new ReviewAdapter(getActivity().getApplicationContext(), R.layout.review_list, reviewList);
                reviewAdapter.setNotifyOnChange(true);
                reviews.setAdapter(reviewAdapter);
            } else {
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
                    int primary = 0xB0ABA0;
                    int maxColor = palette.getMutedColor(primary);

                    ColorDrawable cd = new ColorDrawable(maxColor);
                    mParentActivity.getSupportActionBar().setBackgroundDrawable(cd);

                    Window window = getActivity().getWindow();
                    window.setStatusBarColor(maxColor - 0x0A0A0A);

                }
            };
            try {
                Bitmap bitmap = ((BitmapDrawable) poster.getDrawable()).getBitmap();
                mPoster = bitmap;
                movie.setPoster(mPoster);
                Palette.from(bitmap).generate(paletteAsyncListener);
            }
            catch (Exception e){
                Toast.makeText(getContext(),"Some Error Occurred...",Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(mTRAILERKEY, trailerList);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        mPoster.compress(Bitmap.CompressFormat.PNG, 0, os);
        outState.putByteArray(mPOSTERKEY,os.toByteArray());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id){
            case R.id.movie_detail_favourite:
                //int res = addMovieToFavourite();
                //if(res == 1)
                ContentValues contentValues = getContentValuesToAddMovieToFavourite();
                getContext().getContentResolver().insert(CONTENT_URI,contentValues);
                return true;

            case R.id.movie_detail_share:
                shareMovie();
                return true;

            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
        inflater.inflate(R.menu.menu_movie_detail, menu);
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
                //Toast.makeText(getActivity().getApplicationContext(), "URL Error Occured..Cant fetch Trailer", Toast.LENGTH_LONG).show();
            }
            catch (IOException e){
                //Toast.makeText(getActivity().getApplicationContext(), "Error Occured..Cant fetch Trailer", Toast.LENGTH_LONG).show();
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
                Toast.makeText(getActivity().getApplicationContext(), "Error Occured..Cant fetch Review", Toast.LENGTH_LONG).show();
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
                Toast.makeText(getActivity().getApplicationContext(), "URL Error Occured..Cant fetch Review", Toast.LENGTH_LONG).show();
            }
            catch (IOException e){
                Toast.makeText(getActivity().getApplicationContext(), "Error Occured..Cant fetch Review", Toast.LENGTH_LONG).show();
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
                Toast.makeText(getActivity().getApplicationContext(), "Error Occured..Cant fetch Review", Toast.LENGTH_LONG).show();
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
        FavouriteMovieDBHelper dbHelper = new FavouriteMovieDBHelper(getActivity().getApplicationContext());
        return dbHelper.addMovie(mId,mTitle,mRating,mYor,mSynopsis,mPoster);
    }

    public ContentValues getContentValuesToAddMovieToFavourite (){
        ByteArrayOutputStream byteOS = new ByteArrayOutputStream();
        mPoster.compress(Bitmap.CompressFormat.PNG,0,byteOS);
        byte[] blob = byteOS.toByteArray();
        ContentValues contentValues = new ContentValues();
        contentValues.put(FavouriteMovieContract.MOVIE_ID,mId);
        contentValues.put(FavouriteMovieContract.MOVIE_NAME,mTitle);
        contentValues.put(FavouriteMovieContract.YEAR_OF_RELEASE,mYor);
        contentValues.put(FavouriteMovieContract.RATING,mRating);
        contentValues.put(FavouriteMovieContract.SYNOPSIS,mSynopsis);
        contentValues.put(FavouriteMovieContract.POSTER,blob);

        return contentValues;
    }

}
