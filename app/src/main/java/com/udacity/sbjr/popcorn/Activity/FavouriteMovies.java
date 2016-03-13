package com.udacity.sbjr.popcorn.Activity;

import android.app.AlertDialog;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.udacity.sbjr.popcorn.Adapter.FavouriteMovieAdapter;
import com.udacity.sbjr.popcorn.Database.FavouriteMovieDBHelper;
import com.udacity.sbjr.popcorn.POJO.Movie;
import com.udacity.sbjr.popcorn.R;

import java.util.ArrayList;

public class FavouriteMovies extends Fragment {


    public static final String sActivityName="FavouriteMovies";
    public static final String sFavMovieListKey = "FavMovieListKey";

    public final static String PROVIDER_NAME="com.udacity.sbjr.popcorn.Database.FavouriteMovieContentProvider";
    public final static Uri CONTENT_URI = Uri.parse("content://"+PROVIDER_NAME+"/movies");

    ArrayList<Movie> favMovie = null;


    public interface FavouriteMovieInterface{
        void onFavouriteMovieSelected(Movie movie);
        void onOrderChange(String order);
    }

    FavouriteMovieInterface mFavouriteMovieInterface = null;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mFavouriteMovieInterface = (FavouriteMovieInterface) getActivity();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState!=null){
            favMovie = savedInstanceState.getParcelableArrayList(sFavMovieListKey);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.activity_favourite_movies,container,false);
    }

    @Override
    public void onStart() {
        super.onStart();

        View contentView = getView();

        FavouriteMovieDBHelper dbHelper = new FavouriteMovieDBHelper(getActivity().getApplicationContext());
        if(favMovie==null) {
            Cursor cursor = getContext().getContentResolver().query(CONTENT_URI,null,null,null,null);
            //favMovie = dbHelper.getFavouriteMovies();
            favMovie = new ArrayList<>();
            parseCursor(cursor);
        }
        GridView grid = (GridView) contentView.findViewById(R.id.favourite_movie_grid);
        FavouriteMovieAdapter adapter = new FavouriteMovieAdapter(getActivity().getApplicationContext(),R.layout.moviegrid,favMovie);
        adapter.setNotifyOnChange(true);
        grid.setAdapter(adapter);

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mFavouriteMovieInterface.onFavouriteMovieSelected(favMovie.get(position));
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(sFavMovieListKey, new ArrayList<Movie>(favMovie));
    }

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
                        mFavouriteMovieInterface.onOrderChange(newOrderType);
                    }
                    else if(which==1){
                        newOrderType="vote_count.desc";
                        mFavouriteMovieInterface.onOrderChange(newOrderType);
                    }
                    else if(which==2){
                    }
                }
            });
            builder.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void parseCursor(Cursor cursor) {
        if (cursor.moveToFirst()) {
            do {
                Movie m = new Movie();
                String id = cursor.getString(0);
                String title = cursor.getString(1);
                String rating = cursor.getString(2);
                String yor = cursor.getString(3);
                String synopsis = cursor.getString(4);
                byte[] posterByte = cursor.getBlob(5);
                Bitmap poster = BitmapFactory.decodeByteArray(posterByte, 0, posterByte.length);
                m.setId(id);
                m.setTitle(title);
                m.setRating(rating);
                m.setReleaseDate(yor);
                m.setSynopsis(synopsis);
                m.setPoster(poster);
                favMovie.add(m);
            } while (cursor.moveToNext());
        }
    }

}
