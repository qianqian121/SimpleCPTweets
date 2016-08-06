package com.codepath.apps.simplecptweets.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.apps.simplecptweets.R;
import com.codepath.apps.simplecptweets.TwitterApplication;
import com.codepath.apps.simplecptweets.TwitterClient;
import com.codepath.apps.simplecptweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

/**
 * Created by qiming on 8/5/2016.
 */
public class ComposeActivity extends AppCompatActivity {
    @BindView(R.id.etTweet)
    EditText etTweet;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        ButterKnife.bind(this);
    }

    public void onClick(View view) {
        Toast.makeText(ComposeActivity.this, "post tweet", Toast.LENGTH_SHORT).show();
        String body = etTweet.getText().toString();
        TwitterClient client = TwitterApplication.getRestClient();
        client.postUpdate(body, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Intent resultIntent = new Intent();
                Tweet.fromJSON(response);
//                resultIntent.putExtra("compose", Tweet.fromJSON(tweetJSON));
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("DEBUG", throwable.toString());
                Log.d("DEBUG", responseString.toString());
            }
        });
    }
}
