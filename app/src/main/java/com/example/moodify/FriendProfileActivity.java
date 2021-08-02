package com.example.moodify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.moodify.fragments.ExploreFragment;
import com.example.moodify.fragments.FeedFragment;
import com.example.moodify.fragments.ProfileFragment;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;

public class FriendProfileActivity extends AppCompatActivity {

    ParseUser user;

    TextView etFriendName;
    TextView etFriendStatus;
    PieChart pcFriendMoods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        etFriendName = (TextView) findViewById(R.id.etFriendName);
        etFriendStatus = (TextView) findViewById(R.id.etFriendStatus);
        pcFriendMoods = (PieChart) findViewById(R.id.pcFriendMoods);

        user = Parcels.unwrap(getIntent().getParcelableExtra("user"));

        etFriendName.setText(user.getUsername());
        etFriendStatus.setText(user.getString("status"));

        ArrayList<PieEntry> value = new ArrayList<>();

        value.add(new PieEntry(Integer.valueOf(user.getString("happiness")), "happiness"));
        value.add(new PieEntry(Integer.valueOf(user.getString("sadness")), "sadness"));
        value.add(new PieEntry(Integer.valueOf(user.getString("anger")), "anger"));
        value.add(new PieEntry(Integer.valueOf(user.getString("energy")), "energy"));
        value.add(new PieEntry(Integer.valueOf(user.getString("chill")), "chill"));

        PieDataSet moodDataSet = new PieDataSet(value, "");
        PieData moodData = new PieData(moodDataSet);

        pcFriendMoods.setData(moodData);

        moodDataSet.setColors(MOOD_COLORS);

        setContentView(R.layout.activity_friend_profile);
    }

    public static final int[] MOOD_COLORS = {
            Color.rgb(255, 213, 0), Color.rgb(102, 178, 255), Color.rgb(204, 0, 0),
            Color.rgb(255, 145, 0), Color.rgb(204, 153, 255)
    };
}