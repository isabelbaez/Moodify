package com.example.moodify.connectors;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestHeaders;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.TextHttpResponseHandler;
import com.example.moodify.models.Song;
import com.github.mikephil.charting.utils.Utils;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Headers;

public class SongService {
    private ArrayList<Song> songs = new ArrayList<>();
    private ArrayList<Song> recommendedSongs = new ArrayList<>();

    private SharedPreferences sharedPreferences;
    private RequestQueue queue;
    private Double energyDecimal;
    private Double valenceDecimal;
    private String energy;
    private String valence;

    private String TAG = "SongService";

    //Base code from (working with API calls) from:
    // https://towardsdatascience.com/using-the-spotify-api-with-your-android-application-the-essentials-1a3c1bc36b9e
    // Uses the Volley library for API calls

    public SongService(Context context) {
        sharedPreferences = context.getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(context);
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public ArrayList<Song> getRecommendedSongs() {
        return recommendedSongs;
    }

    //API call to get a song's audio features from its ID and set its mood.
    public void songMood(Song song, String id, final VolleyCallBack callBack) {
        String endpoint = "https://api.spotify.com/v1/audio-features/" + id;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, endpoint, null, response -> {
                    //gets energy (measures intensity and tempo) audio value
                    try {
                        energyDecimal = response.getDouble("energy");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //gets valence (measures positiveness of lyrics audio value
                    try {
                        valenceDecimal = response.getDouble("valence");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //sets energy value as "low", "medium", or "high"
                    if (0.0 <= energyDecimal && energyDecimal <= 0.40) {
                        energy = "low";
                    } else if (0.40 < energyDecimal && energyDecimal <= 0.60) {
                        energy = "medium";
                    } else {
                        energy = "high";
                    }

                    //sets valence value as "low", "medium", or "high"
                    if (0.0 <= valenceDecimal && valenceDecimal <= 0.40) {
                        valence = "low";
                    } else if (0.40 < valenceDecimal && valenceDecimal <= 0.55) {
                        valence = "medium";
                    } else {
                        valence = "high";
                    }
                    // decides mood based on valence measure and energy measure
                    // (eg. low intensity (slow song) and low positiveness of
                    // lyrics would indicate a sad song)
                    if (energy.equals("low")){
                        if (valence.equals("low")){
                            song.setMood("Sad");
                        } else {
                            // low energy but happy/fine lyrics, equal chill song
                            song.setMood("Chill");
                        }
                    } else if (energy.equals("medium")) {
                        if (valence.equals("low")) {
                            // medium energy but negative lyrics, equal sad song
                            song.setMood("Sad");
                        } else {
                            song.setMood("Happy");
                        }
                    } else {
                        if (valence.equals("low")) {
                            // negative lyrics and high energy equal angry
                            song.setMood("Angry");
                        } else {
                            // positive lyrics and high energy equal energized
                            song.setMood("Energized");
                        }
                    }
                    callBack.onSuccess();
                }, error -> {
                    Log.e(TAG, "Error getting and setting song's mood", error);
                }) {
            //Authorization token for request
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = sharedPreferences.getString("token", "");
                String auth = "Bearer " + token;
                headers.put("Authorization", auth);
                return headers;
            }
        };
        queue.add(jsonObjectRequest);
    }

    //Taking some example songs, gets some recommended tracks based of the songs' IDs.
    public ArrayList<Song> getRecommendedTracks(ArrayList<Song> SeedTracks,
                                                final VolleyCallBack callBack) {
        String ids = "";
        // Extract song IDs.
        for (int n = 0; n < 5; n++) {
            Song song = SeedTracks.get(n);
            if (ids.isEmpty()) {
                ids = song.getId();
            } else {
                ids = ids + "," + song.getId();
            }
        }
        //Set query parameter in API endpoint.
        String endpoint = "https://api.spotify.com/v1/recommendations?seed_tracks=" + ids +
                "&seed_artists=&seed_genres=&limit=100";

        //Initialize request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, endpoint, null, response -> {
                    Gson gson = new Gson();
                    JSONArray jsonArray = response.optJSONArray("tracks");

                    for (int n = 0; n < jsonArray.length(); n++) {
                        try {
                            //creates song objects from JSON response
                            JSONObject object = jsonArray.getJSONObject(n);

                            Song song = gson.fromJson(object.toString(), Song.class);

                            //Sets song uri, artist name, and artist id
                            String uri = object.optString("uri");
                            song.setURI(uri);

                            JSONArray jsonArrayArtist = object.optJSONArray("artists");
                            JSONObject artistObject = jsonArrayArtist.getJSONObject(0);

                            String artistName = artistObject.optString("name");
                            String artistID = artistObject.optString("id");
                            song.setArtist(artistName);
                            song.setArtistId(artistID);

                            recommendedSongs.add(song);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    callBack.onSuccess();
                }, error -> {
                    Log.e(TAG, "Error getting recommended tracks", error);

                }) {
            //Authorization token for request
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = sharedPreferences.getString("token", "");
                String auth = "Bearer " + token;
                headers.put("Authorization", auth);
                return headers;
            }
        };
        queue.add(jsonObjectRequest);
        return recommendedSongs;
    }

    //Retrieve's a user's recently played tracks
    public ArrayList<Song> getRecentlyPlayedTracks(final VolleyCallBack callBack) {
        String endpoint = "https://api.spotify.com/v1/me/player/recently-played";

        //Initialize request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, endpoint, null, response -> {
                    Gson gson = new Gson();
                    JSONArray jsonArray = response.optJSONArray("items");

                    for (int n = 0; n < jsonArray.length(); n++) {
                        try {
                            //creates song objects from JSON response
                            JSONObject object = jsonArray.getJSONObject(n);
                            object = object.optJSONObject("track");

                            Song song = gson.fromJson(object.toString(), Song.class);

                            //Sets song uri, artist name, and artist id
                            JSONArray jsonArrayArtist = object.optJSONArray("artists");
                            JSONObject artistObject = jsonArrayArtist.getJSONObject(0);

                            String artistName = artistObject.optString("name");
                            String artistID = artistObject.optString("id");
                            song.setArtist(artistName);
                            song.setArtistId(artistID);

                            songs.add(song);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    callBack.onSuccess();
                }, error -> {
                    Log.e(TAG, "Error getting recently-played tracks", error);
                }) {

            //Authorization token for request
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = sharedPreferences.getString("token", "");
                String auth = "Bearer " + token;
                Log.i("SongService", "auth: " + auth);
                headers.put("Authorization", auth);
                return headers;
            }
        };
        queue.add(jsonObjectRequest);
        return songs;
    }

    public interface VolleyCallBack {
        void onSuccess();
    }
}