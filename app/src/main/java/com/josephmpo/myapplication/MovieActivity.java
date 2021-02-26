package com.josephmpo.myapplication;

import androidx.annotation.NonNull;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.josephmpo.myapplication.databinding.ActivityMovieBinding;
import com.josephmpo.myapplication.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import okhttp3.Headers;

public class MovieActivity extends YouTubeBaseActivity {

    public static final String YOUTUBE_API_KEY = "YOUTUBE_API_KEY";
    public static final String MOVIE_DB_URL = "https://MOVIE_DB_URL.themoviedb.org/3/movie/%d/trailers";
    public static final String MOVIE_DB_API_KEY = "MOVIE_DB_API_KEY";
    public static final String TAG = "MovieActivity";
    private Context context;
    private static YouTubePlayer youTubePlayer;
    private static Movie movie;
    private int minPlayerHeight;
    private RequestOptions requestOptions;
    ActivityMovieBinding amb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        amb = ActivityMovieBinding.inflate(getLayoutInflater());
        View root = amb.getRoot();
        setContentView(root);

        context = getApplicationContext();

        requestOptions = new RequestOptions();
        requestOptions = requestOptions.transform(new CenterCrop(), new RoundedCorners(16));

        minPlayerHeight = amb.youtubePlayer.getLayoutParams().height;

        movie = Parcels.unwrap(getIntent().getParcelableExtra("movie"));

        initYouTubePlayer();

        amb.tvTitle.setText(movie.getTitle());
        amb.tvOverview.setText(movie.getOverview());
        amb.ratingBar.setRating((float) movie.getVoteAverage());

        updateUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        amb = null;
    }

    public void updateUI(){
        amb.youtubePlayer.setMinimumHeight(0);
        amb.youtubePlayer.setVisibility(View.VISIBLE);
        amb.ivPoster.setVisibility(View.GONE);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            amb.youtubePlayer.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
            amb.ivPoster.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
            Glide.with(getApplicationContext()).load(movie.getBackdropPath())
                    .placeholder(R.drawable.placeholder)
                    .apply(requestOptions)
                    .into(amb.ivPoster);
            amb.tvTitle.setVisibility(View.GONE);
            amb.tvOverview.setVisibility(View.GONE);
            amb.ratingBar.setVisibility(View.GONE);
        } else {
            amb.ivPoster.getLayoutParams().height = minPlayerHeight;
            amb.youtubePlayer.getLayoutParams().height = minPlayerHeight;
            Glide.with(getApplicationContext()).load(movie.getBackdropPath())
                .placeholder(R.drawable.placeholder)
                .centerInside()
                .apply(requestOptions)
                .into(amb.ivPoster);
            amb.tvTitle.setVisibility(View.VISIBLE);
            amb.tvOverview.setVisibility(View.VISIBLE);
            amb.ratingBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateUI();

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE && youTubePlayer != null){
            youTubePlayer.setFullscreen(true);
        }
    }

    @SuppressLint("DefaultLocale")
    private void initYouTubePlayer() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("api_key", MOVIE_DB_API_KEY);
        client.get(String.format(MOVIE_DB_URL, movie.getId()),
                params,
                new MyMovieJSONHandler());
    }

    private void initPlayer(String movieID) {
        amb.youtubePlayer.initialize(
                YOUTUBE_API_KEY,
                new YouTubePlayer.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(
                            YouTubePlayer.Provider provider,
                            YouTubePlayer youTubePlayer,
                            boolean b) {
                        MovieActivity.youTubePlayer = youTubePlayer;
                        youTubePlayer.cueVideo(movieID);
                        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                            youTubePlayer.setFullscreen(true);
                        }
                        YouTubePlayer.PlayerStateChangeListener listener = new PlayerListener(youTubePlayer);
                        youTubePlayer.setPlayerStateChangeListener(listener);
                    }

                    @Override
                    public void onInitializationFailure(
                            YouTubePlayer.Provider provider,
                            YouTubeInitializationResult youTubeInitializationResult)
                    {
                        Log.d(TAG, "onInitializationFailure: Something went wrong");
                        amb.ivPoster.setVisibility(View.VISIBLE);
                        amb.youtubePlayer.setVisibility(View.GONE);
                        String message = "";
                        if(youTubeInitializationResult.toString().equals("SERVICE_VERSION_UPDATE_REQUIRED")){
                            message = "Please, update Google Play in order to view trailers";
                        }
                        Toast.makeText(getApplicationContext(),
                                !message.equals("") ? message : "There was an error: "+youTubeInitializationResult.toString(),
                                Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    private class MyMovieJSONHandler extends JsonHttpResponseHandler {


        public MyMovieJSONHandler() {}

        @Override
        public void onSuccess(int i, Headers headers, JSON json) {
            JSONObject object = json.jsonObject;
            JSONArray youtubeResults;
            try {
                youtubeResults = object.getJSONArray("youtube");
                if(youtubeResults.length() > 0){
                    JSONObject trailer = youtubeResults.getJSONObject(0);
                    String movieID = trailer.getString("source");
                    initPlayer(movieID);
                } else {
                    Toast.makeText(
                            context,
                            String.format("No trailer available for %s", movie.getTitle()),
                            Toast.LENGTH_SHORT)
                            .show();
                    amb.ivPoster.setVisibility(View.VISIBLE);
                    amb.youtubePlayer.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                amb.ivPoster.setVisibility(View.VISIBLE);
                amb.youtubePlayer.setVisibility(View.GONE);
            }
        }

        @Override
        public void onFailure(int i, Headers headers, String s, Throwable throwable) {
            Log.d(TAG, "It failed "+s);
        }
    }


    private class PlayerListener implements YouTubePlayer.PlayerStateChangeListener {
        private YouTubePlayer youTubePlayer;

        public PlayerListener(YouTubePlayer youTubePlayer) {
            this.youTubePlayer = youTubePlayer;
        }

        @Override
        public void onLoading() {

        }

        @Override
        public void onLoaded(String s) {
            if(movie.getVoteAverage() > 5){
                youTubePlayer.play();
            }
        }

        @Override
        public void onAdStarted() {
        }

        @Override
        public void onVideoStarted() {

        }

        @Override
        public void onVideoEnded() {}

        @Override
        public void onError(YouTubePlayer.ErrorReason errorReason) {}
    }
}