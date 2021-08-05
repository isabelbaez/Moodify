package com.example.moodify.fragments;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.moodify.R;
import com.example.moodify.StatusAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class FeedFragment extends Fragment {

    protected List<ParseUser> statuses;
    protected StatusAdapter adapter;
    protected ParseUser currentUser = ParseUser.getCurrentUser();

    protected String filter;

    RecyclerView rvStatuses;

    public FeedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvStatuses = view.findViewById(R.id.rvUsers);

        // initialize the array that will hold posts and create a PostsAdapter
        statuses = new ArrayList<>();
        adapter = new StatusAdapter(getContext(), statuses);

        // set the adapter on the recycler view
        rvStatuses.setAdapter(adapter);

        //get the spinner from the xml.
        Spinner dropdown = view.findViewById(R.id.spinner1);
        //create a list of items for the spinner.
        String[] items = new String[]{"", "Sad", "Happy", "Angry", "Chill", "Energized"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> ArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, items);

        //set the spinners adapter to the previously created one.
        dropdown.setAdapter(ArrayAdapter);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filter = (String) parent.getItemAtPosition(position);
                adapter.clear();
                queryStatuses(filter);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // set the layout manager on the recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvStatuses.setLayoutManager(linearLayoutManager);
    }

    protected void queryStatuses(String filter) {
        Log.i("FeedFragment", "filter: " + filter);
        // specify what type of data we want to query - Post.class
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        // include data referred by user key
        query.include("username");
        // limit query to latest 20 items

        ArrayList<String> friends = (ArrayList<String>) currentUser.get("friends");

        if (friends == null) {
            friends = new ArrayList<>();
        }

        query.whereContainedIn("username", friends);

        if (filter == null || filter.isEmpty()) {
        } else {
            query.whereEqualTo("currentMood", filter);
        }

        query.setLimit(20);
        // order posts by creation date (newest first)
        // start an asynchronous call for posts
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e != null) {
                    Log.e("FeedFragment", "Issue with getting statuses", e);
                    return;
                }
                // for debugging purposes let's print every post description to logcat
                for (ParseUser user : statuses) {
                    Log.i("FeedFragment", "Status: " + user.getString("status") + ", username: " + user.getUsername());
                }
                // save received posts to list and notify adapter of new data
                statuses.addAll(objects);
                adapter.notifyDataSetChanged();
            }
        });
    }
}