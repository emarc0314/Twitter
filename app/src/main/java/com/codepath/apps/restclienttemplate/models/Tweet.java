package com.codepath.apps.restclienttemplate.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Parcel
public class Tweet {
    public String body;
    public String createdAt;
    public User user;
    public String profilePath;
    public String picurl;
    public double picsizeratio;

    public Tweet(){}

    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = jsonObject.getString("text");
        tweet.createdAt = 	jsonObject.getString("created_at");
        tweet.user  = User.fromJson(jsonObject.getJSONObject("user"));

        if(!jsonObject.getJSONObject("entities").has("media")){
            tweet.picurl = "none";
        }
        else{
            JSONObject mediaObject = jsonObject.getJSONObject("entities").getJSONArray("media").getJSONObject(0);
//            String date = jsonObject.getJSONObject("created_at").toString();
//            Log.i("the date", date);
            tweet.picurl = mediaObject.getString("media_url_https");

            //getting size
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
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return tweets;
    }

    public String getUsername(){
        return user.name;
    }

    public String getPicURL(){
        return picurl;
    }

    public String getProfile(){
        return "";
    }
}
