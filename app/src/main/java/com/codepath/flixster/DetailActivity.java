package com.codepath.flixster;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.RatingBar;
import android.widget.TextView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.flixster.adapters.MovieAdapter;
import com.codepath.flixster.models.Movie;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class DetailActivity extends YouTubeBaseActivity {

    public final static String YOUTUBE_API_KEY = "AIzaSyBDn2YoPYSD4DFR6sBgTaiCEzAw4Ow2hhw";
    public final static String VIDEO_URL = "https://api.themoviedb.org/3/movie/%d/videos?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";
    TextView tvDOverview;
    TextView tvDTitle;
    RatingBar ratingBar;
    TextView tvReleasedDate;
    YouTubePlayerView youTubePlayerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        tvDOverview = findViewById(R.id.tvDOverview);
        tvDTitle = findViewById(R.id.tvDTitle);
        ratingBar = findViewById(R.id.ratingBar);
        tvReleasedDate = findViewById(R.id.tvReleasedDate);
        youTubePlayerView = findViewById(R.id.player);

        Movie movie = Parcels.unwrap(getIntent().getParcelableExtra("movie"));
        tvDTitle.setText(movie.getTitle());
        tvDOverview.setText(movie.getOverview());
        ratingBar.setRating((float) movie.getRating());
        tvReleasedDate.setText("Released Date: " + movie.getReleasedDate());

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(String.format(VIDEO_URL, movie.getMovieID()), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Headers headers, JSON json) {
                try {
                    JSONArray results = json.jsonObject.getJSONArray("results");
                    if (results.length() == 0) {
                        // TODO: Handle the error by showing the placeholer image like backdrop image
                        return;
                    }
                    // TODO: Make sure the site is YouTube before parsing out the key
                    String youtubeKey = results.getJSONObject(0).getString("key");
                    Log.d("DetailActivity", youtubeKey);
                    initializeYoutube(youtubeKey);

                } catch (JSONException e) {
                    Log.e("DetailActivity", "Failed to parse JSON", e);
                }
            }

            @Override
            public void onFailure(int i, Headers headers, String s, Throwable throwable) {
                Log.e("DetailActivity", "Failed to perform network request");
            }
        });

    }

    private void initializeYoutube(final String youtubeKey) {
        youTubePlayerView.initialize(YOUTUBE_API_KEY,
                new YouTubePlayer.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                        YouTubePlayer youTubePlayer, boolean b) {

                        Log.d("DetailActivity", "onInitializationSuccess");

                        // do any work here to cue video, play video, etc.
                        youTubePlayer.cueVideo(youtubeKey);
                    }
                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                        YouTubeInitializationResult youTubeInitializationResult) {
                        Log.e("DetailActivity", "onInitializationFailure");

                    }
                });
    }

    @Override
    public void onBackPressed() {
        //To support reverse transitions when user clicks the device back button
        finishAfterTransition();
    }
}