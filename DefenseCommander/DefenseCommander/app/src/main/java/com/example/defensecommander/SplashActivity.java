package com.example.defensecommander;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIME_OUT = 3000;
    private ImageView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        title = findViewById(R.id.splashTitle);
        setupFullScreen();
        getScreenDimensions();
        setUpSounds();
        animateTitle();
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

    private void setUpSounds()
    {
        SoundPlayer.setupSound(this, "background", R.raw.background, true);
        SoundPlayer.setupSound(this, "base_blast", R.raw.base_blast, false);
        SoundPlayer.setupSound(this, "interceptor_blast", R.raw.interceptor_blast, false);
        SoundPlayer.setupSound(this, "interceptor_hit_missile", R.raw.interceptor_hit_missile, false);
        SoundPlayer.setupSound(this, "launch_interceptor", R.raw.launch_interceptor, false);
        SoundPlayer.setupSound(this, "launch_missile", R.raw.launch_missile, false);
        SoundPlayer.setupSound(this, "missile_miss", R.raw.missile_miss, false);
        new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {

                SoundPlayer.start("background");
            }
        }.start();
    }

    private void animateTitle()
    {
        new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                ObjectAnimator aAnim =
                        ObjectAnimator.ofFloat(title, "alpha", 0, 1);
                aAnim.setDuration(3000);
                aAnim.start();
                goToMain();
            }
        }.start();
    }

    private void goToMain()
    {
        new Handler().postDelayed(() -> {
            // This method will be executed once the timer is over
            // Start your app main activity
            Intent i =
                    new Intent(SplashActivity.this, MainActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out); // new act, old act
            // close this activity
            finish();
        }, SPLASH_TIME_OUT);
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
    }
}