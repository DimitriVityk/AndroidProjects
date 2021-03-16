package com.example.defensecommander;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;


class Missile {

    private final MainActivity mainActivity;
    private final ImageView imageView;
    private final AnimatorSet aSet = new AnimatorSet();
    private final int screenHeight;
    private final int screenWidth;
    private final long screenTime;
    private static final String TAG = "Missile";

    Missile(int screenWidth, int screenHeight, long screenTime, final MainActivity mainActivity) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.screenTime = screenTime;
        this.mainActivity = mainActivity;


        this.imageView = new ImageView(mainActivity);
        imageView.setImageResource(R.drawable.missile);

    }

    AnimatorSet setData() {

        int startX = (int) (Math.random() * screenWidth) - imageView.getDrawable().getIntrinsicWidth();
        int endX = (int) (Math.random() * screenWidth);

        int startY = -100 - imageView.getDrawable().getIntrinsicWidth();
        int endY = screenHeight;

        double angle = Math.toDegrees(Math.atan2(endX - startX, endY - startY));// Keep angle between 0 and 360
        angle = angle + Math.ceil(-angle / 360) * 360;
        float rAngle = (float) (190.0f -angle);

        imageView.setX(startX);
        imageView.setY(startY);
        imageView.setZ(-10);
        imageView.setRotation(rAngle);

        mainActivity.runOnUiThread(() -> mainActivity.getLayout().addView(imageView));

        ObjectAnimator xAnim = ObjectAnimator.ofFloat(imageView, "x", startX, endX);
        xAnim.setInterpolator(new LinearInterpolator());
        xAnim.setDuration(screenTime);
        xAnim.addUpdateListener(animation -> {
            if(imageView.getY() > screenHeight * .85)
            {
                aSet.cancel();
                makeGroundBlast();
                mainActivity.runOnUiThread(() -> mainActivity.removeMissile(Missile.this));
            }
        });

        ObjectAnimator yAnim = ObjectAnimator.ofFloat(imageView, "y", startY, endY);
        yAnim.setInterpolator(new LinearInterpolator());
        yAnim.setDuration(screenTime);

        aSet.playTogether(xAnim, yAnim);
        return aSet;

    }

    private void makeGroundBlast()
    {
        final ImageView explodeView = new ImageView(mainActivity);
        explodeView.setImageResource(R.drawable.explode);

        float imageWidth = imageView.getDrawable().getIntrinsicWidth();
        float imageHeight = imageView.getDrawable().getIntrinsicHeight();
        explodeView.setX(imageView.getX()-((imageHeight)/2));
        explodeView.setY(imageView.getY()-((imageWidth)/2));
        explodeView.setZ(-15);
        explodeView.setRotation((float) (360.0 * Math.random()));

        mainActivity.runOnUiThread(() -> mainActivity.getLayout().addView(explodeView));

        final ObjectAnimator alpha = ObjectAnimator.ofFloat(explodeView, "alpha", 0.0f);
        alpha.setInterpolator(new LinearInterpolator());
        alpha.setDuration(3000);
        alpha.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mainActivity.runOnUiThread(() -> {
                    mainActivity.getLayout().removeView(explodeView);
                    Log.d(TAG, "run: NUM VIEWS " +
                            mainActivity.getLayout().getChildCount());
                });
            }
        });
        alpha.start();

        mainActivity.runOnUiThread(() -> mainActivity.applyMissileBlast(Missile.this));
    }

    void interceptorBlast() {

        mainActivity.runOnUiThread(() -> mainActivity.removeMissile(Missile.this));

        final ImageView iv = new ImageView(mainActivity);
        iv.setImageResource(R.drawable.explode);

        iv.setTransitionName("Missile Intercepted Blast");

        float imageWidth = imageView.getDrawable().getIntrinsicWidth();
        float imageHeight = imageView.getDrawable().getIntrinsicHeight();
        iv.setX(imageView.getX()-((imageWidth)/2));
        iv.setY(imageView.getY()-((imageHeight)/2));
        iv.setZ(-15);
        iv.setRotation((float) (360.0 * Math.random()));

        aSet.cancel();

        mainActivity.getLayout().removeView(imageView);
        mainActivity.getLayout().addView(iv);

        final ObjectAnimator alpha = ObjectAnimator.ofFloat(iv, "alpha", 0.0f);
        alpha.setInterpolator(new LinearInterpolator());
        alpha.setDuration(3000);
        alpha.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mainActivity.getLayout().removeView(iv);
            }
        });
        alpha.start();
    }

    public ImageView getImageView()
    {
        return this.imageView;
    }

    float getX() {
        return imageView.getX();
    }

    float getY() {
        return imageView.getY();
    }

    void stop() {
        aSet.cancel();
    }

    void pause() {
        aSet.pause();
    }

    void resume() {
        aSet.resume();
    }

}
