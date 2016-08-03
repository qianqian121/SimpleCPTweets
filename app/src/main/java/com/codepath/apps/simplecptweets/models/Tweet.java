package com.codepath.apps.simplecptweets.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by qiming on 8/2/2016.
 */

// Parse the JSON + store the data, encapsulate state logic or display logic
public class Tweet {
    // list out the attributes
    private String body;
    private long uid;   // unique id for the tweet

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
}
