package com.example.moodify;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import com.example.moodify.connectors.SongService;
import com.example.moodify.models.Song;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class HappyMixActivity extends AppCompatActivity {

    protected List<Song> happySongs;

    protected SongsAdapter happySongsAdapter;

    protected ParseUser currentUser = ParseUser.getCurrentUser();


    protected SongService songService;
    protected ArrayList<Song> recentlyPlayedTracks;
    protected ArrayList<Song> recommendedTracks;

    RecyclerView rvHappySongs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_happy_mix);

        songService = new SongService(this);

        happySongs = new ArrayList<Song>();

        happySongsAdapter = new SongsAdapter(this, happySongs);

        rvHappySongs = findViewById(R.id.rvHappySongs);

        LinearLayoutManager happyLinearLayoutManager = new LinearLayoutManager(this);

        rvHappySongs.setAdapter(happySongsAdapter);
        rvHappySongs.setLayoutManager(happyLinearLayoutManager);

        getTracks(currentUser);

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
                        }
                    });
                }
                //Log.i("ExploreFragment", "Size: " + happyTracks.size());
            });
        }
    }
}
