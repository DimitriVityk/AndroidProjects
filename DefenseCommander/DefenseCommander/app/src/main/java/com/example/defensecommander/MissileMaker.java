package com.example.defensecommander;

import android.animation.AnimatorSet;

public class MissileMaker implements Runnable{

    private final MainActivity mainActivity;
    private boolean isRunning = true;
    private final int screenWidth;
    private final int screenHeight;
    private int missileCount = 0; // Current plane count for each level
    private static int LEVEL_CHANGE_VALUE = 10; // Change level after this many missiles
    private int level = 1;
    private long delayBetweenMissiles = 4000;// Pause between new missiles
    private long missileSpeed = 5000;
    private Object mPauseLock;
    private boolean mPaused;
    private boolean mFinished;


    MissileMaker(MainActivity mainActivity, int screenWidth, int screenHeight) {
        this.mainActivity = mainActivity;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        mPauseLock = new Object();
        mPaused = false;
        mFinished = false;
    }

    @Override
    public void run() {
        try
        {
        Thread.sleep((long) (0.5 * delayBetweenMissiles));
        }
        catch (InterruptedException e)
        {
        e.printStackTrace();
        Thread.currentThread().interrupt();
        }
        while (!mFinished) {
            makeMissile();
            missileCount++;
            if(missileCount > LEVEL_CHANGE_VALUE)
            {
                increaseLevel();
                missileCount = 0;
              //  LEVEL_CHANGE_VALUE *= 1.5;
            }
            try
            {
                Thread.sleep(getSleepTime());
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
            synchronized (mPauseLock) {
                while (mPaused) {
                    try {
                        mPauseLock.wait();
                    } catch (InterruptedException e) {
                    }
                }
            }
        }
    }


    public void onPause() {
        synchronized (mPauseLock) {
            mPaused = true;
        }
    }


    public void onResume() {
        synchronized (mPauseLock) {
            mPaused = false;
            mPauseLock.notifyAll();
        }
    }

    void makeMissile()
    {
        final Missile missile = new Missile(screenWidth, screenHeight, missileSpeed, mainActivity);
        mainActivity.addMissile(missile);
        SoundPlayer.start("launch_missile");
        AnimatorSet as = missile.setData();
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                as.start();
            }
        });

    }


    void increaseLevel()
    {
        level++;
        delayBetweenMissiles-=500;
        if(delayBetweenMissiles<=0)
        {
            delayBetweenMissiles = 1;
        }

        mainActivity.runOnUiThread(() -> mainActivity.setLevel(level));

        try
        {
            Thread.sleep(2000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }

    }

    long getSleepTime()
    {
        double randomNum = Math.random();

        if(randomNum < .1)
        {
            return 1;
        } else if (randomNum < 0.2)
        {
            return (long) (0.5 * delayBetweenMissiles);
        } else
        {
            return delayBetweenMissiles;
        }
    }
}
