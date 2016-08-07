package com.codepath.apps.simplecptweets;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.activeandroid.content.ContentProvider;
import com.codepath.apps.simplecptweets.activities.DetailActivity;
import com.codepath.apps.simplecptweets.adapters.EndlessRecyclerViewScrollListener;
import com.codepath.apps.simplecptweets.adapters.TweetCursorAdapter;
import com.codepath.apps.simplecptweets.fragments.ComposeFragment;
import com.codepath.apps.simplecptweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity implements ComposeFragment.ComposeDialogListener {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rvTweets)
    RecyclerView rvTweets;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;
    private TwitterClient client;
    TweetCursorAdapter mTweetCursorAdapter;
    Cursor mCursor;
    LinearLayoutManager mLinearLayoutManager;
    private final int REQUEST_CODE = 20;

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

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateTimeline();
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//                Intent intent = new Intent(TimelineActivity.this, ComposeActivity.class);
//                startActivity(intent);
                FragmentManager fm = getSupportFragmentManager();
                ComposeFragment composeFragment = ComposeFragment.newInstance();
                composeFragment.show(fm, "fragment_compose");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
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
                mCursor = data;
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
//                        mTweetCursorAdapter.notifyItemRangeChanged(totalItemsCount, mCursor.getCount());
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Toast.makeText(TimelineActivity.this, "Load more JSON failure", Toast.LENGTH_SHORT).show();
                        Log.d("TWITTER", errorResponse.toString());
                    }
                });
                mTweetCursorAdapter.notifyItemRangeChanged(totalItemsCount, totalItemsCount + 25);
                return;
            }
        });

        mTweetCursorAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TimelineActivity.this, DetailActivity.class);
                Tweet tweet = Tweet.fromCursor(mTweetCursorAdapter.getCursor(rvTweets.getChildAdapterPosition(view)));
                intent.putExtra("tweetDetail", Parcels.wrap(tweet));
                startActivityForResult(intent, REQUEST_CODE);
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
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(TimelineActivity.this, "JSON failure", Toast.LENGTH_SHORT).show();
                Log.d("TWITTER", errorResponse.toString());
            }
        });
    }

    @Override
    public void onFinishEditDialog() {
        mTweetCursorAdapter.notifyDataSetChanged();
    }
}
