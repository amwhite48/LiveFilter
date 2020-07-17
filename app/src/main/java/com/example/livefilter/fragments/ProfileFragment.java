package com.example.livefilter.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.livefilter.LoginActivity;
import com.example.livefilter.R;
import com.example.livefilter.models.FilterPost;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;


public class ProfileFragment extends HomeFragment {

    Button btLogout;
    View view;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.view = view;
        super.onViewCreated(view, savedInstanceState);

        btLogout =  view.findViewById(R.id.btLogOut);

        btLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // log out current user
                ParseUser.logOut();
                ParseUser currentUser = ParseUser.getCurrentUser(); // this will now be null
                // navigate to login activity
                Intent intent = new Intent(view.getContext(), LoginActivity.class);
                startActivity(intent);
                // make it so user can't go back to activity
                getActivity().finish();
            }
        });
    }

    @Override
    protected void queryFilters() {
        // set recyclerview to one from profile view
        rvFilters = view.findViewById(R.id.rvProPosts);

        // set adapter on RecyclerView
        rvFilters.setAdapter(adapter);
        // set layout manager on recyclerview
        rvFilters.setLayoutManager(new LinearLayoutManager(view.getContext()));


        // make a new query to get filters
        ParseQuery<FilterPost> query = ParseQuery.getQuery(FilterPost.class);
        // include user in information retrieved
        query.include(FilterPost.KEY_USER);
        query.whereEqualTo(FilterPost.KEY_USER, ParseUser.getCurrentUser());
        query.setLimit(20);
        query.addDescendingOrder(FilterPost.KEY_CREATED_AT);

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
        });    }
}