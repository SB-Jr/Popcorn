package com.udacity.sbjr.popcorn.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
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

public class MovieGrid extends Fragment {

    private static final String sOrerTypeKey = "OrderTypeKey";
    private static final String sMoveListKey = "MovieListKey";
    public static final String sActivityName = "MovieGrid";

    public static final String sOrderType = "OrderType";

    GridView movieGrid = null;
    String mMovieJsonData =null;
    String mOrderType ="popularity.desc";
    List<Movie> movieList = null;
    MovieAdapter movieAdapter;

    boolean mLoaded = false;

    View mContentView = null;

    onMovieSelectedListener mActivityListener = null;

    public interface onMovieSelectedListener{
        void onMovieSelected(Movie movie);
        void onOrderChange(String order);
        void onMovieFavourite();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mActivityListener = (onMovieSelectedListener) getActivity();
        }catch (Exception e){
            Toast.makeText(context,"Some Error Occured..",Toast.LENGTH_SHORT).show();
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.movie_grid_fragment,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState!=null) {
            mLoaded = true;
            movieList = savedInstanceState.getParcelableArrayList(sMoveListKey);
            mOrderType = savedInstanceState.getString(sOrderType);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mContentView = getView();

        movieList = new ArrayList<>();

        Bundle bundle = getArguments();
        if(bundle!=null){
            mOrderType = bundle.getString(sOrderType);
        }


        movieGrid = (GridView) mContentView.findViewById(R.id.movie_grid);
        movieGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mActivityListener.onMovieSelected(movieList.get(position));
            }
        });

        movieAdapter = new MovieAdapter(getActivity().getApplicationContext(),R.layout.moviegrid,movieList);
        movieAdapter.setNotifyOnChange(true);
        movieGrid.setAdapter(movieAdapter);

        if(mLoaded ==false) {
            new MovieLoader().execute(mOrderType);
        }
        mLoaded = true;

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(sOrerTypeKey, mOrderType);
        outState.putParcelableArrayList(sMoveListKey, new ArrayList<Movie>(movieList));
    }


    @Override
    public void onPause() {
        super.onStop();
        mLoaded = false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.clear();
        inflater.inflate(R.menu.menu_main, menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        

        //noinspection SimplifiableIfStatement
        if (id == R.id.sort_order) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                        mActivityListener.onMovieFavourite();
                    }
                    if(which<=1&&newOrderType.equals(mOrderType)==false) {
                        mActivityListener.onOrderChange(newOrderType);
                        onStop();
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

        ProgressBar pBar = (ProgressBar) mContentView.findViewById(R.id.progressbar);

        @Override
        protected Void doInBackground(String... params) {

            try {
                mMovieJsonData = getMovieJson(params[0]);
            }
            catch (MalformedURLException e){
                Toast.makeText(getActivity().getApplicationContext(),"URL Error Occured",Toast.LENGTH_LONG).show();
            }
            catch (IOException e){
                Toast.makeText(getActivity().getApplicationContext(),"Error Occured",Toast.LENGTH_LONG).show();
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
                List<Movie> newMovieList =new MovieListBuilder(mMovieJsonData).buildList(getContext());
                for(Movie m:newMovieList){
                    m.setPoster(BitmapFactory.decodeResource(getResources(),R.drawable.movie_icon));
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
                Toast.makeText(getActivity().getApplicationContext(),"Some error Occurred",Toast.LENGTH_LONG).show();
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
