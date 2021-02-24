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
    private YouTubePlayerView playerView;
    private static YouTubePlayer youTubePlayer;
    private static Movie movie;
    private ImageView ivPoster;
    private TextView tvTitle;
    private TextView tvOverview;
    private RatingBar ratingBar;
    private int minPlayerHeight;
    private RequestOptions requestOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);
        context = getApplicationContext();

        requestOptions = new RequestOptions();
        requestOptions = requestOptions.transform(new CenterCrop(), new RoundedCorners(16));

        playerView = findViewById(R.id.youtube_player);
        minPlayerHeight = playerView.getLayoutParams().height;

        movie = Parcels.unwrap(getIntent().getParcelableExtra("movie"));

        initYouTubePlayer();

        tvTitle = findViewById(R.id.tvTitle);
        tvOverview = findViewById(R.id.tvOverview);
        ratingBar = findViewById(R.id.ratingBar);
        ivPoster = findViewById(R.id.ivPoster);

        tvTitle.setText(movie.getTitle());
        tvOverview.setText(movie.getOverview());
        ratingBar.setRating((float) movie.getVoteAverage());

        updateUI();
    }


    public void updateUI(){
        playerView.setMinimumHeight(0);

        playerView.setVisibility(View.VISIBLE);
        ivPoster.setVisibility(View.GONE);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            playerView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
            ivPoster.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
            Glide.with(getApplicationContext()).load(movie.getBackdropPath())
                    .placeholder(R.drawable.placeholder)
                    .apply(requestOptions)
                    .into(ivPoster);
            tvTitle.setVisibility(View.GONE);
            tvOverview.setVisibility(View.GONE);
            ratingBar.setVisibility(View.GONE);
        } else {
            ivPoster.getLayoutParams().height = minPlayerHeight;
            playerView.getLayoutParams().height = minPlayerHeight;
            Glide.with(getApplicationContext()).load(movie.getBackdropPath())
                .placeholder(R.drawable.placeholder)
                .centerInside()
                .apply(requestOptions)
                .into(ivPoster);
            tvTitle.setVisibility(View.VISIBLE);
            tvOverview.setVisibility(View.VISIBLE);
            ratingBar.setVisibility(View.VISIBLE);
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

    private void initPlayer(YouTubePlayerView playerView, String movieID) {
        playerView.initialize(
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
                        ivPoster.setVisibility(View.VISIBLE);
                        playerView.setVisibility(View.GONE);
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
                    initPlayer(playerView, movieID);
                } else {
                    Toast.makeText(
                            context,
                            String.format("No trailer available for %s", movie.getTitle()),
                            Toast.LENGTH_SHORT)
                            .show();
                    ivPoster.setVisibility(View.VISIBLE);
                    playerView.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                ivPoster.setVisibility(View.VISIBLE);
                playerView.setVisibility(View.GONE);
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