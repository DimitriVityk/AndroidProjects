package com.example.defensecommander;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

class Interceptor {

    private final MainActivity mainActivity;
    private ImageView imageview;
    private AnimatorSet animatorSet;
    private final float startX;
    private final float startY;
    private float endX;
    private float endY;
    private static int idVal = -1;
    private static final double DISTANCE_TIME = 0.75;

    Interceptor(MainActivity mainActivity, Base shootingBase, float endX, float endY) {
        this.mainActivity = mainActivity;
        this.startX = shootingBase.getX();
        this.startY = shootingBase.getY();
        this.endX = endX;
        this.endY = endY;
        initialize();
    }

    private void initialize() {
        imageview = new ImageView(mainActivity);
        imageview.setId(idVal--);
        imageview.setImageResource(R.drawable.interceptor);
        imageview.setX(startX);
        imageview.setY(startY);
        imageview.setZ(-10);

        final int imageWidth = (int) (imageview.getDrawable().getIntrinsicWidth() * 0.5);
        final int imageHeight = (int) (imageview.getDrawable().getIntrinsicHeight() * 0.5);
        endX -= imageWidth;
        endY -= imageHeight;

        float angle = MainActivity.calculateAngle(
                imageview.getX(), imageview.getY(), endX, endY);
        imageview.setRotation(angle);
        mainActivity.getLayout().addView(imageview);

        double distanceToEnd =  Math.sqrt((endY - imageview.getY()) * (endY - imageview.getY()) + (endX - imageview.getX()) * (endX - imageview.getX()));

        ObjectAnimator moveX = ObjectAnimator.ofFloat(imageview, "x", endX);
        moveX.setInterpolator(new AccelerateInterpolator());
        moveX.setDuration((long) (distanceToEnd * DISTANCE_TIME));

        ObjectAnimator moveY = ObjectAnimator.ofFloat(imageview, "y", endY);
        moveY.setInterpolator(new AccelerateInterpolator());
        moveY.setDuration((long) (distanceToEnd * DISTANCE_TIME));

        animatorSet = new AnimatorSet();
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mainActivity.getLayout().removeView(imageview);
                makeBlast();
            }
        });
        animatorSet.playTogether(moveX, moveY);
    }

    private void makeBlast() {
        SoundPlayer.start("interceptor_blast");
        final ImageView explodeView = new ImageView(mainActivity);
        explodeView.setImageResource(R.drawable.i_explode);

        float imageWidth = explodeView.getDrawable().getIntrinsicWidth();
        float imageHeight = explodeView.getDrawable().getIntrinsicHeight();
        explodeView.setX(this.getX() - (imageWidth/2));
        explodeView.setY(this.getY() - (imageHeight/2));
        explodeView.setZ(-15);
        mainActivity.getLayout().addView(explodeView);

        final ObjectAnimator alpha = ObjectAnimator.ofFloat(explodeView, "alpha", 0.0f);
        alpha.setInterpolator(new LinearInterpolator());
        alpha.setDuration(3000);
        alpha.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mainActivity.getLayout().removeView(explodeView);
            }
        });
        alpha.start();

        mainActivity.applyInterceptorBlast(this);
    }

    void launch() {
        animatorSet.start();
    }

    float getX() {
        int xVar = imageview.getWidth() / 2;
        return imageview.getX() + xVar;
    }

    float getY() {
        int yVar = imageview.getHeight() / 2;
        return imageview.getY() + yVar;
    }

}
