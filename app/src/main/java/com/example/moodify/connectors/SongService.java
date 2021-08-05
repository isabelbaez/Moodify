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
    private Song song;

    public SongService(Context context) {
        sharedPreferences = context.getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(context);
    }

    public Song getSong() {
        return song;
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public ArrayList<Song> getRecommendedSongs() {
        return recommendedSongs;
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

    /*public void addTrackQueue2(String uri) {
        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams params = new RequestParams();
        params.put("uri", uri);

        RequestHeaders headers = new RequestHeaders();
        String token = sharedPreferences.getString("token", "");
        String auth = "Bearer " + token;
        headers.put("Authorization", auth);

        client.post("https://api.spotify.com/v1/me/player/queue", new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, String response) {
                        // called when response HTTP status is "200 OK"
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String errorResponse, Throwable t) {
                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    }
        }) {

        };
    }*/

    public void addTrackQueue(String uri){
        String endpoint = "https://api.spotify.com/v1/me/player/queue";

        StringRequest myReq = new StringRequest(Request.Method.POST, endpoint,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("SongService", "response: " + response);
                        //mPostCommentResponse.requestCompleted();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("send help", "plis" + error.networkResponse.statusCode, error);
                        //mPostCommentResponse.requestEndedWithError(error);
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = sharedPreferences.getString("token", "");
                String auth = "Bearer " + token;
                headers.put("Authorization", auth);
                //headers.put("Content-Type", "application/json");
                //headers.put("Accept", "application/json");
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                String token = sharedPreferences.getString("token", "");
                Log.e("SongService", "uri: " + uri);
                params.put("uri", uri);
                //params.put("Content-Type", "application/json");
                //params.put("Authorization", "Bearer " + token);
                Log.e("SongService", "params: " + params);
                return params;
            }
        };
        queue.add(myReq);
    }

    public ArrayList<Song> getRecommendedTracks(ArrayList<Song> SeedTracks, String genres,
                                                final VolleyCallBack callBack) {
        String ids = "";
        String artists = "";

        for (int n = 0; n < 5; n++) {
            Song song = SeedTracks.get(n);
            if (ids.isEmpty()) {
                ids = song.getId();
                artists = song.getArtistId();
            } else {
                ids = ids + "," + song.getId();
                artists = artists + "," + song.getArtistId();
            }
        }

        String endpoint = "https://api.spotify.com/v1/recommendations?seed_tracks=" + ids +
                "&seed_artists=&seed_genres=&limit=50";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, endpoint, null, response -> {
                    Gson gson = new Gson();
                    JSONArray jsonArray = response.optJSONArray("tracks");

                    for (int n = 0; n < jsonArray.length(); n++) {
                        try {
                            JSONObject object = jsonArray.getJSONObject(n);

                            Song song = gson.fromJson(object.toString(), Song.class);

                            String uri = object.optString("uri");
                            song.setURI(uri);

                            JSONArray jsonArrayArtist = object.optJSONArray("artists");
                            JSONObject artistObject = jsonArrayArtist.getJSONObject(0);

                            String artistName = artistObject.optString("name");
                            String artistID = artistObject.optString("id");
                            song.setArtist(artistName);
                            song.setArtistId(artistID);

                            //songMood(song, song.getId(), () -> {});

                            recommendedSongs.add(song);
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
        return recommendedSongs;
    }

   /*public Song getTrack(String id, final VolleyCallBack callBack){
        String endpoint = "https://api.spotify.com/v1/tracks/" + id;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, endpoint, null, response -> {
                    Gson gson = new Gson();


                    JSONObject albumObject = response.optJSONObject("album");
                    String albumID = albumObject.optString("id");
                    song.setAlbumId(albumID);

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
*/
    public void setSongGenres(Song song, String ArtistId, final VolleyCallBack callBack){
        String endpoint = "https://api.spotify.com/v1/artists/" + ArtistId;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, endpoint, null, response -> {
                    Gson gson = new Gson();
                    JSONArray artistGenres = response.optJSONArray("genres");
                    if (artistGenres.length() == 0) {
                        Log.i("SongService", "song genre: " +
                                artistGenres.optString(0));
                    }
                    String genres = "";

                    for (int n = 0; n < artistGenres.length(); n++) {
                        if (genres.isEmpty()) {
                            genres = artistGenres.optString(n);
                        } else {
                            genres = genres + "," + artistGenres.optString(n);
                        }
                    }
                    song.setGenres(genres);
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

                            //song.setURI(uri);

                            String artistName = artistObject.optString("name");
                            String artistID = artistObject.optString("id");
                            song.setArtist(artistName);
                            song.setArtistId(artistID);

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