package com.example.rewards;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LeaderboardActivity extends AppCompatActivity implements View.OnLongClickListener, View.OnClickListener {

    private static final String TAG = "Leaderboard";
    private RecyclerView recyclerView;
    private List<Profile> profileList = new ArrayList<>();
    private LeaderboardAdapter lAdapter;
    private String apiKey;
    private Profile currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.icon);
        this.setTitle("  Leaderboard");

        Intent intent = getIntent();
        apiKey = intent.getStringExtra("apiKey");
        currentUser = (Profile)intent.getSerializableExtra("currentUser");

        GetAllProfilesAPIRunnable getProfiles = new GetAllProfilesAPIRunnable(this, apiKey);
        new Thread(getProfiles).start();

        recyclerView = findViewById(R.id.leaderboardRecycler);
        lAdapter = new LeaderboardAdapter(profileList, this);
        recyclerView.setAdapter(lAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Collections.sort(profileList);
        lAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        Intent intent = getIntent();
        apiKey = intent.getStringExtra("apiKey");
        currentUser = (Profile)intent.getSerializableExtra("currentUser");

        GetAllProfilesAPIRunnable getProfiles = new GetAllProfilesAPIRunnable(this, apiKey);
        new Thread(getProfiles).start();
        super.onResume();
    }

    public void loadProfileList(List<Profile> profList)
    {
        profileList.clear();
        profileList.addAll(profList);
        Collections.sort(profileList);
        lAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        final int pos = recyclerView.getChildLayoutPosition(v);
        Profile clickProfile = profileList.get(pos);
        if(!currentUser.getUsername().equals(clickProfile.getUsername())) {
            Intent rewardIntent = new Intent(this, RewardActivity.class);
            rewardIntent.putExtra("receiverProfile", clickProfile);
            rewardIntent.putExtra("senderProfile", currentUser);
            rewardIntent.putExtra("APIKEY", apiKey);
            startActivity(rewardIntent);
        }
        else
        {

        }
    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }

    public void loginFromLeader(String userName, String password)//logs new user into profile immediately after creating it, called in process method in EditProfileAPIRunnable
    {
        LoginAPIRunnable loginAPI = new LoginAPIRunnable(this, userName, password, apiKey);
        new Thread(loginAPI).start();
    }

    public void login(Profile profile)
    {
        Profile p = profile;
        Intent loginIntent = new Intent(this, ProfileActivity.class);
        loginIntent.putExtra("apiKey", apiKey);
        loginIntent.putExtra("profile", p);
        startActivity(loginIntent);
    }

    @Override
    public void onBackPressed() {
        loginFromLeader(currentUser.getUsername(), currentUser.getPassword());
    }
}