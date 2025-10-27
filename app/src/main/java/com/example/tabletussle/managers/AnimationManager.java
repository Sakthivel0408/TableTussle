package com.example.tabletussle.managers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;

/**
 * Manages animations for UI elements
 */
public class AnimationManager {
    private static final String TAG = "AnimationManager";
    private static final String PREFS_NAME = "TableTussleSettings";
    private static final String KEY_ANIMATIONS = "animations";

    private static AnimationManager instance;
    private Context context;
    private SharedPreferences sharedPreferences;
    private boolean animationsEnabled;

    private AnimationManager(Context context) {
        this.context = context.getApplicationContext();
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Load preference
        animationsEnabled = sharedPreferences.getBoolean(KEY_ANIMATIONS, true);
    }

    public static synchronized AnimationManager getInstance(Context context) {
        if (instance == null) {
            instance = new AnimationManager(context);
        }
        return instance;
    }

    /**
     * Animate a button click
     */
    public void animateButtonClick(View view) {
        if (!animationsEnabled || view == null) {
            return;
        }

        view.animate()
                .scaleX(0.9f)
                .scaleY(0.9f)
                .setDuration(100)
                .withEndAction(() -> {
                    view.animate()
                            .scaleX(1.0f)
                            .scaleY(1.0f)
                            .setDuration(100)
                            .start();
                })
                .start();
    }

    /**
     * Animate a cell being filled (for tic-tac-toe)
     */
    public void animateCellFill(View view) {
        if (!animationsEnabled || view == null) {
            return;
        }

        view.setScaleX(0f);
        view.setScaleY(0f);
        view.animate()
                .scaleX(1.0f)
                .scaleY(1.0f)
                .setDuration(200)
                .setInterpolator(new OvershootInterpolator())
                .start();
    }

    /**
     * Animate winning cells
     */
    public void animateWinningCells(View... views) {
        if (!animationsEnabled || views == null) {
            return;
        }

        for (View view : views) {
            if (view != null) {
                // Pulse animation
                view.animate()
                        .scaleX(1.2f)
                        .scaleY(1.2f)
                        .setDuration(300)
                        .withEndAction(() -> {
                            view.animate()
                                    .scaleX(1.0f)
                                    .scaleY(1.0f)
                                    .setDuration(300)
                                    .start();
                        })
                        .start();
            }
        }
    }

    /**
     * Shake animation for errors
     */
    public void animateShake(View view) {
        if (!animationsEnabled || view == null) {
            return;
        }

        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationX", 0, 25, -25, 25, -25, 15, -15, 6, -6, 0);
        animator.setDuration(500);
        animator.start();
    }

    /**
     * Fade in animation
     */
    public void animateFadeIn(View view) {
        if (!animationsEnabled || view == null) {
            return;
        }

        view.setAlpha(0f);
        view.setVisibility(View.VISIBLE);
        view.animate()
                .alpha(1.0f)
                .setDuration(300)
                .setListener(null)
                .start();
    }

    /**
     * Fade out animation
     */
    public void animateFadeOut(View view) {
        if (!animationsEnabled || view == null) {
            return;
        }

        view.animate()
                .alpha(0f)
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(View.GONE);
                    }
                })
                .start();
    }

    /**
     * Slide in from top
     */
    public void animateSlideInFromTop(View view) {
        if (!animationsEnabled || view == null) {
            return;
        }

        view.setTranslationY(-view.getHeight());
        view.setVisibility(View.VISIBLE);
        view.animate()
                .translationY(0)
                .setDuration(400)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
    }

    /**
     * Bounce animation
     */
    public void animateBounce(View view) {
        if (!animationsEnabled || view == null) {
            return;
        }

        view.animate()
                .scaleX(1.2f)
                .scaleY(1.2f)
                .setDuration(200)
                .setInterpolator(new BounceInterpolator())
                .withEndAction(() -> {
                    view.animate()
                            .scaleX(1.0f)
                            .scaleY(1.0f)
                            .setDuration(200)
                            .start();
                })
                .start();
    }

    /**
     * Rotate animation (for refresh/reset actions)
     */
    public void animateRotate(View view) {
        if (!animationsEnabled || view == null) {
            return;
        }

        view.animate()
                .rotation(360f)
                .setDuration(500)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(() -> view.setRotation(0f))
                .start();
    }

    /**
     * Update settings
     */
    public void updateSettings() {
        animationsEnabled = sharedPreferences.getBoolean(KEY_ANIMATIONS, true);
    }

    /**
     * Enable/disable animations
     */
    public void setAnimationsEnabled(boolean enabled) {
        animationsEnabled = enabled;
    }

    /**
     * Check if animations are enabled
     */
    public boolean areAnimationsEnabled() {
        return animationsEnabled;
    }
}

