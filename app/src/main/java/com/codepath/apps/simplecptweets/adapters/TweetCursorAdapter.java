package com.codepath.apps.simplecptweets.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.simplecptweets.R;
import com.codepath.apps.simplecptweets.models.Tweet;

import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;

//import android.widget.CursorAdapter;

/**
 * Created by qiming on 6/21/2016.
 */
public class TweetCursorAdapter extends CursorRecyclerViewAdapter<TweetCursorAdapter.ViewHolder> {
    public static final SimpleDateFormat SDF = new SimpleDateFormat("MM/dd/yyyy");
    private View.OnClickListener mOnClickListener;

//    R.layout.item_tweet

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.mOnClickListener = onClickListener;
    }

    public Cursor getCursor(int position) {
        Cursor cursor = getCursor();
        cursor.moveToPosition(position);
        return cursor;
    }

    public TweetCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivProfileImage)
        ImageView ivProfileImage;
        @BindView(R.id.tvUserName)
        TextView tvUserName;
        @BindView(R.id.tvBody)
        TextView tvBody;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnClickListener.onClick(view);
                }
            });

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tweet, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        Log.d("TWITTER", "View Holder");
        // Extract properties from cursor
        Tweet tweet = Tweet.fromCursor(cursor);
        // Populate fields with extracted properties
        viewHolder.tvBody.setText(tweet.getBody());
        Log.d("TWITTER", "View Holder");
//        viewHolder.tvBody.setText(tweet.getUser().getScreeName());
    }

}