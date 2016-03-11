package com.udacity.sbjr.popcorn.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.udacity.sbjr.popcorn.Adapter.MovieAdapter;
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

public class MainActivity extends AppCompatActivity {

    private static final String sOrerTypeKey = "OrderTypeKey";
    private static final String sMoveListKey = "MovieListKey";

    public static final String sActivityName = "MainActivity";

    GridView movieGrid = null;
    String mMovieJsonData =null;
    String mOrderType ="popularity.desc";
    List<Movie> movieList = null;
    MovieAdapter movieAdapter;

    boolean mLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        movieList = new ArrayList<>();

        if(savedInstanceState!=null){
            mLoaded = true;
            movieList = savedInstanceState.getParcelableArrayList(sMoveListKey);
            mOrderType = savedInstanceState.getString(sOrerTypeKey);
        }

        movieGrid = (GridView) findViewById(R.id.movie_grid);
        movieGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(),MovieDetail.class);
                intent.putExtra(Constants.MOVIE_PARCELABLE,movieList.get(position));
                intent.putExtra(Constants.ActivityName,sActivityName);
                startActivity(intent);
            }
        });

        movieAdapter = new MovieAdapter(getApplicationContext(),R.layout.moviegrid,movieList);
        movieAdapter.setNotifyOnChange(true);
        movieGrid.setAdapter(movieAdapter);

        if(mLoaded ==false) {
            new MovieLoader().execute(mOrderType);
        }
        mLoaded = true;

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(sOrerTypeKey, mOrderType);
        outState.putParcelableArrayList(sMoveListKey, new ArrayList<Movie>(movieList));
    }

    @Override
    protected void onStop() {
        super.onStop();
        mLoaded = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        

        //noinspection SimplifiableIfStatement
        if (id == R.id.sort_order) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select an Option");
            String options[] = {"Sort By Popularity","Sort By Highest-Rated","My Favourites"};
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String newOrderType = null;
                    if(which==0){
                        newOrderType="popularity.desc";
                    }
                    else if(which==1){
                        newOrderType="vote_count.desc";
                    }
                    else if(which==2){
                        Intent intent = new Intent(getApplicationContext(),FavouriteMovies.class);
                        startActivity(intent);
                    }
                    if(which<=1&&newOrderType.equals(mOrderType)==false) {
                        mOrderType = new String(newOrderType);
                        movieList.clear();
                        new MovieLoader().execute(mOrderType);
                    }
                }
            });
            builder.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /*
    * Async Task to run in background
    * displays a progressbar when fetching data and later dismisses it on load complete
    * calls the getMovieJson method
    * creates a ArrayList out of the json string fetched
    * Array List can be used later to update the gridview
    * shows appropriate error as Toast
    */
    class MovieLoader extends AsyncTask<String,Void,Void>{

        ProgressBar pBar = (ProgressBar) findViewById(R.id.progressbar);

        @Override
        protected Void doInBackground(String... params) {

            try {
                mMovieJsonData = getMovieJson(params[0]);
            }
            catch (MalformedURLException e){
                Toast.makeText(getApplicationContext(),"URL Error Occured",Toast.LENGTH_LONG).show();
            }
            catch (IOException e){
                Toast.makeText(getApplicationContext(),"Error Occured",Toast.LENGTH_LONG).show();
            }


            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            try {
                List<Movie> newMovieList =new MovieListBuilder(mMovieJsonData).buildList();
                for(Movie m:newMovieList){
                    boolean contains = false;
                    for(Movie m2:movieList){
                        if(m.getTitle().equals(m2.getTitle())==true){
                            contains = true;
                        }
                    }
                    if(!contains){
                        movieList.add(m);
                    }
                }
            }
            catch (JSONException e){
                Toast.makeText(getApplicationContext(),"Some error Occurred",Toast.LENGTH_LONG).show();
            }
            movieAdapter.notifyDataSetChanged();

            pBar.setVisibility(View.INVISIBLE);
            pBar.invalidate();

        }

    }


    /*
    * methods to
    * create a connection from the movie url
    * get the json string
    * @params: takes the order of movie display
    * return: returns the json string*/
    public String getMovieJson(String order) throws MalformedURLException, IOException{
        String json = "";

        URL url = new URL("http://api.themoviedb.org/3/discover/movie?api_key="+ Constants.mApiKey +"&sort_by="+order);
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
}
