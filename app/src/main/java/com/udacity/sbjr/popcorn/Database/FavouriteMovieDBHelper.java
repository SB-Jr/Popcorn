package com.udacity.sbjr.popcorn.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.widget.Toast;

import com.udacity.sbjr.popcorn.POJO.Movie;

import java.io.ByteArrayOutputStream;
import java.sql.Blob;
import java.util.ArrayList;

/**
 * Created by sbjr on 3/11/16.
 */
public class FavouriteMovieDBHelper extends SQLiteOpenHelper{


    static String DatabaseName="favouriteMovie.db";
    static int DatabaseVersion=1;

    Context context;

    public FavouriteMovieDBHelper(Context context){
        super(context,DatabaseName,null,DatabaseVersion);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_TABLE_QUERY="CREATE TABLE "+FavouriteMovieContract.TABLE_NAME+"( "+
                FavouriteMovieContract.MOVIE_ID+" VARCHAR(10) PRIMARY KEY , "+
                FavouriteMovieContract.MOVIE_NAME+" VARCHAR(30) NOT NULL, "+
                FavouriteMovieContract.YEAR_OF_RELEASE+" VARCHAR(10) NOT NULL, "+
                FavouriteMovieContract.RATING+" VARCHAR(5) NOT NULL, "+
                FavouriteMovieContract.SYNOPSIS+" VARCHAR(200) NOT NULL, "+
                FavouriteMovieContract.POSTER+" BLOB );";

        db.execSQL(SQL_CREATE_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {  }

    public long addMovieCursor(ContentValues contentValues){
        long id = getWritableDatabase().insert(FavouriteMovieContract.TABLE_NAME,null,contentValues);
        return id;
    }

    public int addMovie(String id,String title,String yor,String rating,String synopsis,Bitmap poster){
        SQLiteDatabase db = getWritableDatabase();
        ByteArrayOutputStream byteOS = new ByteArrayOutputStream();
        poster.compress(Bitmap.CompressFormat.PNG,0,byteOS);
        byte[] blob = byteOS.toByteArray();
        ContentValues contentValues = new ContentValues();
        contentValues.put(FavouriteMovieContract.MOVIE_ID,id);
        contentValues.put(FavouriteMovieContract.MOVIE_NAME,title);
        contentValues.put(FavouriteMovieContract.YEAR_OF_RELEASE,yor);
        contentValues.put(FavouriteMovieContract.RATING,rating);
        contentValues.put(FavouriteMovieContract.SYNOPSIS,synopsis);
        contentValues.put(FavouriteMovieContract.POSTER,blob);

        try {
            db.insert(FavouriteMovieContract.TABLE_NAME,null,contentValues);
            db.close();
            Toast.makeText(context,title+" added to favourite list",Toast.LENGTH_LONG).show();

        }
        catch (Exception e){
            Toast.makeText(context,"Either movie is already in the Fouvirite List or else Some Error occured...",Toast.LENGTH_LONG).show();
            db.close();
            return -1;
        }
        return 1;
    }

    public Cursor getFavouriteMovieCursor(){
        String query = "SELECT * FROM "+FavouriteMovieContract.TABLE_NAME+";";
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }

    public ArrayList<Movie> getFavouriteMovies(){
        ArrayList<Movie> favMovies = new ArrayList<>();

        String query = "SELECT * FROM "+FavouriteMovieContract.TABLE_NAME+";";
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()){
            do{
                Movie m = new Movie();
                String id = cursor.getString(0);
                String title = cursor.getString(1);
                String rating = cursor.getString(2);
                String yor = cursor.getString(3);
                String synopsis = cursor.getString(4);
                byte[] posterByte = cursor.getBlob(5);
                Bitmap poster = BitmapFactory.decodeByteArray(posterByte,0,posterByte.length);
                m.setId(id);
                m.setTitle(title);
                m.setRating(rating);
                m.setReleaseDate(yor);
                m.setSynopsis(synopsis);
                m.setPoster(poster);
                favMovies.add(m);
            }while (cursor.moveToNext());
        }
        return  favMovies;
    }

}
