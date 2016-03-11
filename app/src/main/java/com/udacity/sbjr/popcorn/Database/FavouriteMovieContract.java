package com.udacity.sbjr.popcorn.Database;

import android.net.Uri;

/**
 * Created by sbjr on 3/3/16.
 */
public class FavouriteMovieContract {


    public final static String TABLE_NAME="favourite_movie";

    public final static String AUTHORITY="COM.UDACITY.SBJR.POPCORN";
    public final static Uri BASE_URI = Uri.parse("content://" + AUTHORITY);
    public final static Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(TABLE_NAME).build();


    public final static String MOVIE_ID="movie_id";
    public final static String MOVIE_NAME="movie_name";
    public final static String YEAR_OF_RELEASE="year_of_release";
    public final static String RATING="rating";
    public final static String SYNOPSIS="synopsis";
    public final static String POSTER="poster";


}
