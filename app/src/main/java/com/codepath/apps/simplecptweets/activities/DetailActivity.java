package com.codepath.apps.simplecptweets.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.simplecptweets.R;
import com.codepath.apps.simplecptweets.TwitterApplication;
import com.codepath.apps.simplecptweets.TwitterClient;
import com.codepath.apps.simplecptweets.models.Tweet;
import com.codepath.apps.simplecptweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class DetailActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.ivProfileImage)
    ImageView ivProfileImage;
    @BindView(R.id.tvUserName)
    TextView tvUserName;
    @BindView(R.id.tvBody)
    TextView tvBody;
    @BindView(R.id.tvScreenName)
    TextView tvScreenName;
    @BindView(R.id.tvTimeLine)
    TextView tvTimeLine;
    @BindView(R.id.etReply)
    EditText etReply;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.llMedia)
    LinearLayout llMedia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                String body = etReply.getText().toString();
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
        });

        Tweet tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra("tweetDetail"));
        tvBody.setText(tweet.getBody());

        User user = User.find(tweet.getUserId());
        tvUserName.setText(user.getName());
        String reply = "@" + user.getScreeName();
        tvScreenName.setText(reply);
        etReply.setText(reply + " ");
        etReply.setSelection(etReply.getText().length());
        Glide.with(DetailActivity.this).load(user.getProfileImageUrl()).into(ivProfileImage);

        Date date = new Date(tweet.getCreatedAt());
        SimpleDateFormat df = new SimpleDateFormat("hh:mm aa - dd MMM yyyy");
        tvTimeLine.setText(df.format(date));

//        VideoPlayerManager mVideoPlayerManager = new SingleVideoPlayerManager(new PlayerItemChangeListener() {
//            @Override
//            public void onPlayerItemChanged(MetaData currentItemMetaData) {
//                return;
//            }
//        });
//        mVideoPlayerManager.playNewVideo(null, videoPlayer, "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4");
//        mVideoPlayerManager.playNewVideo(null, videoPlayer, "https://goo.gl/cHZzP6");   // not able to play

        loadImage(tweet.getUid());
        loadVideo(tweet.getUid());
    }

    private void loadVideo(long uId) {
        TwitterClient client = TwitterApplication.getRestClient();
        client.getHomeTimeline(1, uId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
//                Toast.makeText(TimelineActivity.this, "Load more JSON success", Toast.LENGTH_SHORT).show();
                Log.d("TWITTER", response.toString());
                String url = "";
                try {
                    JSONObject extEntity = response.getJSONObject(0).getJSONObject("entities");
                    if (extEntity == null)
                        return;
                    JSONArray media = extEntity.getJSONArray("urls");
                    if (media == null)
                        return;
                    url = media.getJSONObject(0).getString("expanded_url");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                injectVideo(url);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("TWITTER", errorResponse.toString());
            }
        });
    }

    private void injectVideo(String url) {
        final WebView wvMedia = new WebView(this);
//        wvMedia.setLayoutParams(new LinearLayout.LayoutParams(1024, 720));
        wvMedia.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 600));
//        llMedia.addView(wvMedia);
        wvMedia.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                llMedia.addView(wvMedia);
                return true;
            }
        });
        wvMedia.setWebChromeClient(new WebChromeClient() {
        });
        wvMedia.getSettings().setLoadsImagesAutomatically(true);
        wvMedia.getSettings().setJavaScriptEnabled(true);
        wvMedia.getSettings().setAllowFileAccess(true);
        wvMedia.getSettings().setAllowUniversalAccessFromFileURLs(true);
        wvMedia.getSettings().setPluginState(WebSettings.PluginState.ON);
        wvMedia.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        // Enable responsive layout
        wvMedia.getSettings().setUseWideViewPort(true);
// Zoom out if the content width is greater than the width of the veiwport
        wvMedia.getSettings().setLoadWithOverviewMode(true);
//        String frameVideo = "<html><body>Video<br><iframe width=\"1080\" height=\"800\" src=\"https://www.youtube.com/embed/47yJ2XCRLZs\" frameborder=\"0\" allowfullscreen></iframe></body></html>";
        String frameVideo = String.format("<html><body>Video<br><iframe width=\"1080\" height=\"800\" src=\"%s\" frameborder=\"0\" allowfullscreen></iframe></body></html>", url);
//        String frameVideo = "<html><body>Youtube video .. <br> <iframe width=\"320\" height=\"315\" src=\"https://www.youtube.com/embed/lY2H2ZP56K4\" frameborder=\"0\" allowfullscreen></iframe></body></html>";

//        String frameVideo = "<html><body>Video From YouTube<br><iframe width=\"320\" height=\"240\" src=\"https://goo.gl/cHZzP6\" frameborder=\"0\" allowfullscreen></iframe></body></html>";
//        wvMedia.loadData(frameVideo, "text/html", "utf-8");
        wvMedia.loadUrl(url);
    }

    private void loadImage(long uId) {
        TwitterClient client = TwitterApplication.getRestClient();
        client.getHomeTimeline(1, uId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
//                Toast.makeText(TimelineActivity.this, "Load more JSON success", Toast.LENGTH_SHORT).show();
                Log.d("TWITTER", response.toString());
                String url = "";
                try {
                    JSONObject extEntity = response.getJSONObject(0).getJSONObject("extended_entities");
                    if (extEntity == null)
                        return;
                    JSONArray media = extEntity.getJSONArray("media");
                    if (media == null)
                        return;
                    url = media.getJSONObject(0).getString("media_url");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                injectImage(url);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("TWITTER", errorResponse.toString());
            }
        });
    }

    private void injectImage(String url) {
        ImageView image = new ImageView(this);
        image.setLayoutParams(new android.view.ViewGroup.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
//        image.setMaxHeight(20);
//        image.setMaxWidth(20);

//        Glide.with(this).load("http://pbs.twimg.com/media/CpQbWQAWgAAf6cY.jpg").into(image);
        Glide.with(this).load(url).into(image);
        // Adds the view to the layout
        llMedia.addView(image);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }
}
