package com.example.android.newsapp;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StoryActivity extends AppCompatActivity implements LoaderCallbacks<List<Story>> {

    private static final String GUARDIAN_REQUEST_URL =
            "https://content.guardianapis.com/search?section=technology&show-tags=contributor&show-fields=thumbnail&api-key=72ce1cf7-395d-41ee-bd3c-feb64b969d8b";

    private static final int STORY_LOADER_ID = 1;

    private StoryAdapter mAdapter;

    private TextView mEmptyStateTextView;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.story_activity);

        Objects.requireNonNull(getSupportActionBar()).setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ListView storyListView = findViewById(R.id.list);

        mEmptyStateTextView = findViewById(R.id.empty_view);
        storyListView.setEmptyView(mEmptyStateTextView);

        mAdapter = new StoryAdapter(this, new ArrayList<Story>());

        storyListView.setAdapter(mAdapter);

        storyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Story currentStory = mAdapter.getItem(position);

                assert currentStory != null;
                Uri storyUri = Uri.parse(currentStory.getUrl());

                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, storyUri);

                startActivity(websiteIntent);
            }
        });

        NetworkInfo networkInfo = getNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getLoaderManager();

            loaderManager.initLoader(STORY_LOADER_ID, null, this);
        } else {
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    @Override
    public Loader<List<Story>> onCreateLoader(int i, Bundle bundle) {
        return new StoryLoader(this, GUARDIAN_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<Story>> loader, List<Story> stories) {
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        NetworkInfo networkInfo = getNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {

            mEmptyStateTextView.setText(R.string.no_stories);

            mAdapter.clear();

            if (stories != null && !stories.isEmpty()) {
                mAdapter.addAll(stories);
            }
        } else {
            mAdapter.clear();

            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    private NetworkInfo getNetworkInfo() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        assert connMgr != null;
        return connMgr.getActiveNetworkInfo();
    }

    @Override
    public void onLoaderReset(Loader<List<Story>> loader) {
        mAdapter.clear();
    }
}
