package com.udacity.sbjr.popcorn.POJO;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sbjr on 2/3/16.
 */
public class Movie implements Parcelable {

    String title;
    String releaseDate;
    String synopsis;
    String posterPath;
    String backdropPath;
    String rating;

    public Movie(){    }

    public Movie(Parcel parcel){
        this.title = parcel.readString();
        this.releaseDate = parcel.readString();
        this.synopsis = parcel.readString();
        this.posterPath = parcel.readString();
        this.backdropPath = parcel.readString();
        this.rating = parcel.readString();
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(releaseDate);
        dest.writeString(synopsis);
        dest.writeString(posterPath);
        dest.writeString(backdropPath);
        dest.writeString(rating);
    }


    public static final Parcelable.Creator<Movie> CREATOR = new ClassLoaderCreator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source, ClassLoader loader) {
            return new Movie(source);
        }

        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[0];
        }
    };


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}
