package com.josephmpo.myapplication;

import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.josephmpo.myapplication.models.Movie;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    List<Movie> movies;
    Context context;

    public MovieAdapter(List<Movie> movies, Context context) {
        this.movies = movies;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(movies.get(position));
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView overviewTextView;
        ImageView posterImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.movie_title_text_view);
            overviewTextView = itemView.findViewById(R.id.movie_overview_text_view);
            posterImageView = itemView.findViewById(R.id.poster_image_view);
        }

        public void bind(Movie m) {
            titleTextView.setText(m.getTitle());
            overviewTextView.setText(m.getOverview());
            String imageURL;
            if(context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                imageURL = m.getBackdropPath();
            } else {
                imageURL = m.getPosterPath();
            }
            Glide.with(context)
                    .load(imageURL)
                    .into(posterImageView);
        }
    }
}
