package com.example.administrator.common.commonUtils;

import android.content.Context;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;

/**
 * Created by Administrator on 2017/1/17.
 */

public class AnimationUtils {

    /**
     * 动画集合，由右向左，由淡变深
     * @param context
     * @return
     */
    public static LayoutAnimationController getRightToLeftAlpha (Context context){
        int duration = 200;
        AnimationSet set = new AnimationSet(true);

        Animation animation = new AlphaAnimation(0.5f ,1.0f);
        animation.setDuration(duration);
        set.addAnimation(animation);

        animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,1.0f,Animation.RELATIVE_TO_SELF, 0.0f,Animation.RELATIVE_TO_SELF, 0.5f,Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(duration);
        set.addAnimation(animation);

        LayoutAnimationController controller = new LayoutAnimationController(set,0.5f);
        controller.setOrder(LayoutAnimationController.ORDER_NORMAL);

        return controller;
    }

    /**
     * 动画-抖动，应用场景：当某个editText输错时
     * @param view
     */
    public static void viewShake(View view){
        TranslateAnimation animation = new TranslateAnimation(0, 100, 0, 0);
        animation.setInterpolator(new OvershootInterpolator());
        animation.setDuration(200);
        animation.setRepeatCount(2);
        animation.setRepeatMode(Animation.RESTART);
        view.startAnimation(animation);
    }
}
