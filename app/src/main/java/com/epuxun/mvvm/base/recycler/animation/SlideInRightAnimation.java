package com.epuxun.mvvm.base.recycler.animation;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;

import com.epuxun.mvvm.base.recycler.BaseRecyclerViewAdapter;

public class SlideInRightAnimation implements BaseRecyclerViewAdapter.BaseAnimation {
    @Override
    public Animator[] getAnimators(View view) {
        return new Animator[]{
                ObjectAnimator.ofFloat(view, "translationX", view.getRootView().getWidth(), 0)
        };
    }
}
