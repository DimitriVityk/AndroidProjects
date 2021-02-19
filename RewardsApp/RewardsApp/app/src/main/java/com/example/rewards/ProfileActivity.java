package com.example.rewards;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ProfileActivity extends AppCompatActivity implements View.OnLongClickListener, View.OnClickListener {

    private TextView profileName;
    private TextView profileUserName;
    private TextView profileLocation;
    private TextView profilePointsAwarded;
    private TextView profileDepartment;
    private TextView profilePointToAward;
    private ImageView profileImage;
    private TextView profileStory;
    private TextView profilePosition;
    private String apiKey;
    private Profile profile;
    private List<Reward> listOfrewards;
    private String image64;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent intent = getIntent();
        apiKey = intent.getStringExtra("apiKey");
        profile = (Profile) intent.getSerializableExtra("profile");

        listOfrewards = profile.getRewardList();
        image64 = profile.getImage64();


        profileName = findViewById(R.id.profileName);
        profileUserName = findViewById(R.id.profileUserName);
        profileLocation = findViewById(R.id.profileLocation);
        profileDepartment = findViewById(R.id.profileDepartment);
        profileImage = findViewById(R.id.profileImage);
        profilePointsAwarded = findViewById(R.id.profilePointsAwarded);
        profilePointToAward = findViewById(R.id.profilePointsToAward);
        profileStory = findViewById(R.id.profileStory);
        profilePosition = findViewById(R.id.profilePosition);

        String user = "(" + profile.getUsername() + ")";
        String name = profile.getLastName() + ", " + profile.getFirstName();
        profilePosition.setText(profile.getPosition());
        profileName.setText(name);
        profileUserName.setText(user);
        profileLocation.setText(profile.getLocation());
        profileDepartment.setText(profile.getDepartment());
        String remaining = String.valueOf(profile.getRemainingPoints());
        String awarded = String.valueOf(profile.getPointsAwarded());
        profilePointToAward.setText(remaining);
        profilePointsAwarded.setText(awarded);
        profileStory.setText(profile.getStory());
        textToImage();
        recyclerView = findViewById(R.id.profileRecycler);
        ProfileRewardAdapter pAdapter = new ProfileRewardAdapter(listOfrewards, this);
        recyclerView.setAdapter(pAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.icon);
        this.setTitle("  Your Profile");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.profileDelete:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setIcon(R.drawable.logo);
                builder.setTitle("Delete Profile?");
                builder.setMessage("Delete Profile for " + profile.getFirstName() + " " + profile.getLastName() + "?\n(The Rewards app " +
                        "will be closed upon deletion).");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteProfile();
                    }
                });
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                return super.onOptionsItemSelected(item);
            case R.id.profileEdit:
                Intent editProfileIntent = new Intent(this, EditProfileActivity.class);
                editProfileIntent.putExtra("profile", profile);
                editProfileIntent.putExtra("apiKey", apiKey);
                startActivity(editProfileIntent);
                return true;
            case R.id.profileLeaderboard:
                Intent leaderboardIntent = new Intent(this, LeaderboardActivity.class);
                leaderboardIntent.putExtra("apiKey", apiKey);
                leaderboardIntent.putExtra("currentUser", profile);
                startActivity(leaderboardIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void deleteProfile()
    {
        DeleteProfileAPIRunnable deleteProfile = new DeleteProfileAPIRunnable(this, profile.getUsername() ,apiKey);
        new Thread(deleteProfile).start();
    }

    public void exitApplication()
    {
        finishAffinity();
        System.exit(0);
    }

    public void textToImage() {
        if (image64 == null) return;
        byte[] imageBytes = Base64.decode(image64, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        profileImage.setImageBitmap(bitmap);
    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }
}