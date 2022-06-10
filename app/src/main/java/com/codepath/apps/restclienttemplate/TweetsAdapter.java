package com.codepath.apps.restclienttemplate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;

import java.text.ParseException;
import java.util.List;
import java.util.Locale;

import okhttp3.Headers;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {
    Context context;
    List<Tweet> tweets;

    public TweetsAdapter(Context context, List<Tweet>tweets){
        this.context = context;
        this.tweets = tweets;
    }
    //For each row inflate the layout

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    //Bind values based on the position of the element
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //get the data at position
        Tweet tweet = tweets.get(position);
        //Bind the tweet with view holder
        holder.bind(tweet);
    }

    //Pass in the context and list of tweets
    @Override
    public int getItemCount() {
        return tweets.size();
    }

    //Define a viewholder
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView ivProfileImage;
        TextView tvBody;
        TextView tvScreenName;
        TextView tvName;
        TextView tvCreatedat;
        ImageView ivURL;
        ImageButton ibFavorite;
        TextView tvFavoriteCount;

        private static final int SECOND_MILLIS = 1000;
        private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvName = itemView.findViewById(R.id.tvName);
            ivURL = itemView.findViewById(R.id.ivURL);
            tvCreatedat = itemView.findViewById(R.id.tvCreatedat);
            ibFavorite = itemView.findViewById(R.id.ibFavorite);
            tvFavoriteCount = itemView.findViewById(R.id.tvFavoriteCount);
            itemView.setOnClickListener(this);
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        public String getRelativeTimeAgo(String rawJsonDate) {
            String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
            SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
            sf.setLenient(true);

            try {
                long time = sf.parse(rawJsonDate).getTime();
                long now = System.currentTimeMillis();

                final long diff = now - time;
//                Log.i("diff", );
                if (diff < MINUTE_MILLIS) {
                    return "just now";
                } else if (diff < 2 * MINUTE_MILLIS) {
                    return "a minute ago";
                } else if (diff < 50 * MINUTE_MILLIS) {
                    return diff / MINUTE_MILLIS + " m";
                } else if (diff < 90 * MINUTE_MILLIS) {
                    return "an hour ago";
                } else if (diff < 24 * HOUR_MILLIS) {
                    return diff / HOUR_MILLIS + " h";
                } else if (diff < 48 * HOUR_MILLIS) {
                    return "yesterday";
                } else {
                    return diff / DAY_MILLIS + " d";
                }
            } catch (ParseException e) {
                Log.i("whatever", "getRelativeTimeAgo failed");
                e.printStackTrace();
            }

            return "";
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        public void bind(Tweet tweet) {
            int profileRadius = 100;
            tvBody.setText(tweet.body);
            tvScreenName.setText(tweet.user.screenName);
            tvName.setText(tweet.user.getName());
            tvFavoriteCount.setText(String.valueOf(tweet.favoriteCount));
            tvCreatedat.setText(" • " + getRelativeTimeAgo(tweet.createdAt));

            if(!tweet.isFavorited){
                Glide.with(context).load(R.drawable.ic_vector_heart_stroke).into(ibFavorite);
            }
            else{
                Glide.with(context).load(R.drawable.ic_vector_heart).into(ibFavorite);

            }


            ibFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!tweet.isFavorited){
//                        Drawable newIMage = context.getDrawable()
                        Glide.with(context).load(R.drawable.ic_vector_heart).into(ibFavorite);
//                        ibFavorite.setImageDrawable(R.drawable.ic_vector_heart_stroke).into(ibFavorite);
                        tweet.favoriteCount = tweet.favoriteCount + 1;
                        tvFavoriteCount.setText(String.valueOf(tweet.favoriteCount));
                        TwitterApp.getRestClient(context).favorite(tweet.id, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                Log.i("adapter", "This sghould've been favorite");
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                            }
                        });
                    }
                    else{
                        Glide.with(context).load(R.drawable.ic_vector_heart_stroke).into(ibFavorite);
                        tweet.favoriteCount = tweet.favoriteCount - 1;
                        tvFavoriteCount.setText(String.valueOf(tweet.favoriteCount));

                        TwitterApp.getRestClient(context).unfavorite(tweet.id, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                Log.i("adapter", "This sghould've been favorite");
                            }
                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            }
                        });

                    }
                    tweet.isFavorited = !tweet.isFavorited;

                    //if not already favorited
                        //tell twitter I want to favorite
                        // change the drawable to btn_star_big_on
                         // increment the text inside tvFavoriteCount
                    //else if favorited
                        //tell twitter I want to unfavorite this
                        //change drawable back to btn_star_off
                        // decrement the text inside tvFavoriteCount

                }
            });

            Glide.with(context)
                    .load(tweet.user.profileImageUrl)
                    .transform(new RoundedCorners(profileRadius))
                    .into(ivProfileImage);
            int radius = 30;

            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity) context).getWindowManager()
                    .getDefaultDisplay()
                    .getMetrics(displayMetrics);

            int imageWidth = displayMetrics.widthPixels - 100;
            int imageHeight = (int)(imageWidth * tweet.picsizeratio);

            if(!tweet.picurl.isEmpty()){
                Glide.with(context).load(tweet.picurl)
                        .override(imageWidth, imageHeight)
                        .transform(new RoundedCorners(radius)).into(ivURL);
                ivURL.setVisibility(View.VISIBLE);
            }
            else{
                ivURL.setVisibility(View.GONE);
            }

        }

        @Override
        public void onClick(View v) {
            // gets item position
            int position = getAdapterPosition();
            // make sure the position is valid, i.e. actually exists in the view
            if (position != RecyclerView.NO_POSITION) {
                // get the movie at the position, this won't work if the class is static
                Tweet tweet = tweets.get(position);
                // create intent for the new activity
                Intent intent = new Intent(context, DetailActivity.class);
                // serialize the movie using parceler, use its short name as a key
                intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                // show the activity
                context.startActivity(intent);
            }
        }
    }

    // Clean all elements of the recycler
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        tweets.addAll(list);
        notifyDataSetChanged();
    }
}
