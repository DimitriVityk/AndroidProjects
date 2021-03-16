package com.example.defensecommander;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

public class Base {
    private final ImageView imageView;
    public Base(ImageView imageView)
    {
        this.imageView = imageView;
    }

    public float getX()
    {
        return imageView.getX();
    }

    public float getY()
    {
        return imageView.getY();
    }

    public void destruct(MainActivity mainActivity)
    {
        SoundPlayer.start("base_blast");
        mainActivity.runOnUiThread(() -> mainActivity.getLayout().removeView(imageView));
        ImageView explodeImage = new ImageView(mainActivity);
        explodeImage.setImageResource(R.drawable.blast);
        explodeImage.setX(getX()-(explodeImage.getDrawable().getIntrinsicWidth()/2));
        explodeImage.setY(getY()-(explodeImage.getDrawable().getIntrinsicHeight()/2));

        mainActivity.runOnUiThread(() -> mainActivity.getLayout().addView(explodeImage));

        final ObjectAnimator alpha = ObjectAnimator.ofFloat(explodeImage, "alpha", 0.0f);
        alpha.setInterpolator(new LinearInterpolator());
        alpha.setDuration(3000);
        alpha.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mainActivity.getLayout().removeView(explodeImage);
            }
        });
        alpha.start();
    }

}
