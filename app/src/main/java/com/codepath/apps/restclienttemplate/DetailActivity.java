package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

public class DetailActivity extends AppCompatActivity {
    TextView tvUsername;
    TextView tvHandle;
    TextView tvBody;
    ImageView ivProfile;
    ImageView ivBodyImage;

    int profileRadius = 100;
    int radius = 30;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        tvUsername = findViewById(R.id.tvDusername);
        tvBody = findViewById(R.id.tvDbody);
        ivProfile = findViewById(R.id.ivDprofile);
        ivBodyImage = findViewById(R.id.ivDimage);
        tvHandle = findViewById(R.id.tvHandle);
        Tweet tweet = Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));
        tvUsername.setText(tweet.user.name);
        tvBody.setText(tweet.body);
        tvHandle.setText(tweet.user.screenName);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) this).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);
        int imageWidth = displayMetrics.widthPixels - 200;
        int imageHeight = (int)(imageWidth * tweet.picsizeratio);
        Glide.with(this).load(tweet.user.profileImageUrl).transform(new RoundedCorners(profileRadius)).into(ivProfile);
        Glide.with(this).load(tweet.getPicURL()).transform(new RoundedCorners(radius)).override(imageWidth, imageHeight).into(ivBodyImage);
    }
}