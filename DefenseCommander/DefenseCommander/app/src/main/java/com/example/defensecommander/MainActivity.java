package com.example.defensecommander;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static int screenHeight;
    public static int screenWidth;
    private ConstraintLayout layout;
    private TextView scoreText, levelText;
    private ImageView gameOver;
    private MissileMaker missileMaker = null;
    private int score = 0;
    private final ArrayList<Missile> activeMissiles = new ArrayList<>();
    private final ArrayList<Interceptor> activeInterceptors = new ArrayList<>();
    private final ArrayList<Base> activeBases = new ArrayList<>();
    private static final String TAG = "MainActivity";
    private final MainActivity mainActivity = this;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scoreText = findViewById(R.id.scoreText);
        levelText = findViewById(R.id.levelText);
        layout = findViewById(R.id.layout);
//        initials = findViewById(R.id.dialogInitials);
//        errorText = findViewById(R.id.dialogError);
        ImageView launcherOne = findViewById(R.id.baseOne);
        ImageView launcherTwo = findViewById(R.id.baseTwo);
        ImageView launcherThree = findViewById(R.id.baseThree);
        gameOver = findViewById(R.id.gameover);
        Base baseOne = new Base(launcherOne);
        Base baseTwo = new Base(launcherTwo);
        Base baseThree = new Base(launcherThree);
        activeBases.add(baseOne);
        activeBases.add(baseTwo);
        activeBases.add(baseThree);

        layout.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                handleTouch(motionEvent.getX(), motionEvent.getY());
            }
            return false;
        });
        setupFullScreen();
        getScreenDimensions();
        //ViewGroup clouds = findViewById(R.id.clouds);
        new ScrollingBackground(this,
                layout, R.drawable.clouds, 30000);

        missileMaker = new MissileMaker(this, screenWidth, screenHeight);
        new Thread(missileMaker).start();
    }

    @Override
    protected void onPause() {
        SoundPlayer.pauseAll();
        pauseAllMissiles();
        super.onPause();
    }

    @Override
    protected void onResume() {
        SoundPlayer.resumeAll();
        resumeAllMissiles();
        super.onResume();
    }

    public void handleTouch(float xLoc, float yLoc) {
        if(!activeBases.isEmpty() && yLoc < screenHeight * .80) {
            Base shootingBase = pickShootingBase(xLoc, yLoc);
            launchInterceptor(shootingBase, xLoc, yLoc);

        }
    }

    public void pauseAllMissiles()
    {
        missileMaker.onPause();
        ArrayList<Missile> temp = new ArrayList<>(activeMissiles);
        for(Missile missile : temp)
        {
            missile.pause();
        }
    }

    public void resumeAllMissiles() {
        missileMaker.onResume();
        ArrayList<Missile> temp = new ArrayList<>(activeMissiles);
        for(Missile missile : temp)
        {
            missile.resume();
        }
    }

    public void launchInterceptor(Base shootingBase, float xLoc, float yLoc)
    {
        if(!(activeInterceptors.size() >= 3)) {
            Interceptor i = new Interceptor(this, shootingBase, xLoc, yLoc);
            activeInterceptors.add(i);
            SoundPlayer.start("launch_interceptor");
            i.launch();
        }
    }

    private Base pickShootingBase(float xLoc, float yLoc)
    {
        double shortestDistance = 1000000.00;
        Base shootingBase = null;
        for(Base base : activeBases)
        {
            double baseX = base.getX();
            double baseY = base.getY();
            double distance = calculateDistance(baseX, baseY, xLoc, yLoc);
            if(distance < shortestDistance)
            {
                shortestDistance = distance;
                shootingBase = base;
            }
        }
        return shootingBase;
    }

    public static float calculateAngle(double x1, double y1, double x2, double y2) {
        double angle = Math.toDegrees(Math.atan2(x2 - x1, y2 - y1));
        // Keep angle between 0 and 360
        angle = angle + Math.ceil(-angle / 360) * 360;
        return (float) (190.0f - angle);
    }

    public static Double calculateDistance(double x1, double y1, double x2, double y2)
    {
        return Math.sqrt(Math.pow((x2-x1), 2)+ Math.pow((y2 - y1), 2));
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

    private void getScreenDimensions() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;
    }

    public void removeMissile(Missile m) {
        activeMissiles.remove(m);
        layout.removeView(m.getImageView());
    }

    public void addMissile(Missile m)
    {
        activeMissiles.add(m);
    }

    public void applyMissileBlast(Missile missile)
    {
        Base hitBase = null;
        for (Base base : activeBases)
        {
            double baseX = base.getX();
            double baseY = base.getY();
            double distance = calculateDistance(baseX, baseY, missile.getX(), missile.getY());
            if(distance < 250)
            {
                hitBase = base;
                base.destruct(this);
            } else
            {
                SoundPlayer.start("missile_miss");
            }
        }
        if (hitBase != null)
            activeBases.remove(hitBase);
        if(activeBases.isEmpty())
        {
            endGame();
        }

    }

    public void applyInterceptorBlast(Interceptor interceptor) {
        activeInterceptors.remove(interceptor);
        ArrayList<Missile> nowGone = new ArrayList<>();
        ArrayList<Missile> temp = new ArrayList<>(activeMissiles);
        for (Missile activeMissile : temp)
        {
            double distance = calculateDistance(activeMissile.getX(), activeMissile.getY(), interceptor.getX(), interceptor.getY());
            if(distance < 120)
            {
                setScore(++score);
                SoundPlayer.start("interceptor_hit_missile");
                nowGone.add(activeMissile);
                activeMissile.interceptorBlast();
            }
        }
        for(Missile m : nowGone){
            activeMissiles.remove(m);
            getLayout().removeView(m.getImageView());
        }
    }

    public void endGame()
    {
        missileMaker.onPause();
        for(Missile mis : activeMissiles)
        {
            mis.stop();
        }
        ObjectAnimator aAnim =
                ObjectAnimator.ofFloat(gameOver, "alpha", 0, 1);
        aAnim.setDuration(3000);
        aAnim.start();
        new CountDownTimer(6000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                DatabaseHandler dbh =
                        new DatabaseHandler(mainActivity, getLevel(), getScore());
                new Thread(dbh).start();
            }
        }.start();
    }

    public ConstraintLayout getLayout() {
        return layout;
    }

    public void setLevel(int currentLevel)
    {
        String text = "Level: " + currentLevel;
        levelText.setText(text);
    }

    public void setScore(int currentScore)
    {
        scoreText.setText(String.valueOf(currentScore));
    }

    public void displayTopDialog(){
        LayoutInflater inflater = LayoutInflater.from(this);
        @SuppressLint("InflateParams")
        final View view = inflater.inflate(R.layout.top_score_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.launcher);
        builder.setTitle("You are a Top-Player!");
        builder.setMessage("Please enter your initials (up to 3 characters):");
        builder.setView(view);
        builder.setPositiveButton("OK", null);
        builder.setNegativeButton("CANCEL", (dialog, which) -> goToLeaderboard());
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialog1 -> {
            Button button = ((AlertDialog) dialog1).getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(v -> {
                EditText initials = view.findViewById(R.id.dialogInitials);
                String initialsString = initials == null? "" : initials.getText().toString();

                if(initialsString.trim().equals(""))
                {
                    TextView errorText = view.findViewById(R.id.dialogError);
                    errorText.setVisibility(View.VISIBLE);
                } else
                {
                    doTransferToLeaderboard(initialsString);
                    dialog1.dismiss();
                }

            });
        });
        dialog.show();
    }

    public void goToLeaderboard() {
        Intent intent = new Intent(MainActivity.this, LeaderboardActivity.class);
        finish();
        startActivity(intent);
    }

    public int getLevel()
    {
        String [] levelSplit = levelText.getText().toString().split(" ");
        return Integer.parseInt(levelSplit[1]);
    }

    public int getScore()
    {
        return Integer.parseInt(scoreText.getText().toString());
    }

    public void doTransferToLeaderboard(String init) {
        int level = getLevel();
        int score = getScore();
        Log.d(TAG, "doSave: level:" + level + "Score:" + score + "Initial: " + init);

        Intent leaderboardIntent = new Intent(MainActivity.this, LeaderboardActivity.class);
        leaderboardIntent.putExtra("initial", init);
        leaderboardIntent.putExtra("score", score);
        leaderboardIntent.putExtra("level", level);
        finish();
        startActivity(leaderboardIntent);
    }



}