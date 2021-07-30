package com.example.moodify.connectors;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.moodify.models.Song;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SongService {
    private ArrayList<Song> songs = new ArrayList<>();
    private ArrayList<String> moods = new ArrayList<>();

    private SharedPreferences sharedPreferences;
    private RequestQueue queue;
    private Double energyDecimal;
    private Double valenceDecimal;
    private String energy;
    private String valence;


    public SongService(Context context) {
        sharedPreferences = context.getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(context);
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public ArrayList<String> getMoods() {
       for (int n = 0; n < songs.size(); n++) {
           songMood(songs.get(n), songs.get(n).getId(), ()-> {});
       }
        return moods;
    }

    public void songMood(Song song, String id, final VolleyCallBack callBack) {
        String endpoint = "https://api.spotify.com/v1/audio-features/" + id;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, endpoint, null, response -> {
                    try {
                        energyDecimal = response.getDouble("energy");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        valenceDecimal = response.getDouble("valence");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (0.0 <= energyDecimal && energyDecimal <= 0.33) {
                        energy = "low";
                    } else if (0.33 < energyDecimal && energyDecimal <= 0.66) {
                        energy = "medium";
                    } else {
                        energy = "high";
                    }

                    if (0.0 <= valenceDecimal && valenceDecimal <= 0.33) {
                        valence = "low";
                    } else if (0.33 < valenceDecimal && valenceDecimal <= 0.66) {
                        valence = "medium";
                    } else {
                        valence = "high";
                    }

                    if (energy.equals("low")){
                        if (valence.equals("low")){
                            song.setMood("Sad");
                        } else {
                            song.setMood("Chill");
                        }
                    } else if (energy.equals("medium")) {
                        if (valence.equals("low")) {
                            song.setMood("Sad");
                        } else if (valence.equals("medium")){
                            song.setMood("Happy");
                        } else {
                            song.setMood("Happy");
                        }
                    } else {
                        if (valence.equals("low")) {
                            song.setMood("Angry");
                        } else {
                            song.setMood("Energized");
                        }
                    }
                    callBack.onSuccess();
                }, error -> {
                    // TODO: Handle error

                }) {
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

    public ArrayList<Song> getRecentlyPlayedTracks(final VolleyCallBack callBack) {
        String endpoint = "https://api.spotify.com/v1/me/player/recently-played";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, endpoint, null, response -> {
                    Gson gson = new Gson();
                    JSONArray jsonArray = response.optJSONArray("items");

                    for (int n = 0; n < jsonArray.length(); n++) {
                        try {
                            JSONObject object = jsonArray.getJSONObject(n);
                            object = object.optJSONObject("track");
                            Song song = gson.fromJson(object.toString(), Song.class);

                            JSONArray jsonArrayArtist = object.optJSONArray("artists");
                            JSONObject artistObject = jsonArrayArtist.getJSONObject(0);

                            String artistName = artistObject.optString("name");

                            song.setArtist(artistName);

                            //songMood(song, song.getId(), () -> {});

                            songs.add(song);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    callBack.onSuccess();
                }, error -> {
                    // TODO: Handle error

                }) {
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
        return songs;
    }

    public interface VolleyCallBack {
        void onSuccess();
    }

}