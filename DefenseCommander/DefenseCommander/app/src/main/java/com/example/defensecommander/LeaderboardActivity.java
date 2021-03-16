package com.example.defensecommander;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardActivity extends AppCompatActivity {

    private final List<Player> topPlayerList = new ArrayList<>();
    private LeaderboardAdapter lAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        setupFullScreen();

        RecyclerView recyclerView = findViewById(R.id.leaderboardRecycler);

        Intent intent = getIntent();
        String initial = intent.getStringExtra("initial");
        int score = intent.getIntExtra("score", -1);
        int level = intent.getIntExtra("level", -1);

        if(score != -1 && level != -1)
        {
            DatabaseHandler dbh =
                    new DatabaseHandler(this, level, score, initial);
            new Thread(dbh).start();
        }else
        {
            DatabaseHandler db = new DatabaseHandler(this);
            new Thread(db).start();
        }

        lAdapter = new LeaderboardAdapter(topPlayerList);
        recyclerView.setAdapter(lAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        lAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onPause() {
        super.onPause();
        SoundPlayer.pauseAll();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SoundPlayer.resumeAll();
    }

    public void initializeList(List<Player> playList)
    {
        topPlayerList.clear();
        topPlayerList.addAll(playList);
        lAdapter.notifyDataSetChanged();
    }

    private void setupFullScreen() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    public void doExit(View v)
    {
        SoundPlayer.stopAll();
        finish();
    }





}