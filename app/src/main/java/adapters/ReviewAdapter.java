package adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.josephmpo.myapplication.R;
import com.josephmpo.myapplication.models.Review;
import com.josephmpo.myapplication.models.ReviewAuthor;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    List<Review> reviews;
    Context context;
    ScrollToTop scrollToTopObject;

    public interface ScrollToTop {
        void scrollToTop(int pos);
    }

    public ReviewAdapter(List<Review> reviews, Context context, ScrollToTop scroll) {
        this.reviews = reviews;
        this.context = context;
        this.scrollToTopObject = scroll;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_review_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(reviews.get(position), position);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvAuthorName, tvContent, tvAuthorRating, tvReadMore, tvExpand;
        View vRoot;
        private boolean expanded;
        private int initialHeight;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            expanded = false;
            vRoot = itemView.findViewById(R.id.reviewRoot);
            tvAuthorName = itemView.findViewById(R.id.tvAuthorName);
            tvReadMore = itemView.findViewById(R.id.tvReadMore);
            tvExpand = itemView.findViewById(R.id.tvExpand);
            tvContent = itemView.findViewById(R.id.tvReviewContent);
            tvAuthorRating = itemView.findViewById(R.id.tvAuthorRating);
        }

        public void bind(Review review, int position) {
            ReviewAuthor reviewAuthor = review.getReviewAuthor();
            tvAuthorName.setText(reviewAuthor.getName().equals("") ? reviewAuthor.getUsername() : reviewAuthor.getName());
            tvContent.setText(review.getContent());
            if(reviewAuthor.getRating() < 0){
                tvAuthorRating.setText("None");
                tvAuthorRating.setTextSize(20);
                tvAuthorRating.setBackground(context.getDrawable(R.drawable.cornered_background_neutral));
            } else {
                tvAuthorRating.setText(Float.toString(reviewAuthor.getRating()));
                if(reviewAuthor.getRating() > 5){
                    tvAuthorRating.setBackground(context.getDrawable(R.drawable.cornered_background_light));
                } else {
                    tvAuthorRating.setBackground(context.getDrawable(R.drawable.cornered_background_red));
                }
            }

            initialHeight = vRoot.getLayoutParams().height;

            tvReadMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(review.getUrl())));
                }
            });

            tvExpand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    scrollToTopObject.scrollToTop(position);
                    if(expanded){
                        vRoot.getLayoutParams().height = initialHeight;
                        tvContent.setMaxLines(5);
                        tvExpand.setText("Expand");
                    } else {
                        tvContent.setMaxLines(Integer.MAX_VALUE);
                        vRoot.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                        tvExpand.setText("Hide");
                    }

                    expanded = !expanded;
                }
            });
        }
    }
}
