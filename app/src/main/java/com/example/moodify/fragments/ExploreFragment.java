package com.example.moodify.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.moodify.R;
import com.example.moodify.StatusAdapter;
import com.example.moodify.UsersAdapter;
import com.example.moodify.connectors.SongService;
import com.example.moodify.models.Song;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ExploreFragment extends Fragment {

    protected List<ParseUser> users;
    protected UsersAdapter adapter;
    protected EditText etSearch;
    protected AppCompatImageButton btnSearch;

    protected String username;

    protected SongService songService;
    protected ArrayList<Song> recentlyPlayedTracks;
    protected ArrayList<Song> recommendedTracks;

    RecyclerView rvUsers;
    RecyclerView rvHappyMix;
    RecyclerView rvSadMix;
    RecyclerView rvAngryMix;
    RecyclerView rvChillMix;
    RecyclerView rvCEnergizedMix;

    public ExploreFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_explore, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // initialize the array that will hold posts and create a PostsAdapter
        songService = new SongService(getContext());
        users = new ArrayList<>();
        adapter = new UsersAdapter(getContext(), users);

        rvUsers = view.findViewById(R.id.rvUsers);
        etSearch = view.findViewById(R.id.etSearch);
        btnSearch = view.findViewById(R.id.btnSearch);

        // set the adapter on the recycler view
        rvUsers.setAdapter(adapter);
        // set the layout manager on the recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvUsers.setLayoutManager(linearLayoutManager);

        rvUsers.setVisibility(View.GONE);
        // query posts from Instagram

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rvUsers.setVisibility(View.VISIBLE);

                adapter.clear();
                username = etSearch.getText().toString();
                queryStatuses(username);
                //rvUsers.setVisibility(View.GONE);
            }
        });
        //Log.i("ExploreFEed", "User: " + username);
    }

    private void getTracks(ParseUser user) {
        songService.getRecentlyPlayedTracks(() -> {
            recentlyPlayedTracks = songService.getSongs();
            getRecommendedTracks(user);

            for (int n = 0; n < recentlyPlayedTracks.size(); n++) {
                Song song = recentlyPlayedTracks.get(n);
                user.saveInBackground();
            }
        });
    }

    private void getRecommendedTracks(ParseUser user) {
        Log.i("Recommended", "user: " + user.getString("genres"));
        if (recentlyPlayedTracks.size() > 0) {
            songService.getRecommendedTracks(recentlyPlayedTracks, user.getString("genres"), () -> {
                recommendedTracks = songService.getRecommendedSongs();
                for (int n = 0; n < recommendedTracks.size(); n++) {
                    Log.i("RecommendedTracks", "track: " + recommendedTracks.get(n).getName());
                }
            });
        }
    }

    protected void queryStatuses(String username) {
        // specify what type of data we want to query - Post.class
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        // include data referred by user key
        query.include("username");
        query.whereEqualTo("username", username);
        // start an asynchronous call for posts
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e != null) {
                    Log.e("ExploreFragment", "Issue with getting users", e);
                    return;
                }
                // for debugging purposes let's print every post description to logcat
                // save received posts to list and notify adapter of new data
                users.addAll(objects);
                adapter.notifyDataSetChanged();
            }
        });
    }
}