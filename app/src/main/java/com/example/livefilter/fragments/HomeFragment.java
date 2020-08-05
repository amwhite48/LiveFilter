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
import android.widget.Button;

import com.example.livefilter.MainActivity;
import com.example.livefilter.R;
import com.example.livefilter.adapters.FeedAdapter;
import com.example.livefilter.models.AppliedFilter;
import com.example.livefilter.models.FilterPost;
import com.example.livefilter.models.SortableFilterPost;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class HomeFragment extends Fragment {

    public static final String TAG = "HomeFragment";
    protected RecyclerView rvFilters;
    protected FeedAdapter adapter;
    protected List<FilterPost> allPosts;
    private Button btRecommend;
    private List<FilterPost> usersPosts;


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
        btRecommend = view.findViewById(R.id.btRecommend);

        allPosts = new ArrayList<>();
        adapter = new FeedAdapter(view.getContext(), allPosts);

        // set adapter on RecyclerView
        rvFilters.setAdapter(adapter);
        // set layout manager on recyclerview
        rvFilters.setLayoutManager(new LinearLayoutManager(view.getContext()));

        // get filters from parse
        queryFilters();

        btRecommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortByUserSimilarity();
            }
        });
    }

    protected void queryFilters() {
        // make a new query to get filters
        ParseQuery<FilterPost> query = ParseQuery.getQuery(FilterPost.class);
        // include user in information retrieved
        query.include(FilterPost.KEY_USER);

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
        });


    }


    // calculate the average values of filters user usually creates
    // calculate the distance of each queried filter from the user's average
    private void sortByUserSimilarity() {

        // make a new query to get filters
        ParseQuery<FilterPost> userQuery = ParseQuery.getQuery(FilterPost.class);
        // include user in information retrieved
        userQuery.include(FilterPost.KEY_USER);
        userQuery.whereEqualTo(FilterPost.KEY_USER, ParseUser.getCurrentUser());
        userQuery.setLimit(20);
        userQuery.addDescendingOrder(FilterPost.KEY_CREATED_AT);

        // list to add users' posts to
        usersPosts = new ArrayList<>();

        // search for filter displays in background
        userQuery.findInBackground(new FindCallback<FilterPost>() {
            @Override
            public void done(List<FilterPost> filterPosts, ParseException e) {
                // if unsuccessful, or error is thrown
                if (e != null) {
                    Log.e(TAG, "Issue with retrieving filters", e);
                    return;
                }
                for (FilterPost post : filterPosts) {
                    Log.i(TAG, "Filter: " + post.getName());
                }

                // save received posts
                usersPosts.addAll(filterPosts);
                // calculate the user preferences
                int[] userPrefs = computeUserPrefs();

                List<SortableFilterPost> sortedFilters = new ArrayList<>();

                // get an indexed array for every item from the original query
                for(int i = 0; i < allPosts.size(); i++) {
                    // calculate how filter differs from user prefs
                    int[] filterIndexed = getIndexedFilterIntensities(getStringArrayFromJsonArray(allPosts.get(i).getEffectNames()), getIntArrayFromJsonArray(allPosts.get(i).getEffectIntensities()));
                    int distanceFromPrefs = getDistanceQuantity(userPrefs, filterIndexed);
                    // make a new SortableFilterPost using this difference and add it to our array
                    sortedFilters.add(new SortableFilterPost(allPosts.get(i), distanceFromPrefs));
                }

                // sort the filters by their distance from the main filter, as outlined by the comparator
                Collections.sort(sortedFilters);

                // if user created one of the filters, don't show it in the recommendations


                // add all the sorted posts to the adapter
                allPosts.clear();
                for(int i = sortedFilters.size() - 1; i >= 0; i--) {
                    allPosts.add(sortedFilters.size() - i - 1, sortedFilters.get(i).getFilterPost());
                    Log.i(TAG, "Filter " + sortedFilters.get(i).getFilterPost().getName() + " has difference level " + sortedFilters.get(i).getCloseness());
                }
                adapter.notifyDataSetChanged();

            }
        });
        // get all posts made by as user}
    }

    private int getDistanceQuantity(int[] userPrefs, int[] filterIndexed) {
        int totalDist = 0;
        // add absolute value of difference between two filters to compute their similarity
        for(int i = 0; i < userPrefs.length; i++) {
            totalDist += Math.abs(userPrefs[i] - filterIndexed[i]);
        }
        return totalDist;
    }

    // compute average filter values for a user
    private int[] computeUserPrefs() {
        // set up array to track totals
        int[] totals = new int[AppliedFilter.EFFECTS.length];

        // iterate through each given filter
        for(int i = 0; i < usersPosts.size(); i++) {
            // get effect names and intensities from current filters
            String[] effectNames = getStringArrayFromJsonArray(usersPosts.get(i).getEffectNames());
            int[] effectIntensities = getIntArrayFromJsonArray(usersPosts.get(i).getEffectIntensities());
            // get array where effect intensities are indexed
            int[] indexedIntensities = getIndexedFilterIntensities(effectNames, effectIntensities);
            // add indexed intensities to total
            for(int j = 0; j < totals.length; j++) {
                totals[j] += indexedIntensities[j];
            }

        }

        // compute average intensities for each effect
        int[] averageIntensities = new int[totals.length];
        for(int i = 0; i < totals.length; i++) {
            // compute int average for use of each filter
            averageIntensities[i] = totals[i] / usersPosts.size();
        }

        return averageIntensities;

    }

    // generate an indexed indication of a filter's values that can be compared with user averages
    private int[] getIndexedFilterIntensities(String[] effectNames, int[] effectIntensities) {
        int[] indexedIntensities = new int[AppliedFilter.DEFAULT_INTENSITIES.length];

        for (int j = 0; j < indexedIntensities.length; j++) {
            // iterate through all possible effects
            // check if current effect is used on this filter, or contained in effects array
            boolean effectApplied = false;
            for (int k = 0; k < effectNames.length; k++) {
                // have to check each index in effect names because effectNames is not ordered
                if (effectNames[k].equals(AppliedFilter.EFFECTS[j])) {
                    // if effect is applied store its intensity
                    indexedIntensities[j] = effectIntensities[k];
                    effectApplied = true;
                    break;
                }

            }
            // if we didn't find the current effect in the filter effects, set it as our default
            if (!effectApplied) {
                indexedIntensities[j] = AppliedFilter.DEFAULT_INTENSITIES[j];
            }
        }
        return indexedIntensities;
    }

    // generate String array from jsonArray
    private String[] getStringArrayFromJsonArray(JSONArray jsonArray) {

        String[] stringArray = new String[jsonArray.length()];
        // iterate through each item in jsonArray and add it to stringArray
        for(int i = 0; i < jsonArray.length(); i++) {
            stringArray[i] = jsonArray.optString(i);
            Log.i(TAG, stringArray[i] + " added to array");
        }
        return stringArray;
    }

    // generate int array from jsonArray
    private int[] getIntArrayFromJsonArray(JSONArray jsonArray) {
        int[] intArray = new int[jsonArray.length()];
        // iterate through each item in jsonArray and add it to stringArray
        for(int i = 0; i < jsonArray.length(); i++) {
            try {
                intArray[i] = jsonArray.getInt(i);
            } catch (JSONException e) {
                Log.e(TAG, "error converting jsonArray to int[]", e);
            }
            Log.i(TAG, intArray[i] + " added to array");
        }
        return intArray;
    }

}