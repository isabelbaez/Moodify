package com.example.moodify.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.moodify.R;
import com.example.moodify.adapters.SongsAdapter;
import com.example.moodify.connectors.SongService;
import com.example.moodify.models.Song;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class MixActivity extends AppCompatActivity {

    protected List<Song> songs;
    protected String mood;

    protected SongsAdapter songsAdapter;

    protected ParseUser currentUser = ParseUser.getCurrentUser();

    protected SongService songService;
    protected ArrayList<Song> recentlyPlayedTracks;
    protected ArrayList<Song> recommendedTracks;

    protected TextView tvMood;
    protected ImageButton btnBackExplore;

    RecyclerView rvSongs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mix);

        mood = getIntent().getStringExtra("Mood");
        songService = new SongService(this);
        songs = new ArrayList<Song>();
        songsAdapter = new SongsAdapter(this, songs);

        rvSongs = findViewById(R.id.rvSongs);
        tvMood = findViewById(R.id.tvMood);
        btnBackExplore = findViewById(R.id.btnBackExplore);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        rvSongs.setAdapter(songsAdapter);
        rvSongs.setLayoutManager(linearLayoutManager);

        getTracks(currentUser, mood);

        tvMood.setText(mood + " Mix");

        btnBackExplore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goExploreFragment();
            }
        });
    }

    private void goExploreFragment() {
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("frgToLoad", "explore");
        startActivity(i);
    }

    private void getTracks(ParseUser user, String mood) {
        songService.getRecentlyPlayedTracks(() -> {
            recentlyPlayedTracks = songService.getSongs();
            getRecommendedTracks(user, mood);
            Log.i("MixActivity", "Mood: " + mood);
        });
    }

    private void getRecommendedTracks(ParseUser user, String mood) {
        Log.i("Recommended", "user: " + user.getString("genres"));
        if (recentlyPlayedTracks.size() > 0) {
            songService.getRecommendedTracks(recentlyPlayedTracks, () -> {
                recommendedTracks = songService.getRecommendedSongs();

                for (int n = 0; n < recommendedTracks.size(); n++) {
                    Song song = recommendedTracks.get(n);
                    songService.songMood(song, song.getId(), () -> {

                        if (song.getMood().equals(mood)){
                            songs.add(song);
                            songsAdapter.notifyDataSetChanged();
                        }
                    });
                }
            });
        }
    }
}
