package com.josephmpo.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.josephmpo.myapplication.databinding.ActivityMainBinding;
import com.josephmpo.myapplication.models.Movie;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class MainActivity extends AppCompatActivity implements MovieAdapter.OnItemClickListener {

    public static final String MOVIE_DB_URL = "https://api.themoviedb.org/3/movie/now_playing";
    public static final String API_KEY = "a07e22bc18f5cb106bfe4cc1f83ad8ed";
    public static final String TAG = "MainActivity";
    List<Movie> movies;
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);

        movies = new ArrayList<>();

        MovieAdapter adapter = new MovieAdapter(movies, this, this);

        binding.moviesRv.setAdapter(adapter);
        binding.moviesRv.setLayoutManager(new LinearLayoutManager(this));

        setSupportActionBar(binding.toolbar);
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void onMovieItemClicked(int position, View sharedImageView) {
        Intent intent = new Intent(MainActivity.this, MovieActivity.class);
        intent.putExtra("movie", Parcels.wrap(movies.get(position)));
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                MainActivity.this, sharedImageView, "poster");
        startActivity(intent, options.toBundle());
    }
}