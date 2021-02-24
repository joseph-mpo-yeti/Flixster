package com.josephmpo.myapplication.models;

import android.util.Log;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.josephmpo.myapplication.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

@Parcel
public class Movie {
    public static final String TAG = "Movie";
    String title, posterPath, backdropPath, overview;
    double voteAverage, popularity;
    int id;

    public Movie(){}

    public Movie(JSONObject obj) throws JSONException {
        this.title = obj.getString("title");
        this.posterPath = obj.getString("poster_path");
        this.backdropPath = obj.getString("backdrop_path");
        this.overview = obj.getString("overview");
        this.voteAverage = obj.getDouble("vote_average");
        this.popularity = obj.getDouble("popularity");
        this.id = obj.getInt("id");
    }

    public static List<Movie> fromJSONArray(JSONArray jsonArray) throws JSONException {
        List<Movie> movies = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            movies.add(new Movie(jsonArray.getJSONObject(i)));
        }

        return movies;
    }

    public String getTitle() {
        return title;
    }

    public String getPosterPath() {
        return String.format("https://image.tmdb.org/t/p/original%s", posterPath);
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public String getBackdropPath() {
        return String.format("https://image.tmdb.org/t/p/original%s", backdropPath);
    }

    public double getPopularity() {
        return popularity;
    }

    public int getId() {
        return id;
    }

    public String getOverview() {
        return overview;
    }
}
