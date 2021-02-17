package com.josephmpo.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.josephmpo.myapplication.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class MainActivity extends AppCompatActivity {

    public static final String MOVIE_DB_URL = "https://api.themoviedb.org/3/movie/now_playing";
    public static final String API_KEY = "a07e22bc18f5cb106bfe4cc1f83ad8ed";
    public static final String TAG = "MainActivity";
    List<Movie> movies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        movies = new ArrayList<>();

        MovieAdapter adapter = new MovieAdapter(movies, this);
        RecyclerView rv = findViewById(R.id.movies_rv);

        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));

        getSupportActionBar().setTitle("Now Playing 🍿");

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("api_key", API_KEY);
        client.get(MOVIE_DB_URL, params, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int i, Headers headers, JSON json) {
                JSONObject object = json.jsonObject;
                JSONArray results;
                try {
                    results = object.getJSONArray("results");
                    movies.addAll(Movie.fromJSONArray(results));
                    Log.d(TAG, "onSuccess: " + movies.size());
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Headers headers, String s, Throwable throwable) {
                Log.d(TAG, "It failed");
            }
        });

//
    }
}