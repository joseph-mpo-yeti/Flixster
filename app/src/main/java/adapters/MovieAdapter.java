package adapters;

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
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.josephmpo.myapplication.R;
import com.josephmpo.myapplication.models.Movie;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    List<Movie> movies;
    Context context;
    OnItemClickListener onItemClickListener;

    RequestOptions requestOptions;

    public interface OnItemClickListener {
        void onMovieItemClicked(int position, View view);
    }

    public MovieAdapter(List<Movie> movies, Context context, OnItemClickListener onItemClickListener) {
        this.movies = movies;
        this.context = context;
        this.onItemClickListener = onItemClickListener;
        requestOptions = (new RequestOptions()).transform(new CenterCrop(), new RoundedCorners(16));
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
                    onItemClickListener.onMovieItemClicked(getAdapterPosition(), (View) posterImageView);
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
                        .apply(requestOptions)
                        .into(posterImageView);
            } else {
                Glide.with(context)
                        .load(m.getPosterPath())
                        .placeholder(R.drawable.placeholder_portrait)
                        .apply(requestOptions)
                        .into(posterImageView);
            }
        }

        public void bindFull(Movie m) {
            titleTextView.setText(m.getTitle());
            Glide.with(context)
                    .load(m.getBackdropPath())
                    .placeholder(R.drawable.placeholder)
                    .centerCrop()
                    .apply(requestOptions)
                    .into(posterImageView);
        }
    }


}
