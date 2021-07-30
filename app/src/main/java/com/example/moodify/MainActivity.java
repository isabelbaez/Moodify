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

    private static final String SCOPES = "user-read-recently-played,user-library-modify,user-read-email,user-read-private, streaming";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        songService = new SongService(this);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_main);

        ParseUser currentUser = ParseUser.getCurrentUser();
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

        SharedPreferences sharedPreferences = this.getSharedPreferences("SPOTIFY", 0);

        getTracks(currentUser);
        currentUser.saveInBackground();

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

            //setMood(user, recentlyPlayedTracks.get(0));
            //setMood(recentlyPlayedTracks.get(0));

            Integer total = recentlyPlayedTracks.size();
            user.put("numberTracks",total);
            user.saveInBackground();
            //user.put("RecentSongs", recentlyPlayedTracks);

            for (int n = 0; n < recentlyPlayedTracks.size(); n++) {
                Song song = recentlyPlayedTracks.get(n);
                setMood(user, song);
                user.saveInBackground();
            }

            //user.put("moods", moods);


            /*Double happiness = (Integer.valueOf(user.getString("happiness")).doubleValue() - 1)
                    /total.doubleValue();
            user.put("happiness", happiness.toString());

            Double sadness = (Integer.valueOf(user.getString("sadness")).doubleValue() - 1)
                    /total.doubleValue();
            user.put("sadness", sadness.toString());

            Double energy = (Integer.valueOf(user.getString("energy")).doubleValue() - 1)
                    /total.doubleValue();
            user.put("energy", energy.toString());

            Double anger = (Integer.valueOf(user.getString("anger")).doubleValue() - 1)
                    /total.doubleValue();
            user.put("anger", anger.toString());

            Double chill = (Integer.valueOf(user.getString("chill")).doubleValue() - 1)
                    /total.doubleValue();
            user.put("chill", chill.toString());*/

            /*finalMoods = new Map<String, Double>() {
                @Override
                public int size() {
                    return 0;
                }

                @Override
                public boolean isEmpty() {
                    return false;
                }

                @Override
                public boolean containsKey(@Nullable Object key) {
                    return false;
                }

                @Override
                public boolean containsValue(@Nullable Object value) {
                    return false;
                }

                @Nullable
                @Override
                public Double get(@Nullable Object key) {
                    return null;
                }

                @Nullable
                @Override
                public Double put(String key, Double value) {
                    return null;
                }

                @Nullable
                @Override
                public Double remove(@Nullable Object key) {
                    return null;
                }

                @Override
                public void putAll(@NonNull Map<? extends String, ? extends Double> m) {

                }

                @Override
                public void clear() {

                }

                @NonNull
                @Override
                public Set<String> keySet() {
                    return null;
                }

                @NonNull
                @Override
                public Collection<Double> values() {
                    return null;
                }

                @NonNull
                @Override
                public Set<Entry<String, Double>> entrySet() {
                    return null;
                }
            };*/

            /*rawMoods = new Map<String, Integer>() {
                @Override
                public int size() {
                    return 0;
                }

                @Override
                public boolean isEmpty() {
                    return false;
                }

                @Override
                public boolean containsKey(@Nullable Object key) {
                    return false;
                }

                @Override
                public boolean containsValue(@Nullable Object value) {
                    return false;
                }

                @Nullable
                @Override
                public Integer get(@Nullable Object key) {
                    return null;
                }

                @Nullable
                @Override
                public Integer put(String key, Integer value) {
                    return null;
                }

                @Nullable
                @Override
                public Integer remove(@Nullable Object key) {
                    return null;
                }

                @Override
                public void putAll(@NonNull Map<? extends String, ? extends Integer> m) {

                }

                @Override
                public void clear() {

                }

                @NonNull
                @Override
                public Set<String> keySet() {
                    return null;
                }

                @NonNull
                @Override
                public Collection<Integer> values() {
                    return null;
                }

                @NonNull
                @Override
                public Set<Entry<String, Integer>> entrySet() {
                    return null;
                }
            };

            rawMoods.put("Sad", Integer.valueOf(0));
            rawMoods.put("Happy", Integer.valueOf(0));
            rawMoods.put("Energized", Integer.valueOf(0));
            rawMoods.put("Angry", Integer.valueOf(0));
            rawMoods.put("Chill", Integer.valueOf(0));*/

           /* Happy = 0;
            Sad = 0;
            Angry = 0;
            Energized = 0;
            Chill = 0;*/

            /*Integer zero = 2;

            user.put("happiness", zero.toString());
            user.put("sadness", zero.toString());
            user.put("anger", zero.toString());
            user.put("chill", zero.toString());
            user.put("energy", zero.toString());*/

            /*for (int n = 0; n < recentlyPlayedTracks.size(); n++) {

                Song song = recentlyPlayedTracks.get(n);
                songService.songMood(recentlyPlayedTracks.get(n),recentlyPlayedTracks.get(n).getId(),() -> {
                    //count = rawMoods.get(song.getMood());

                    if (song.getMood().equals("Happy")) {
                        Integer count = Integer.valueOf(user.getString("happiness")) + 1;
                        user.put("happiness", count.toString());
                    } else if (song.getMood().equals("Sad")) {
                        Integer count = Integer.valueOf(user.getString("sadness")) + 1;
                        user.put("sadness", count.toString());
                    } else if (song.getMood().equals("Angry")) {
                        Integer count = Integer.valueOf(user.getString("anger")) + 1;
                        user.put("anger", count.toString());
                    } else if (song.getMood().equals("Energized")) {
                        Integer count = Integer.valueOf(user.getString("energy")) + 1;
                        user.put("energy", count.toString());
                    } else {
                        Integer count = Integer.valueOf(user.getString("chill")) + 1;
                        user.put("chill", count.toString());
                    };

                    //Log.d("Profile", "Mood: " + song.getMood());
                    //rawMoods.put(song.getMood(), count + 1);
                });
            }

            Log.i("plis", "plis: " + Integer.valueOf(user.getString("happiness")));

            Double happiness = (Integer.valueOf(user.getString("happiness")).doubleValue() - 1)
                    /total.doubleValue();
            user.put("happiness", happiness.toString());

            Double sadness = (Integer.valueOf(user.getString("sadness")).doubleValue() - 1)
                    /total.doubleValue();
            user.put("sadness", sadness.toString());

            Double energy = (Integer.valueOf(user.getString("energy")).doubleValue() - 1)
                    /total.doubleValue();
            user.put("energy", energy.toString());

            Double anger = (Integer.valueOf(user.getString("anger")).doubleValue() - 1)
                    /total.doubleValue();
            user.put("anger", anger.toString());

            Double chill = (Integer.valueOf(user.getString("chill")).doubleValue() - 1)
                    /total.doubleValue();
            user.put("chill", chill.toString());*/

            /*for (Map.Entry<String, Integer> entry : rawMoods.entrySet()) {
                String key = entry.getKey();
                Integer val = entry.getValue();
                Integer total = recentlyPlayedTracks.size();
                Double newVal = val.doubleValue()/total.doubleValue();
                finalMoods.put(key, newVal);
            }*/
        });

        Log.d("plis", "Plis " + user.getUsername());
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
    };

    private void updateSong() {
        if (recentlyPlayedTracks.size() > 0) {
            ParseUser user = ParseUser.getCurrentUser();

            Song firstSong = recentlyPlayedTracks.get(0);

            songService.songMood(firstSong, firstSong.getId(), () -> {
                user.put("status", "Feeling " + firstSong.getMood() + " and listening to " +
                        firstSong.getName() + " - " + firstSong.getArtist());
                user.saveInBackground();
            });

            /*Song song = recentlyPlayedTracks.get(n);

            songService.songMood(recentlyPlayedTracks.get(n),recentlyPlayedTracks.get(n).getId(),() -> {
                    //count = rawMoods.get(song.getMood());

                    if (song.getMood().equals("Happy")) {
                        Integer count = Integer.valueOf(user.getString("happiness")) + 1;
                        user.put("happiness", count.toString());
                    } else if (song.getMood().equals("Sad")) {
                        Integer count = Integer.valueOf(user.getString("sadness")) + 1;
                        user.put("sadness", count.toString());
                    } else if (song.getMood().equals("Angry")) {
                        Integer count = Integer.valueOf(user.getString("anger")) + 1;
                        user.put("anger", count.toString());
                    } else if (song.getMood().equals("Energized")) {
                        Integer count = Integer.valueOf(user.getString("energy")) + 1;
                        user.put("energy", count.toString());
                    } else {
                        Integer count = Integer.valueOf(user.getString("chill")) + 1;
                        user.put("chill", count.toString());
                    };
                    //Log.d("Profile", "Mood: " + song.getMood());
                    //rawMoods.put(song.getMood(), count + 1);
                });
            }*/

            //Log.i("plis", "plis: " + Integer.valueOf(user.getString("happiness")));

            //song.setMood(mood);
        }
    }

    private void goLogin() {
        Intent newintent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(newintent);
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
        });
    }

    private void authenticateSpotify() {
        AuthorizationRequest.Builder builder =
                new AuthorizationRequest.Builder(CLIENT_ID, AuthorizationResponse.Type.TOKEN, REDIRECT_URI);
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