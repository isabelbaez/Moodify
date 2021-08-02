package com.example.moodify.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.moodify.LoginActivity;
import com.example.moodify.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.parse.ParseUser;
import com.spotify.sdk.android.auth.AuthorizationClient;

import java.util.ArrayList;

public class ProfileFragment extends Fragment {

    private TextView etName;
    private TextView etStatus;
    private PieChart pcMoods;
    private Button btnLogout;

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

        etName = (TextView) view.findViewById(R.id.etFriendName);
        etStatus = (TextView) view.findViewById(R.id.etFriendStatus);
        btnLogout = (Button) view.findViewById(R.id.btnLogout);

        pcMoods = (PieChart) view.findViewById(R.id.pcFriendMoods);

        etName.setText(currentUser.getUsername());
        etStatus.setText(currentUser.getString("status"));

        ArrayList<PieEntry> value = new ArrayList<>();

        value.add(new PieEntry(Integer.valueOf(currentUser.getString("happiness")), "happiness"));
        value.add(new PieEntry(Integer.valueOf(currentUser.getString("sadness")), "sadness"));
        value.add(new PieEntry(Integer.valueOf(currentUser.getString("anger")), "anger"));
        value.add(new PieEntry(Integer.valueOf(currentUser.getString("energy")), "energy"));
        value.add(new PieEntry(Integer.valueOf(currentUser.getString("chill")), "chill"));

        PieDataSet moodDataSet = new PieDataSet(value, "");
        PieData moodData = new PieData(moodDataSet);

        pcMoods.setData(moodData);

        moodDataSet.setColors(MOOD_COLORS);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                AuthorizationClient.clearCookies(getContext());
                goLoginActivity();
            }
        });
    }

    private void goLoginActivity() {
        Intent i = new Intent(getContext(), LoginActivity.class);
        startActivity(i);
    }

    public static final int[] MOOD_COLORS = {
            Color.rgb(255, 213, 0), Color.rgb(102, 178, 255), Color.rgb(204, 0, 0),
            Color.rgb(255, 145, 0), Color.rgb(204, 153, 255)
    };

}
