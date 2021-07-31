package com.example.moodify.fragments;

import android.content.SharedPreferences;
import android.graphics.Color;
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
import com.github.mikephil.charting.utils.ColorTemplate;
import com.parse.Parse;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProfileFragment extends Fragment {

    private TextView etName;
    private TextView etStatus;
    private PieChart pcMoods;

    private ParseUser currentUser = ParseUser.getCurrentUser();


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

        PieDataSet moodDataSet = new PieDataSet(value, "");
        PieData moodData = new PieData(moodDataSet);

        pcMoods.setData(moodData);

        ArrayList<Integer> colors = new ArrayList<>();

        moodDataSet.setColors(MOOD_COLORS);

        String Moods = "happiness: " + currentUser.getString("happiness") + ", sadness: " + currentUser.getString("sadness")
                + ", energy: " + currentUser.getString("energy") + ", anger: " + currentUser.getString("anger") +
                ", chill: " + currentUser.getString("chill");

        //etMoods.setText(Moods);
    }

    public static final int[] MOOD_COLORS = {
            Color.rgb(255, 213, 0), Color.rgb(102, 178, 255), Color.rgb(204, 0, 0),
            Color.rgb(255, 145, 0), Color.rgb(204, 153, 255)
    };

}
