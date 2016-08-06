package com.codepath.apps.simplecptweets.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
 * Created by User on 8/5/2016.
 */
public class ComposeFragment extends DialogFragment {
    @BindView(R.id.etTweet)
    EditText etTweet;
    @BindView(R.id.btnTweet)
    Button btnTweet;

    ComposeDialogListener mComposeDialogListener;

    public interface ComposeDialogListener {
        void onFinishEditDialog();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mComposeDialogListener = (ComposeDialogListener) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_compose, container);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "post tweet", Toast.LENGTH_SHORT).show();
                String body = etTweet.getText().toString();
                TwitterClient client = TwitterApplication.getRestClient();
                client.postUpdate(body, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                        Intent resultIntent = new Intent();
                        Tweet.fromJSON(response);
//                resultIntent.putExtra("compose", Tweet.fromJSON(tweetJSON));
//                        setResult(Activity.RESULT_OK, resultIntent);
//                        finish();
                        mComposeDialogListener.onFinishEditDialog();
                        dismiss();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.d("DEBUG", throwable.toString());
                        Log.d("DEBUG", responseString.toString());
                    }
                });
            }
        });
    }

    public static ComposeFragment newInstance() {
        ComposeFragment frag = new ComposeFragment();
        Bundle args = new Bundle();
        frag.setArguments(args);

        return frag;
    }
}
