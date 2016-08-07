package com.codepath.apps.simplecptweets.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.simplecptweets.R;
import com.codepath.apps.simplecptweets.models.Tweet;
import com.codepath.apps.simplecptweets.models.User;
import com.codepath.apps.simplecptweets.utils.TimeUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
        @BindView(R.id.tvScreenName)
        TextView tvScreenName;
        @BindView(R.id.tvTimeLine)
        TextView tvTimeLine;
//        R.layout.item_tweet

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
//        Log.d("TWITTER", "View Holder");
        // Extract properties from cursor
        Tweet tweet = Tweet.fromCursor(cursor);
        // Populate fields with extracted properties
        viewHolder.tvBody.setText(tweet.getBody());
        User user = User.find(tweet.getUserId());
        viewHolder.tvUserName.setText(user.getName());
        viewHolder.tvScreenName.setText("@" + user.getScreeName());
//        viewHolder.tvTimeLine.setText(getRelativeTimeAgo(tweet.getCreatedAt()));
        viewHolder.tvTimeLine.setText(TimeUtils.timeAgo(new Date(tweet.getCreatedAt())));
        Glide.with(viewHolder.itemView.getContext()).load(user.getProfileImageUrl()).into(viewHolder.ivProfileImage);
//        Log.d("TWITTER", "View Holder");
//        viewHolder.tvBody.setText(tweet.getUser().getScreeName());
    }

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    public String getRelativeTimeAgo(long dateMillis) {

        String relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL).toString();

        return relativeDate;
    }

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }
}