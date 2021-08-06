package com.example.moodify.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.moodify.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;

public class FriendProfileActivity extends AppCompatActivity {

    ParseUser user;

    TextView etFriendName;
    TextView etFriendStatus;
    PieChart pcFriendMoods;
    ImageButton btnBack;

    TextView etNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_friend_profile);

        etFriendName = findViewById(R.id.etFriendName);
        etFriendStatus = findViewById(R.id.etFriendStatus);
        pcFriendMoods = findViewById(R.id.pcFriendMoods);
        btnBack = findViewById(R.id.btnBack);

        user = Parcels.unwrap(getIntent().getParcelableExtra("user"));

        etFriendName.setText(user.getUsername());
        etFriendStatus.setText(user.getString("status"));

        Description desc = new Description();
        desc.setText("");
        pcFriendMoods.setDescription(desc);

        ArrayList<PieEntry> value = new ArrayList<>();

        value.add(new PieEntry(Integer.valueOf(user.getString("happiness")), "happiness"));
        value.add(new PieEntry(Integer.valueOf(user.getString("sadness")), "sadness"));
        value.add(new PieEntry(Integer.valueOf(user.getString("anger")), "anger"));
        value.add(new PieEntry(Integer.valueOf(user.getString("energy")), "energy"));
        value.add(new PieEntry(Integer.valueOf(user.getString("chill")), "chill"));

        PieDataSet moodDataSet = new PieDataSet(value, "");
        PieData moodData = new PieData(moodDataSet);

        pcFriendMoods.setData(moodData);

        etNumber = (TextView) findViewById(R.id.etNumber);

        ArrayList<String> following = (ArrayList<String>) user.get("friends");
        Integer followingNum = following.size();

        etNumber.setText(followingNum.toString());

        moodDataSet.setColors(MOOD_COLORS);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goMainActivity();
            }
        });
    }

    public static final int[] MOOD_COLORS = {
            Color.rgb(255, 213, 0), Color.rgb(102, 178, 255), Color.rgb(204, 0, 0),
            Color.rgb(255, 145, 0), Color.rgb(204, 153, 255)
    };

    private void goMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}