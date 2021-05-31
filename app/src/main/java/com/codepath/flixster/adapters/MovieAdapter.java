package com.codepath.flixster.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.flixster.DetailActivity;
import com.codepath.flixster.PlayTrailerActivity;
import com.codepath.flixster.R;
import com.codepath.flixster.models.Movie;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.Headers;


public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder>{

    Context context;
    List<Movie> movies;

    public MovieAdapter(Context context, List<Movie> movies) {
        this.context = context;
        this.movies = movies;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@androidx.annotation.NonNull @NotNull ViewGroup parent, int viewType) {
        Log.d("MovieAdapter", "onCreateViewHolder");
        View movieView = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        return new ViewHolder(movieView);
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(@androidx.annotation.NonNull @NotNull ViewHolder holder, int position) {
        Log.d("MovieAdapter", "onBindViewHolder " + position);
        // Get the movie at the passed in position
        Movie movie = movies.get(position);
        // Bind the movie data into the VH
        holder.bind(movie);

    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout container;
        TextView tvTitle;
        TextView tvOverview;
        ImageView ivPoster;
        TextView tvDetails;
        TextView tvPlayTrailer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvOverview = itemView.findViewById(R.id.tvOverview);
            ivPoster = itemView.findViewById(R.id.ivPoster);
            container = itemView.findViewById(R.id.container);
            tvDetails = itemView.findViewById(R.id.tvDetails);
            tvPlayTrailer = itemView.findViewById(R.id.tvPlayTrailer);


        }

        public void bind(Movie movie) {
            tvTitle.setText(movie.getTitle());
            tvOverview.setText(movie.getOverview());
            String imageUrl;

            // if phone is in landscape
            if (context.getResources().getConfiguration(). orientation == Configuration.ORIENTATION_LANDSCAPE) {
                // then imageUrl = back drop image
                imageUrl = movie.getBackdropPath();
            } else {
                // else imageUrl = poster image
                imageUrl = movie.getPosterPath();
            }

            int radius = 30; // corner radius, higher value = more rounded
            int margin = 10; // crop margin, set to 0 for corners with no crop
            // Glide.with(context).load(imageUrl).into(ivPoster);
            Glide.with(context).load(imageUrl)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.imagenotfound)
                    .transform(new RoundedCornersTransformation(radius, margin))
                    .into(ivPoster);

            // 1. Register click listener on the whole row
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navigateToDetails(v, movie, 1);
                }
            });
            tvDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navigateToDetails(v, movie, 1);
                }
            });
            tvPlayTrailer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navigateToDetails(v, movie, 2);
                }
            });

        }
        public void navigateToDetails(View v, Movie movie, int activityNum) {
            // 2. Navigate to a new activity on tap
            Intent i;
            if (activityNum == 2) {
                i = new Intent(context, PlayTrailerActivity.class);
                i.putExtra("movie", Parcels.wrap(movie));
                context.startActivity(i);
                return;
            }
            i = new Intent(context, DetailActivity.class);
            i.putExtra("movie", Parcels.wrap(movie));

            // The shared element transitions require Android 5.0 (API level 21) and above
            if( Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                context.startActivity(i);
            } else {
                Pair<View, String> p1 = Pair.create((View)tvTitle, "title");
                Pair<View, String> p2 = Pair.create((View)tvOverview, "overview");
                Pair<View, String> p3 = Pair.create((View)ivPoster, "poster");
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation((Activity) context, p1, p2, p3);
                context.startActivity(i, options.toBundle());

            }
        }
    }



}
