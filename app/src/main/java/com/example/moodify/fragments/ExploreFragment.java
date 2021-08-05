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
import com.example.moodify.SongsAdapter;
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
    protected List<Song> happySongs;
    protected List<Song> sadSongs;
    protected List<Song> angrySongs;
    protected List<Song> chillSongs;
    protected List<Song> energizedSongs;

    protected UsersAdapter userAdapter;
    protected SongsAdapter happySongsAdapter;
    protected SongsAdapter sadSongsAdapter;
    protected SongsAdapter angrySongsAdapter;
    protected SongsAdapter chillSongsAdapter;
    protected SongsAdapter energizedSongsAdapter;

    protected ParseUser currentUser = ParseUser.getCurrentUser();


    protected EditText etSearch;
    protected AppCompatImageButton btnSearch;

    protected String username;

    protected SongService songService;
    protected ArrayList<Song> recentlyPlayedTracks;
    protected ArrayList<Song> recommendedTracks;

    RecyclerView rvUsers;
    RecyclerView rvHappySongs;
    RecyclerView rvSadSongs;
    RecyclerView rvAngrySongs;
    RecyclerView rvChillSongs;
    RecyclerView rvEnergizedSongs;

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

        happySongs = new ArrayList<Song>();
        sadSongs = new ArrayList<Song>();
        angrySongs = new ArrayList<Song>();
        chillSongs = new ArrayList<Song>();
        energizedSongs = new ArrayList<Song>();

        userAdapter = new UsersAdapter(getContext(), users);

        happySongsAdapter = new SongsAdapter(getContext(), happySongs);
        sadSongsAdapter = new SongsAdapter(getContext(), sadSongs);
        angrySongsAdapter = new SongsAdapter(getContext(), angrySongs);
        chillSongsAdapter = new SongsAdapter(getContext(), chillSongs);
        energizedSongsAdapter = new SongsAdapter(getContext(), energizedSongs);

        rvHappySongs = view.findViewById(R.id.rvHappySongs);
        rvSadSongs = view.findViewById(R.id.rvSadSongs);
        rvAngrySongs = view.findViewById(R.id.rvAngrySongs);
        rvChillSongs = view.findViewById(R.id.rvChillSongs);
        rvEnergizedSongs = view.findViewById(R.id.rvEnergizedSongs);

        LinearLayoutManager happyLinearLayoutManager = new LinearLayoutManager(getContext());
        LinearLayoutManager sadLinearLayoutManager = new LinearLayoutManager(getContext());
        LinearLayoutManager angryLinearLayoutManager = new LinearLayoutManager(getContext());
        LinearLayoutManager chillLinearLayoutManager = new LinearLayoutManager(getContext());
        LinearLayoutManager energizedLinearLayoutManager = new LinearLayoutManager(getContext());

        rvHappySongs.setAdapter(happySongsAdapter);
        rvHappySongs.setLayoutManager(happyLinearLayoutManager);

        rvSadSongs.setAdapter(sadSongsAdapter);
        rvSadSongs.setLayoutManager(sadLinearLayoutManager);

        rvAngrySongs.setAdapter(angrySongsAdapter);
        rvAngrySongs.setLayoutManager(angryLinearLayoutManager);

        rvChillSongs.setAdapter(chillSongsAdapter);
        rvChillSongs.setLayoutManager(chillLinearLayoutManager);

        rvEnergizedSongs.setAdapter(energizedSongsAdapter);
        rvEnergizedSongs.setLayoutManager(energizedLinearLayoutManager);

        getTracks(currentUser);

        //Log.i("ExploreFragment", "Size: " + happySongs.size());

        // User search things:
        /*rvUsers = view.findViewById(R.id.rvUsers);
        etSearch = view.findViewById(R.id.etSearch);
        btnSearch = view.findViewById(R.id.btnSearch);
        // set the adapter on the recycler view
        rvUsers.setAdapter(userAdapter);
        // set the layout manager on the recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvUsers.setLayoutManager(linearLayoutManager);
        rvUsers.setVisibility(View.GONE);


        /*btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rvUsers.setVisibility(View.VISIBLE);

                userAdapter.clear();
                username = etSearch.getText().toString();
                queryStatuses(username);
                //rvUsers.setVisibility(View.GONE);
            }
        });*/
        //Log.i("ExploreFEed", "User: " + username);
    }

    private void getTracks(ParseUser user) {
        songService.getRecentlyPlayedTracks(() -> {
            recentlyPlayedTracks = songService.getSongs();
            getRecommendedTracks(user);
        });
    }

    private void getRecommendedTracks(ParseUser user) {
        Log.i("Recommended", "user: " + user.getString("genres"));
        if (recentlyPlayedTracks.size() > 0) {
            songService.getRecommendedTracks(recentlyPlayedTracks, user.getString("genres"), () -> {
                recommendedTracks = songService.getRecommendedSongs();

                for (int n = 0; n < recommendedTracks.size(); n++) {
                    Song song = recommendedTracks.get(n);
                    songService.songMood(song, song.getId(), () -> {

                        if (song.getMood() == "Happy"){
                            happySongs.add(song);
                            happySongsAdapter.notifyDataSetChanged();
                        } else if (song.getMood() == "Sad") {
                            sadSongs.add(song);
                            sadSongsAdapter.notifyDataSetChanged();
                        } else if (song.getMood() == "Angry") {
                            angrySongs.add(song);
                            angrySongsAdapter.notifyDataSetChanged();
                        } else if (song.getMood() == "Chill") {
                            chillSongs.add(song);
                            chillSongsAdapter.notifyDataSetChanged();
                        } else {
                            energizedSongs.add(song);
                            energizedSongsAdapter.notifyDataSetChanged();
                        }
                    });
                }
                //Log.i("ExploreFragment", "Size: " + happyTracks.size());
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
                userAdapter.notifyDataSetChanged();
            }
        });
    }
}