package com.example.defensecommander;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import static com.example.defensecommander.MainActivity.screenHeight;
import static com.example.defensecommander.MainActivity.screenWidth;

public class ScrollingBackground {
    private final Context context;
    private final ViewGroup layout;
    private ImageView backImageA;
    private ImageView backImageB;
    private final long duration;
    private final int resId;
    private final AnimatorSet aSet = new AnimatorSet();

    ScrollingBackground(Context context, ViewGroup layout, int resId, long duration) {
        this.context = context;
        this.layout = layout;
        this.resId = resId;
        this.duration = duration;
        setupBackground();
    }

    private void setupBackground() {
        backImageA = new ImageView(context);
        backImageB = new ImageView(context);

        LinearLayout.LayoutParams params = new LinearLayout
                .LayoutParams(screenWidth + getBarHeight(), screenHeight);
        backImageA.setLayoutParams(params);
        backImageB.setLayoutParams(params);

        layout.addView(backImageA);
        layout.addView(backImageB);

        Bitmap backBitmapA = BitmapFactory.decodeResource(context.getResources(), resId);
        Bitmap backBitmapB = BitmapFactory.decodeResource(context.getResources(), resId);

        backImageA.setImageBitmap(backBitmapA);
        backImageB.setImageBitmap(backBitmapB);

        backImageA.setScaleType(ImageView.ScaleType.FIT_XY);
        backImageB.setScaleType(ImageView.ScaleType.FIT_XY);

        backImageA.setZ(-1);
        backImageB.setZ(-1);

        backImageA.setAlpha(0.4f);
        backImageB.setAlpha(0.4f);

        animateBack();
    }

    private void animateBack() {

        ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(duration);


        animator.addUpdateListener(animation -> {
            final float progress = (float) animation.getAnimatedValue();
            float width = screenWidth + getBarHeight();

            float a_translationX = width * progress;
            float b_translationX = width * progress - width;

            backImageA.setTranslationX(a_translationX);
            backImageB.setTranslationX(b_translationX);
        });

        ValueAnimator ani = ValueAnimator.ofFloat(0.25f, 0.95f);
        ani.setRepeatCount(ValueAnimator.INFINITE);
        ani.setRepeatMode(ValueAnimator.REVERSE);
        ani.setInterpolator(new LinearInterpolator());
        ani.setDuration(duration);


        ani.addUpdateListener(animation -> {
            final float progress = (float) animation.getAnimatedValue();



            backImageA.setAlpha(progress);
            backImageB.setAlpha(progress);
        });

        aSet.playTogether(animator, ani);
        aSet.start();
    }


    private int getBarHeight() {
        int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return context.getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }
}
