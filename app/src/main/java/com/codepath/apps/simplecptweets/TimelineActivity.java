package com.codepath.apps.simplecptweets;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.activeandroid.content.ContentProvider;
import com.codepath.apps.simplecptweets.activities.ComposeActivity;
import com.codepath.apps.simplecptweets.adapters.EndlessRecyclerViewScrollListener;
import com.codepath.apps.simplecptweets.adapters.TweetCursorAdapter;
import com.codepath.apps.simplecptweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rvTweets)
    RecyclerView rvTweets;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    private TwitterClient client;
    TweetCursorAdapter mTweetCursorAdapter;
    Cursor mCursor;
    LinearLayoutManager mLinearLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        client = TwitterApplication.getRestClient();    // singleton client
        mLinearLayoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(mLinearLayoutManager);
        setupAdapter();
        populateTimeline();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Intent intent = new Intent(TimelineActivity.this, ComposeActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            mTweetCursorAdapter.notifyDataSetChanged();
        }
    }

    private void setupAdapter() {
        mTweetCursorAdapter = new TweetCursorAdapter(TimelineActivity.this, null);
        rvTweets.setAdapter(mTweetCursorAdapter);
        getSupportLoaderManager().initLoader(0, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle cursor) {
                return new CursorLoader(TimelineActivity.this,
                        ContentProvider.createUri(Tweet.class, null),
                        null, null, null, "uid DESC"
                );
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//                populateTimeline();
                mTweetCursorAdapter.swapCursor(data);
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                mTweetCursorAdapter.swapCursor(null);
            }
            // ...
        });

        rvTweets.addOnScrollListener(new EndlessRecyclerViewScrollListener(mLinearLayoutManager) {
            @Override
            public void onLoadMore(int page, final int totalItemsCount) {
                mCursor.moveToLast();
                long maxId = Tweet.fromCursor(mCursor).getUid();
                client.getHomeTimeline(maxId, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        Toast.makeText(TimelineActivity.this, "Load more JSON success", Toast.LENGTH_SHORT).show();
                        Log.d("TWITTER", response.toString());
                        Tweet.fromJson(response);
//                mTweetCursorAdapter.swapCursor(mCursor);
                        mTweetCursorAdapter.notifyDataSetChanged();
                        mTweetCursorAdapter.notifyItemRangeChanged(totalItemsCount, mCursor.getCount());
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Toast.makeText(TimelineActivity.this, "Load more JSON failure", Toast.LENGTH_SHORT).show();
                        Log.d("TWITTER", errorResponse.toString());
                    }
                });
                return;
            }
        });
    }

    private void populateTimeline() {
//        Toast.makeText(getApplicationContext(), "JSON request", Toast.LENGTH_SHORT).show();
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Toast.makeText(TimelineActivity.this, "JSON success", Toast.LENGTH_SHORT).show();
                Log.d("TWITTER", response.toString());
                Tweet.fromJson(response);
//                mTweetCursorAdapter.swapCursor(mCursor);
                mTweetCursorAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(TimelineActivity.this, "JSON failure", Toast.LENGTH_SHORT).show();
                Log.d("TWITTER", errorResponse.toString());
            }
        });
    }

}
