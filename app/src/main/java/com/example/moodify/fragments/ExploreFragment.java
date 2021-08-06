package com.example.moodify.fragments;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.moodify.activities.MixActivity;
import com.example.moodify.R;
import com.example.moodify.adapters.UsersAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ExploreFragment extends Fragment {

    protected List<ParseUser> users;

    protected UsersAdapter userAdapter;

    protected EditText etSearch;
    protected ImageView btnSearch;
    protected String username;

    RecyclerView rvUsers;

    protected TextView HappyMix;
    protected TextView SadMix;
    protected TextView AngryMix;
    protected TextView ChillMix;
    protected TextView EnergizedMix;

    protected TextView tvHappyDesc;
    protected TextView tvSadDesc;
    protected TextView tvAngryDesc;
    protected TextView tvChillDesc;
    protected TextView tvEnergizedDesc;

    protected TextView tvHappy;
    protected TextView tvSad;
    protected TextView tvAngry;
    protected TextView tvChill;
    protected TextView tvEnergized;


    public ExploreFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_explore, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // initialize the array that will hold posts and create a PostsAdapter

        HappyMix = view.findViewById(R.id.HappyMix);
        SadMix = view.findViewById(R.id.SadMix);
        AngryMix = view.findViewById(R.id.AngryMix);
        ChillMix = view.findViewById(R.id.ChillMix);
        EnergizedMix = view.findViewById(R.id.EnergizedMix);

        int[] attrs = new int[]{R.attr.selectableItemBackground};
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs);

        int backgroundResource = typedArray.getResourceId(0, 0);
        HappyMix.setBackgroundResource(backgroundResource);
        SadMix.setBackgroundResource(backgroundResource);
        AngryMix.setBackgroundResource(backgroundResource);
        ChillMix.setBackgroundResource(backgroundResource);
        EnergizedMix.setBackgroundResource(backgroundResource);

        tvHappyDesc = view.findViewById(R.id.tvHappyDesc);
        tvSadDesc = view.findViewById(R.id.tvSadDesc);
        tvAngryDesc = view.findViewById(R.id.tvAngryDesc);
        tvChillDesc = view.findViewById(R.id.tvChillDesc);
        tvEnergizedDesc = view.findViewById(R.id.tvEnergizedDesc);

        tvHappy = view.findViewById(R.id.tvHappy);
        tvSad = view.findViewById(R.id.tvSad);
        tvAngry = view.findViewById(R.id.tvAngry);
        tvChill = view.findViewById(R.id.tvChill);
        tvEnergized = view.findViewById(R.id.tvEnergized);


        users = new ArrayList<>();

        userAdapter = new UsersAdapter(getContext(), users);

        // User search things:
        rvUsers = view.findViewById(R.id.rvUsers);
        etSearch = view.findViewById(R.id.etSearch);
        btnSearch = view.findViewById(R.id.btnSearch);
        // set the adapter on the recycler view
        rvUsers.setAdapter(userAdapter);
        // set the layout manager on the recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvUsers.setLayoutManager(linearLayoutManager);
        rvUsers.setVisibility(View.GONE);

        btnSearch.setBackgroundResource(backgroundResource);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userAdapter.clear();
                username = etSearch.getText().toString();
                queryUsers(username);
                //rvUsers.setVisibility(View.GONE);
            }
        });

        HappyMix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goMixActivity("Happy");
            }
        });

        SadMix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goMixActivity("Sad");
            }
        });

        AngryMix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goMixActivity("Angry");
            }
        });

        ChillMix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goMixActivity("Chill");
            }
        });

        EnergizedMix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goMixActivity("Energized");
            }
        });

        etSearch = view.findViewById(R.id.etSearch);
        etSearch.setFocusableInTouchMode(true);

        etSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    rvUsers.setVisibility(View.VISIBLE);
                    userAdapter.clear();

                    HappyMix.setVisibility(View.GONE);
                    SadMix.setVisibility(View.GONE);
                    AngryMix.setVisibility(View.GONE);
                    ChillMix.setVisibility(View.GONE);
                    EnergizedMix.setVisibility(View.GONE);

                    tvHappyDesc.setVisibility(View.GONE);
                    tvSadDesc.setVisibility(View.GONE);
                    tvAngryDesc.setVisibility(View.GONE);
                    tvChillDesc.setVisibility(View.GONE);
                    tvEnergizedDesc.setVisibility(View.GONE);

                    tvHappy.setVisibility(View.GONE);
                    tvSad.setVisibility(View.GONE);
                    tvAngry.setVisibility(View.GONE);
                    tvChill.setVisibility(View.GONE);
                    tvEnergized.setVisibility(View.GONE);

                } else {
                    rvUsers.setVisibility(View.GONE);

                    HappyMix.setVisibility(View.VISIBLE);
                    SadMix.setVisibility(View.VISIBLE);
                    AngryMix.setVisibility(View.VISIBLE);
                    ChillMix.setVisibility(View.VISIBLE);
                    EnergizedMix.setVisibility(View.VISIBLE);

                    tvHappyDesc.setVisibility(View.VISIBLE);
                    tvSadDesc.setVisibility(View.VISIBLE);
                    tvAngryDesc.setVisibility(View.VISIBLE);
                    tvChillDesc.setVisibility(View.VISIBLE);
                    tvEnergizedDesc.setVisibility(View.VISIBLE);

                    tvHappy.setVisibility(View.VISIBLE);
                    tvSad.setVisibility(View.VISIBLE);
                    tvAngry.setVisibility(View.VISIBLE);
                    tvChill.setVisibility(View.VISIBLE);
                    tvEnergized.setVisibility(View.VISIBLE);
                }
            }
        });

        btnSearch = view.findViewById(R.id.btnSearch);
        //Log.i("ExploreFEed", "User: " + username);
    }

    protected void goMixActivity(String mood){
        Intent intent = new Intent(getContext(), MixActivity.class);
        intent.putExtra("Mood", mood);
        startActivity(intent);
    }

    protected void queryUsers(String username) {
        // specify what type of data we want to query - Post.class
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        // include data referred by user key
        query.include("username");
        query.whereEqualTo("username", username);
        // start an asynchronous call for posts
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e != null) {
                    Log.e("ExploreFragment", "Issue with getting users", e);
                    return;
                }
                // for debugging purposes let's print every post description to logcat
                // save received posts to list and notify adapter of new data
                users.addAll(objects);
                userAdapter.notifyDataSetChanged();
            }
        });
    }
}