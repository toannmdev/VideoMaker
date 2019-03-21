package com.hiep.video.maker.entity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.hiep.video.maker.system.Config;

/**
 * Created by admin on 12/16/2015.
 */
public class AnimationView {

    private int mDuration = 300;

    public static void runAnimation(View view, int mDuration, AnimationType type) {
        switch (type) {
            case ROTATE:
                runRotateAnimation(view, mDuration);
                break;
            case ALPHA:
                runAlphaAnimation(view, mDuration);
                break;
            case HORIZION_LEFT:
                runHorizonLeftAnimation(view, mDuration);
                break;
            case HORIZION_RIGHT:
                runHorizonRightAnimation(view, mDuration);
                break;
            case HORIZON_CROSS:
                // NOT SUPPORT NOW
                // May be something for List
                break;
            case SCALE:
                runScaleAnimation(view, mDuration);
                break;
            case FLIP_HORIZON:
                runFlipHorizonAnimation(view, mDuration);
                break;
            case FLIP_VERTICAL:
                runFlipVertialAnimation(view, mDuration);
                break;
            default:
                break;
        }
    }

    public static void runAlphaAnimation(View view, int mDuration) {
        view.setAlpha(0);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "alpha",
                0, 1);
        objectAnimator.setDuration(mDuration);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.start();
    }

    public static void runHorizonLeftAnimation(View view, int mDuration) {
        view.setAlpha(0);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view,
                "translationX", Config.SCREENWIDTH, 0);
        objectAnimator.setInterpolator(new LinearInterpolator());
        ObjectAnimator objectAnimatorAlpha = ObjectAnimator.ofFloat(view,
                "alpha", 0f, 1f);
        AnimatorSet set = new AnimatorSet();
        set.setDuration(mDuration);
        set.playTogether(objectAnimator, objectAnimatorAlpha);
        set.start();
    }

    public static void runHorizonRightAnimation(View view, int mDuration) {
        view.setAlpha(0);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view,
                "translationX", Config.SCREENWIDTH, 0);
        objectAnimator.setInterpolator(new LinearInterpolator());
        ObjectAnimator objectAnimatorAlpha = ObjectAnimator.ofFloat(view,
                "alpha", 0f, 1f);
        AnimatorSet set = new AnimatorSet();
        set.setDuration(mDuration);
        set.playTogether(objectAnimator, objectAnimatorAlpha);
        set.start();
    }

    public static void runRotateAnimation(View view, int mDuration) {
        view.setAlpha(0);
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view,
                "rotation", 0f, 360f);
        ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(view, "scaleX",
                0f, 1f);
        ObjectAnimator objectAnimator3 = ObjectAnimator.ofFloat(view, "scaleY",
                0f, 1f);
        ObjectAnimator objectAnimator4 = ObjectAnimator.ofFloat(view, "alpha",
                0f, 1f);

        objectAnimator2.setInterpolator(new AccelerateInterpolator(1.0f));
        objectAnimator3.setInterpolator(new AccelerateInterpolator(1.0f));

        set.setDuration(mDuration);
        set.playTogether(objectAnimator, objectAnimator2, objectAnimator3,
                objectAnimator4);
        set.start();
    }

    public static void runScaleAnimation(View view, int mDuration) {
        view.setAlpha(0);
        AnimatorSet set = new AnimatorSet();

        ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(view, "scaleX",
                0f, 1f);
        ObjectAnimator objectAnimator3 = ObjectAnimator.ofFloat(view, "scaleY",
                0f, 1f);
        ObjectAnimator objectAnimator4 = ObjectAnimator.ofFloat(view, "alpha",
                0f, 1f);
        set.setDuration(mDuration);
        set.playTogether(objectAnimator2, objectAnimator3, objectAnimator4);
        set.start();
    }

    public static void runFlipVertialAnimation(View view, int mDuration) {
        view.setAlpha(0);
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(view,
                "rotationX", -180f, 0f);
        ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(view, "alpha",
                0f, 1f);
        set.setDuration(mDuration);
        set.playTogether(objectAnimator1, objectAnimator2);
        set.start();
    }

    public static void runFlipHorizonAnimation(View view, int mDuration) {
        view.setAlpha(0);
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(view,
                "rotationY", -180f, 0f);
        ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(view, "alpha",
                0f, 1f);
        set.setDuration(mDuration);
        set.playTogether(objectAnimator1, objectAnimator2);
        set.start();
    }

}
