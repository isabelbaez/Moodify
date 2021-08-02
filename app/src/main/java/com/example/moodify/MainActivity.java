package com.example.moodify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.moodify.R;
import com.example.moodify.connectors.SongService;
import com.example.moodify.connectors.UserService;
import com.example.moodify.fragments.ExploreFragment;
import com.example.moodify.fragments.FeedFragment;
import com.example.moodify.fragments.ProfileFragment;
import com.example.moodify.models.Song;
import com.example.moodify.models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final String CLIENT_ID = "1124ddefbcad484993f7f56399a98ffb";
    private static final int REQUEST_CODE = 1337;
    private static final String REDIRECT_URI = "com.example.moodify://callback";
    private SpotifyAppRemote mSpotifyAppRemote;

    private SharedPreferences.Editor editor;
    private SharedPreferences msharedPreferences;
    private RequestQueue queue;

    private SongService songService;
    private ArrayList<Song> recentlyPlayedTracks;

    private ParseUser currentUser = ParseUser.getCurrentUser();

    private static final String SCOPES = "user-read-recently-played,user-library-modify," +
            "user-read-email,user-read-private, streaming";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        songService = new SongService(this);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getSupportActionBar().hide();

        setContentView(R.layout.activity_main);

        if (currentUser != null) {
            //);
        } else {
            goLogin();
        }

        authenticateSpotify();

        msharedPreferences = this.getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(this);

        currentUser.put("happiness", "0");
        currentUser.put("sadness", "0");
        currentUser.put("anger", "0");
        currentUser.put("energy", "0");
        currentUser.put("chill", "0");
        currentUser.saveInBackground();

        getTracks(currentUser);
        //currentUser.saveInBackground();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);

        final FragmentManager fragmentManager = getSupportFragmentManager();
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        // handle navigation selection
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment fragment;
                        switch (item.getItemId()) {
                            case R.id.feed_action:
                                fragment = new FeedFragment();
                                break;
                            case R.id.explore_action:
                                fragment = new ExploreFragment();
                                break;
                            case R.id.profile_action:
                            default:
                                fragment = new ProfileFragment();
                                break;
                        }
                        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                        return true;
                    }
                });

        bottomNavigationView.setSelectedItemId(R.id.feed_action);

        /*Integer total = Integer.valueOf(currentUser.getString("numberTracks"));

        Double happiness = (Integer.valueOf(currentUser.getString("happiness")).doubleValue() - 1)
                /total.doubleValue();
        currentUser.put("happiness", happiness.toString());
        currentUser.saveInBackground();

        Double sadness = (Integer.valueOf(currentUser.getString("sadness")).doubleValue() - 1)
                /total.doubleValue();
        currentUser.put("sadness", sadness.toString());
        currentUser.saveInBackground();

        Double energy = (Integer.valueOf(currentUser.getString("energy")).doubleValue() - 1)
                /total.doubleValue();
        currentUser.put("energy", energy.toString());
        currentUser.saveInBackground();

        Double anger = (Integer.valueOf(currentUser.getString("anger")).doubleValue() - 1)
                /total.doubleValue();
        currentUser.put("anger", anger.toString());
        currentUser.saveInBackground();

        Double chill = (Integer.valueOf(currentUser.getString("chill")).doubleValue() - 1)
                /total.doubleValue();
        currentUser.put("chill", chill.toString());
        currentUser.saveInBackground();*/
    }

    private void getTracks(ParseUser user) {

        songService.getRecentlyPlayedTracks(() -> {
            recentlyPlayedTracks = songService.getSongs();
            updateSong();

            Integer total = recentlyPlayedTracks.size();
            user.put("numberTracks",total);
            user.saveInBackground();
            //user.put("RecentSongs", recentlyPlayedTracks);

            for (int n = 0; n < recentlyPlayedTracks.size(); n++) {
                Song song = recentlyPlayedTracks.get(n);
                setMood(user, song);
                user.saveInBackground();
            }

            Log.d("USer test: ", user.toString());
        });
    }

    private void setMood(ParseUser user, Song song) {

        if (recentlyPlayedTracks.size() > 0) {
            //ParseUser user = ParseUser.getCurrentUser();

            songService.songMood(song, song.getId(), () -> {
                Log.i("PLIS", "woo: " + song.getMood());
                if (song.getMood().equals("Happy")) {
                    Integer count = Integer.valueOf(user.getString("happiness")) + 1;
                    user.put("happiness", count.toString());
                    user.saveInBackground();

                } else if (song.getMood().equals("Sad")) {
                    Integer count = Integer.valueOf(user.getString("sadness")) + 1;
                    user.put("sadness", count.toString());
                    user.saveInBackground();

                } else if (song.getMood().equals("Angry")) {
                    Integer count = Integer.valueOf(user.getString("anger")) + 1;
                    user.put("anger", count.toString());
                    user.saveInBackground();

                } else if (song.getMood().equals("Energized")) {
                    Integer count = Integer.valueOf(user.getString("energy")) + 1;
                    user.put("energy", count.toString());
                    user.saveInBackground();

                } else {
                    Integer count = Integer.valueOf(user.getString("chill")) + 1;
                    user.put("chill", count.toString());
                    user.saveInBackground();
                }
            });
        }
    }

    private void updateSong() {

        if (recentlyPlayedTracks.size() > 0) {

            ParseUser user = ParseUser.getCurrentUser();
            Song firstSong = recentlyPlayedTracks.get(0);

            songService.songMood(firstSong, firstSong.getId(), () -> {
                user.put("status", "Feeling " + firstSong.getMood() + " and listening to " +
                        firstSong.getName() + " - " + firstSong.getArtist());
                user.saveInBackground();
            });
        }
    }

    private void goLogin() {
        Intent newintent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(newintent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.connect(this, connectionParams,
                new Connector.ConnectionListener() {

                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        Log.d("MainActivity", "Connected! Yay!");
                        // Now you can start interacting with App Remote
                        connected();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e("MainActivity", throwable.getMessage(), throwable);
                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });
    }

    private void connected() {
        // Then we will write some more code here.
        //mSpotifyAppRemote.getPlayerApi().play("spotify:playlist:37i9dQZF1DX2sUQwD7tbmL");
        Log.d("TAG", mSpotifyAppRemote.getUserApi().toString());
    }

    private void waitForUserInfo() {

        UserService userService = new UserService(queue, msharedPreferences);
        userService.get(() -> {
            User user = userService.getUser();
            editor = getSharedPreferences("SPOTIFY", 0).edit();
            editor.putString("userid", user.id);
            Log.d("STARTING", "GOT USER INFORMATION");
            // We use commit instead of apply because we need the information stored immediately
            editor.commit();

            Log.i("I burn you?", "You melt me.");
            //startCheckActivity();
        });
    }

    private void authenticateSpotify() {
        AuthorizationRequest.Builder builder =
                new AuthorizationRequest.Builder(CLIENT_ID, AuthorizationResponse.Type.TOKEN,
                        REDIRECT_URI);
        builder.setScopes(new String[]{SCOPES});
        AuthorizationRequest request = builder.build();
        AuthorizationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    @Override
    protected void onStop() {
        super.onStop();
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.i("hello", "goodbye");
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, intent);

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    editor = getSharedPreferences("SPOTIFY", 0).edit();
                    editor.putString("token", response.getAccessToken());
                    Log.d("STARTING", "GOT AUTH TOKEN");
                    editor.apply();
                    waitForUserInfo();
                    break;
                // Auth flow returned an error
                case ERROR:
                    break;
                // Most likely auth flow was cancelled
                default:
                    // Handle other cases
            }
        }
    }
}