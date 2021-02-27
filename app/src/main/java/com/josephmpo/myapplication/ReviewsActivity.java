package com.josephmpo.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.josephmpo.myapplication.databinding.ActivityReviewsBinding;
import com.josephmpo.myapplication.models.Review;
import com.josephmpo.myapplication.models.ReviewAuthor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import adapters.ReviewAdapter;
import okhttp3.Headers;

public class ReviewsActivity extends AppCompatActivity implements ReviewAdapter.ScrollToTop {
    public static final String REVIEWS_URL = "https://api.themoviedb.org/3/movie/%d/reviews";
    public static final String API_KEY = "a07e22bc18f5cb106bfe4cc1f83ad8ed";
    ActivityReviewsBinding arb;
    List<Review> reviews;
    ReviewAdapter adapter;
    LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        arb = ActivityReviewsBinding.inflate(getLayoutInflater());
        setContentView(arb.getRoot());

        Bundle data = getIntent().getExtras();
        arb.tvMovieTitle.setText(data.get("title").toString());
        arb.tvMovieRating.setText(String.format("%s/10", data.get("rating").toString()));

        reviews = new ArrayList<>();

        adapter = new ReviewAdapter(reviews, this, this);
        arb.rvReviews.setAdapter(adapter);

        layoutManager = new LinearLayoutManager(this);
        arb.rvReviews.setLayoutManager(layoutManager);

        fetchReviews(data.getInt("id"));
    }

    private void fetchReviews(int id) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("api_key", API_KEY);

        client.get(String.format(REVIEWS_URL, id), params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Headers headers, JSON json) {
                JSONObject obj = json.jsonObject;
                try {
                    JSONArray res = obj.getJSONArray("results");
                    if(res.length() > 0){
                        List<Review> r = new ArrayList<>();
                        for (int j = 0; j < res.length(); j++) {
                            JSONObject ele = res.getJSONObject(j);
                            JSONObject authorDetails = ele.getJSONObject("author_details");
                            float rating;
                            try {
                                rating = (float) authorDetails.getDouble("rating");
                            } catch (Exception e) {
                                rating = -1f;
                            }

                            ReviewAuthor reviewAuthor = new ReviewAuthor(
                                    authorDetails.getString("name"),
                                    authorDetails.getString("username"),
                                    authorDetails.getString("avatar_path"),
                                    rating
                            );
                            Review review = new Review(
                                    reviewAuthor, ele.getString("author"),
                                    ele.getString("content"), ele.getString("created_at"),
                                    ele.getString("updated_at"), ele.getString("id"),
                                    ele.getString("url")
                            );
                            r.add(review);
                        }
                        reviews.addAll(r);
                        adapter.notifyDataSetChanged();
                    }else{
                        arb.rvReviews.setVisibility(View.GONE);
                        arb.tvNoReviews.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Headers headers, String s, Throwable throwable) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        supportFinishAfterTransition();
    }

    @Override
    public void scrollToTop(int position) {
        layoutManager.scrollToPositionWithOffset(position, 20);
    }
}