package com.udacity.sbjr.popcorn.POJO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    String json = null;
    public MovieListBuilder(String json){
        this.json = json;
    }

    public ArrayList<Movie> buildList() throws JSONException{
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


            list.add(movieObj);
        }

        return list;
    }


}
