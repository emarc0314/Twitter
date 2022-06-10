package com.codepath.apps.restclienttemplate.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class Tweet {
    public String body;
    public String createdAt;
    public User user;
    public String profilePath;
    public String picurl;
    public double picsizeratio;
    public boolean isFavorited;
    public boolean isRetweeted;
    public int favoriteCount;
    public int retweetedCount;
    public String id;

    public Tweet(){}

    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        if(jsonObject.has("retweeted_status")){
            return null;
        }

        Tweet tweet = new Tweet();
        tweet.body = jsonObject.getString("text");
        tweet.createdAt = 	jsonObject.getString("created_at");
        tweet.user  = User.fromJson(jsonObject.getJSONObject("user"));
        tweet.isFavorited = jsonObject.getBoolean("favorited");
        tweet.isRetweeted = jsonObject.getBoolean("retweeted");
        tweet.favoriteCount = jsonObject.getInt("favorite_count");
        tweet.retweetedCount = jsonObject.getInt("retweet_count");
        tweet.id = jsonObject.getString("id_str");

        if(!jsonObject.getJSONObject("entities").has("media")){
            tweet.picurl = "none";
        }
        else {
            JSONObject mediaObject = jsonObject.getJSONObject("entities").getJSONArray("media").getJSONObject(0);
            tweet.picurl = mediaObject.getString("media_url_https");
            JSONArray sizeCategories = mediaObject.getJSONObject("sizes").names();

            for(int i = 0; i < sizeCategories.length(); i++) {
                String sizeName = sizeCategories.getString(i);
                if(sizeName != "thumb") {
                    JSONObject size = mediaObject.getJSONObject("sizes").getJSONObject(sizeName);
                    tweet.picsizeratio = (double) size.getInt("h") / size.getInt("w");
                    break;
                }
            }
        }
        return tweet;
    }

    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for(int i = 0; i<jsonArray.length(); i++){
            Tweet newTweet = fromJson(jsonArray.getJSONObject(i));
            if(newTweet != null){
                tweets.add(newTweet);
            }
        }
        return tweets;
    }

    public String getPicURL(){
        return picurl;
    }
}
