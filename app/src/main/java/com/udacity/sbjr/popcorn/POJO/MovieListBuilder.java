package com.udacity.sbjr.popcorn.POJO;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.udacity.sbjr.popcorn.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sbjr on 2/5/16.
 */
public class MovieListBuilder {


    final String results = "results";
    final String movieName = "original_title";
    final String movieReleaseDate = "release_date";
    final String moviePosterPath = "poster_path";
    final String movieBackdropPath = "backdrop_path";
    final String movieVote = "vote_average";
    final String movieSynopsis = "overview";
    final String movieId ="id";

    String json = null;
    public MovieListBuilder(String json){
        this.json = json;
    }



    /*
    * this method is used to create a list of movie objects by parsing the json and pass it to the calling function
    * */
    public ArrayList<Movie> buildList(Context context) throws JSONException{
        ArrayList<Movie> list = new ArrayList<>();

        if(json==null)
            return null;

        JSONObject obj = new JSONObject(json);
        JSONArray movieArray = obj.getJSONArray("results");
        for(int i=0;i<movieArray.length();i++){
            JSONObject movieJsonObj= movieArray.getJSONObject(i);
            Movie movieObj = new Movie();

            movieObj.setTitle(movieJsonObj.getString(movieName));
            movieObj.setSynopsis(movieJsonObj.getString(movieSynopsis));
            movieObj.setBackdropPath(movieJsonObj.getString(movieBackdropPath));
            movieObj.setPosterPath(movieJsonObj.getString(moviePosterPath));
            movieObj.setRating(movieJsonObj.getString(movieVote));
            movieObj.setReleaseDate(movieJsonObj.getString(movieReleaseDate));
            movieObj.setId(movieJsonObj.getString(movieId));
            movieObj.setPoster(null);

            list.add(movieObj);
        }

        return list;
    }

    /*
    * this method is used to build a list of trailer urls and pass it to the calling function
    * */
    public ArrayList<String> buildTrailerList() throws JSONException{

        ArrayList<String> list = new ArrayList<>();

        JSONObject obj = new JSONObject(json);
        JSONArray trailers = obj.getJSONArray("results");
        for(int i=0;i<trailers.length();i++){
            JSONObject trailer = trailers.getJSONObject(i);
            String trailerUrl = "https://www.youtube.com/watch?v="+trailer.getString("key");
            list.add(trailerUrl);
        }
        return list;
    }


    /*
    * this method creates a list of author and its review pair
    * */
    public ArrayList<String[]> buildReviewList() throws JSONException{

        ArrayList<String[]> list = new ArrayList<>();

        JSONObject obj = new JSONObject(json);
        JSONArray reviews = obj.getJSONArray("results");
        for(int i=0;i<reviews.length();i++){
            JSONObject review = reviews.getJSONObject(i);
            String author = review.getString("author");
            String verdict = review.getString("content");

            list.add(new String[]{author,verdict});

        }
        return list;
    }

}
