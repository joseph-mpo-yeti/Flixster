package com.josephmpo.myapplication;

import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
    OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onMovieItemClicked(int position);
    }

    public MovieAdapter(List<Movie> movies, Context context, OnItemClickListener onItemClickListener) {
        this.movies = movies;
        this.context = context;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (movies.get(position).getVoteAverage() > 5) {
            return 1;
        }

        return 0;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        switch (viewType){
            case 1:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie_full_layout, parent, false);
                break;
            default:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie_layout, parent, false);
        }

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        switch (holder.getItemViewType()){
            case 1:
                holder.bindFull(movies.get(position));
                break;
            default:
                holder.bind(movies.get(position));
        }
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

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onMovieItemClicked(getAdapterPosition());
                }
            });
        }

        public void bind(Movie m) {
            titleTextView.setText(m.getTitle());
            overviewTextView.setText(m.getOverview());
            if(context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                Glide.with(context)
                        .load(m.getBackdropPath())
                        .placeholder(R.drawable.placeholder)
                        .centerCrop()
                        .into(posterImageView);
            } else {
                Glide.with(context)
                        .load(m.getPosterPath())
                        .placeholder(R.drawable.placeholder_portrait)
                        .into(posterImageView);
            }
        }

        public void bindFull(Movie m) {
            titleTextView.setText(m.getTitle());
            Glide.with(context)
                    .load(m.getBackdropPath())
                    .placeholder(R.drawable.placeholder)
                    .centerCrop()
                    .into(posterImageView);
        }
    }


}
