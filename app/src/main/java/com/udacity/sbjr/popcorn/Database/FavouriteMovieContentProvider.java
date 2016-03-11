package com.udacity.sbjr.popcorn.Database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by sbjr on 3/11/16.
 */
public class FavouriteMovieContentProvider extends ContentProvider {

    UriMatcher uriMatcher;

    static final int MOVIE = 1;

    FavouriteMovieDBHelper movieDBHelper = null;

    public static UriMatcher getUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(FavouriteMovieContract.AUTHORITY,"movie",MOVIE);
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
        if(uriMatcher.match(uri)==MOVIE)
            return movieDBHelper.getFavouriteMovieCursor();
        else
            return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        if(uriMatcher.match(uri)==MOVIE){
            return "vnd.android.cursor.dir/vnd.com.udacity.popcorn";
        }

        return "";
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        movieDBHelper.addMovie(values.getAsString(FavouriteMovieContract.MOVIE_ID),
                values.getAsString(FavouriteMovieContract.MOVIE_NAME),
                values.getAsString(FavouriteMovieContract.YEAR_OF_RELEASE),
                values.getAsString(FavouriteMovieContract.RATING),
                values.getAsString(FavouriteMovieContract.SYNOPSIS),
                (Bitmap)values.get(FavouriteMovieContract.POSTER));
        return null;
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
