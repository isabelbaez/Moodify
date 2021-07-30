package com.example.moodify.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.moodify.R;
import com.example.moodify.connectors.SongService;
import com.example.moodify.models.Song;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.parse.Parse;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Map;
import java.util.Set;

public class ProfileFragment extends Fragment {

    private TextView etName;
    private TextView etStatus;
    private TextView etMoods;
    private Song song;
    private Integer count;

    private SongService songService;
    private ArrayList<Song> recentlyPlayedTracks;

    private ArrayList<String> moods;

    private Map<String, Integer> rawMoods;

    private Integer Happy = 0;
    private Integer Sad = 0;
    private Integer Angry = 0;
    private Integer Energized = 0;
    private Integer Chill = 0;

    private Integer total;
    private ParseUser currentUser = ParseUser.getCurrentUser();

    private Integer happiness = 0;


    private Map<String, Double> finalMoods;

    private PieChart pcMoods;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        songService = new SongService(getContext());
        etName = (TextView) view.findViewById(R.id.etName);
        etStatus = (TextView) view.findViewById(R.id.etStatus);
        //etMoods = (TextView) view.findViewById(R.id.etMoods);

        pcMoods = (PieChart) view.findViewById(R.id.pcMoods);

        Log.i("aja","aja");

        etName.setText(currentUser.getUsername());
        etStatus.setText(currentUser.getString("status"));

        //Map<String, Double> plis = (Map<String, Double>) user.getParseObject("Moods");

        ArrayList<PieEntry> value = new ArrayList<>();

        value.add(new PieEntry(Integer.valueOf(currentUser.getString("happiness")), "happiness"));
        value.add(new PieEntry(Integer.valueOf(currentUser.getString("sadness")), "sadness"));
        value.add(new PieEntry(Integer.valueOf(currentUser.getString("anger")), "anger"));
        value.add(new PieEntry(Integer.valueOf(currentUser.getString("energy")), "energy"));
        value.add(new PieEntry(Integer.valueOf(currentUser.getString("chill")), "chill"));

        PieDataSet moodDataSet = new PieDataSet(value, "Moods");
        PieData moodData = new PieData(moodDataSet);

        pcMoods.setData(moodData);

        String Moods = "happiness: " + currentUser.getString("happiness") + ", sadness: " + currentUser.getString("sadness")
                + ", energy: " + currentUser.getString("energy") + ", anger: " + currentUser.getString("anger") +
                ", chill: " + currentUser.getString("chill");

        //etMoods.setText(Moods);
    }

}
