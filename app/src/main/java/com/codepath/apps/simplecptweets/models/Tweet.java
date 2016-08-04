package com.codepath.apps.simplecptweets.models;

import android.database.Cursor;
import android.provider.BaseColumns;
import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by qiming on 8/2/2016.
 */

// Parse the JSON + store the data, encapsulate state logic or display logic
@Table(name = "tweets", id = BaseColumns._ID)
public class Tweet extends Model {
    // list out the attributes
    @Column(name = "body")
    private String body;
    @Column(name = "uid")
    private long uid;   // unique id for the tweet
    @Column(name = "userId")
    String userId;

    public Tweet(JSONObject object) {
        super();

        try {
//            this.userId = object.getString("user_id");
//            this.userHandle = object.getString("user_username");
//            this.timestamp = object.getString("timestamp");
            this.body = object.getString("text");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Tweet() {
        super();
    }

    public User getUser() {
        return user;
    }

    private User user;

    public String getBody() {
        return body;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public long getUid() {
        return uid;
    }

    @Column(name = "createAt")
    private String createdAt;

    // Desearialize the JSON
    // Tweet.fromJSON => Tweet
    public static Tweet fromJSON(JSONObject jsonObject) {
        Tweet tweet = new Tweet();
        // Extract the values from the json, store there
        try {
            tweet.body = jsonObject.getString("text");
            tweet.uid = jsonObject.getLong("id");
            tweet.createdAt = jsonObject.getString("created_at");
            tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // tweet.user =
        return tweet;
    }

    public static ArrayList<Tweet> fromJson(JSONArray jsonArray) {
        ArrayList<Tweet> tweets = new ArrayList<Tweet>(jsonArray.length());

        for (int i=0; i < jsonArray.length(); i++) {
            JSONObject tweetJson = null;
            try {
                tweetJson = jsonArray.getJSONObject(i);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            Tweet tweet = new Tweet(tweetJson);
            tweet.save();
            Log.d("TWITTER", tweet.getBody());
            tweets.add(tweet);
        }

        return tweets;
    }

    public static Tweet fromCursor(Cursor cursor) {
        Tweet tweet = new Tweet();
        tweet.loadFromCursor(cursor);
        return tweet;
    }
}
