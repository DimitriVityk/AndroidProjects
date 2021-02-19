package com.example.rewards;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class RewardActivity extends AppCompatActivity {

    private static final int MAX_LEN = 80;

    private Profile sender;
    private Profile receiver;

    private TextView rewardCommentLabel;
    private TextView rewardName;
    private TextView rewardPoints;
    private TextView rewardDepartment;
    private TextView rewardPosition;
    private TextView rewardBio;
    private ImageView rewardImage;
    private EditText pointsToSend;
    private EditText rewardComment;
    private String image64;
    private String apiKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward);

        Intent intent = getIntent();
        sender = (Profile)intent.getSerializableExtra("senderProfile");
        receiver = (Profile)intent.getSerializableExtra("receiverProfile");
        apiKey = intent.getStringExtra("APIKEY");
        image64 = receiver.getImage64();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.icon);
        this.setTitle("  " + receiver.getFirstName() + " " + receiver.getLastName());

        rewardCommentLabel = findViewById(R.id.rewardCommentLabel);
        rewardBio = findViewById(R.id.rewardBio);
        rewardComment = findViewById(R.id.rewardComment);
        rewardName = findViewById(R.id.rewardName);
        rewardPoints = findViewById(R.id.rewardPoints);
        rewardDepartment = findViewById(R.id.rewardDepartment);
        rewardPosition = findViewById(R.id.rewardPosition);
        rewardImage = findViewById(R.id.rewardImage);
        pointsToSend = findViewById(R.id.pointsToSend);

        String name = receiver.getLastName() + ", " + receiver.getFirstName();
        rewardName.setText(name);

        String pointString = String.valueOf(receiver.getPointsAwarded());
        rewardPoints.setText(pointString);

        rewardDepartment.setText(receiver.getDepartment());
        rewardPosition.setText(receiver.getPosition());
        rewardBio.setText(receiver.getStory());
        textToImage();
        setupEditText();

        rewardComment.setHint("Add comment for " + receiver.getFirstName());
    }

    public void textToImage() {
        if (image64 == null) return;
        byte[] imageBytes = Base64.decode(image64, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        rewardImage.setImageBitmap(bitmap);
    }

    private void setupEditText() {

        rewardComment.setFilters(new InputFilter[] {
                new InputFilter.LengthFilter(MAX_LEN) // Specifies a max text length
        });

        rewardComment.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void afterTextChanged(Editable s) {
                        // This one executes upon completion of typing a character
                        int len = s.toString().length();
                        String countText = "Comment: (" + len + " of " + MAX_LEN + ")";
                        rewardCommentLabel.setText(countText);
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start,
                                                  int count, int after) {
                        // Nothing to do here
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start,
                                              int before, int count) {
                        // Nothing to do here
                    }
                });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.reward_menu, menu);
        return true;
    }

    public void pointsError()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.logo);
        builder.setTitle("Insufficient Points");
        builder.setMessage("You can't send more points than you have, negative points, or zero points");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void digitError()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.logo);
        builder.setTitle("Not a valid point value");
        builder.setMessage("You can only send positive integers");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void commentError()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.logo);
        builder.setTitle("Not a Valid Comment");
        builder.setMessage("You must provide a comment");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        boolean isDigit = true;
        boolean commentFilled = true;
        if (rewardComment.getText().toString().equals("")) { commentFilled = false; }
        int rewardAmount;
        String giverName = sender.getFirstName() + " " + sender.getLastName();
        String amount = pointsToSend.getText().toString();
        if (amount.equals("")) { isDigit = false; }
        char[] chars = amount.toCharArray();
        for(char c : chars)
        {
            if(!Character.isDigit(c))
            {
                isDigit = false;
            }
        }
        if(isDigit) {
            rewardAmount = Integer.parseInt(amount);
            if (rewardAmount > sender.getRemainingPoints() || rewardAmount < 1) {
                pointsError();
            } else {
                if(commentFilled) {

                    RewardsAPIRunnable sendReward = new RewardsAPIRunnable(this, apiKey, receiver.getUsername(), sender.getUsername(), giverName, rewardAmount, rewardComment.getText().toString());
                    new Thread(sendReward).start();
                    Intent leaderboardIntent = new Intent(this, LeaderboardActivity.class);
                    leaderboardIntent.putExtra("apiKey", apiKey);
                    leaderboardIntent.putExtra("currentUser", sender);
                    startActivity(leaderboardIntent);
                }
                else
                {
                    commentError();
                }
            }
        } else
        {
            digitError();
        }
        return super.onOptionsItemSelected(item);
    }

}