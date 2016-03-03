package com.udacity.sbjr.popcorn.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

    final private String apiKey="----";

    GridView movieGrid = null;
    String movieJsonData=null;
    String orderType="popularity.desc";
    List<Movie> movieList = null;
    MovieAdapter movieAdapter;

    boolean loaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        movieList = new ArrayList<>();

        movieGrid = (GridView) findViewById(R.id.movie_grid);
        movieGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(),MovieDetail.class);
                intent.putExtra(Constants.MOVIE_PARCELABLE,movieList.get(position));
                startActivity(intent);
            }
        });

        movieAdapter = new MovieAdapter(getApplicationContext(),R.layout.moviegrid,movieList);
        movieAdapter.setNotifyOnChange(true);
        movieGrid.setAdapter(movieAdapter);

        if(loaded==false) {
            new MovieLoader().execute(orderType);
        }
        loaded = true;

    }



    @Override
    protected void onStop() {
        super.onStop();
        loaded = false;
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
            builder.setTitle("Select a Sort Order");
            String options[] = {"Popularity","Highest-Rated"};
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String newOrderType;
                    if(which==0){
                        newOrderType="popularity.desc";
                    }
                    else{
                        newOrderType="vote_count.desc";
                    }
                    if(newOrderType.equals(orderType)==false) {
                        orderType = new String(newOrderType);
                        movieList.clear();
                        new MovieLoader().execute(orderType);
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
                movieJsonData = getMovieJson(params[0]);
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
                List<Movie> newMovieList =new MovieListBuilder(movieJsonData).buildList();
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

        URL url = new URL("http://api.themoviedb.org/3/discover/movie?api_key="+apiKey+"&sort_by="+order);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
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
