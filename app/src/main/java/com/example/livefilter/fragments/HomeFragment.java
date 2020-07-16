package com.example.livefilter.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.livefilter.R;
import com.example.livefilter.adapters.FeedAdapter;
import com.example.livefilter.models.FilterPost;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    public static final String TAG = "HomeFragment";
    protected RecyclerView rvFilters;
    protected FeedAdapter adapter;
    protected List<FilterPost> allPosts;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvFilters = view.findViewById(R.id.rvFilters);

        allPosts = new ArrayList<>();
        adapter = new FeedAdapter(view.getContext(), allPosts);

        // set adapter on RecyclerView
        rvFilters.setAdapter(adapter);
        // set layout manager on recyclerview
        rvFilters.setLayoutManager(new LinearLayoutManager(view.getContext()));

        // get filters from parse
        queryFilters();
    }

    private void queryFilters() {
        // make a new query to get filters
        ParseQuery<FilterPost> query = ParseQuery.getQuery(FilterPost.class);
        // include user in information retrieved
        query.include(FilterPost.KEY_USER);
        // search for filter displays in background
        query.findInBackground(new FindCallback<FilterPost>() {
            @Override
            public void done(List<FilterPost> filterPosts, ParseException e) {
                // if unsuccessful, or error is thrown
                if (e != null) {
                    Log.e(TAG, "Issue with retrieving filters", e);
                    return;
                }
                for(FilterPost post: filterPosts) {
                    Log.i(TAG, "Filter: " + post.getName());
                }

                // save received posts, notify adapter of changes
                allPosts.addAll(filterPosts);
                adapter.notifyDataSetChanged();
            }
        });


    }
}