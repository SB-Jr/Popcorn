package com.udacity.sbjr.popcorn.Database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.udacity.sbjr.popcorn.POJO.Constants;

/**
 * Created by sbjr on 3/11/16.
 */
public class FavouriteMovieContentProvider extends ContentProvider {

    public final static String PROVIDER_NAME="com.udacity.sbjr.popcorn.Database.FavouriteMovieContentProvider";
    public final static Uri CONTENT_URI = Uri.parse("content://"+PROVIDER_NAME+"/movies");

    UriMatcher uriMatcher = getUriMatcher();

    static final int MOVIE = 1;

    FavouriteMovieDBHelper movieDBHelper = null;

    public static UriMatcher getUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME,"movies",MOVIE);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        movieDBHelper = new FavouriteMovieDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return movieDBHelper.getFavouriteMovieCursor();
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        //if(uriMatcher.match(uri)==MOVIE){
            return "vnd.android.cursor.dir/vnd.com.udacity.popcorn.provider.movies";
        //}

        //return "";
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        long id = movieDBHelper.addMovieCursor(values);
        if(id<0){
            Toast.makeText(getContext(),"Movie already a favourite Movie...",Toast.LENGTH_LONG).show();
        }

        Uri returnUri = ContentUris.withAppendedId(CONTENT_URI,id);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
