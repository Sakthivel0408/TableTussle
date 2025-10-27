package com.example.tabletussle.managers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

/**
 * Manages haptic feedback (vibration) for the game
 */
public class VibrationManager {
    private static final String TAG = "VibrationManager";
    private static final String PREFS_NAME = "TableTussleSettings";
    private static final String KEY_VIBRATION = "vibration";

    private static VibrationManager instance;
    private Context context;
    private SharedPreferences sharedPreferences;
    private Vibrator vibrator;
    private boolean vibrationEnabled;

    public enum VibrationType {
        LIGHT,    // Short tap (10ms)
        MEDIUM,   // Button press (25ms)
        HEAVY,    // Move made (50ms)
        SUCCESS,  // Win (pattern)
        ERROR     // Loss (pattern)
    }

    private VibrationManager(Context context) {
        this.context = context.getApplicationContext();
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        // Load preference
        vibrationEnabled = sharedPreferences.getBoolean(KEY_VIBRATION, true);
    }

    public static synchronized VibrationManager getInstance(Context context) {
        if (instance == null) {
            instance = new VibrationManager(context);
        }
        return instance;
    }

    /**
     * Vibrate with specified type
     */
    @SuppressLint("MissingPermission")
    public void vibrate(VibrationType type) {
        if (!vibrationEnabled || vibrator == null || !vibrator.hasVibrator()) {
            return;
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Modern API (Android 8.0+)
                VibrationEffect effect = null;

                switch (type) {
                    case LIGHT:
                        effect = VibrationEffect.createOneShot(10, VibrationEffect.DEFAULT_AMPLITUDE);
                        break;
                    case MEDIUM:
                        effect = VibrationEffect.createOneShot(25, VibrationEffect.DEFAULT_AMPLITUDE);
                        break;
                    case HEAVY:
                        effect = VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE);
                        break;
                    case SUCCESS:
                        // Pattern: short, pause, short, pause, long
                        long[] successPattern = {0, 50, 100, 50, 100, 200};
                        effect = VibrationEffect.createWaveform(successPattern, -1);
                        break;
                    case ERROR:
                        // Pattern: long, pause, long
                        long[] errorPattern = {0, 100, 100, 100};
                        effect = VibrationEffect.createWaveform(errorPattern, -1);
                        break;
                }

                if (effect != null) {
                    vibrator.vibrate(effect);
                }
            } else {
                // Legacy API (Android 7.1 and below)
                long duration;
                switch (type) {
                    case LIGHT:
                        duration = 10;
                        break;
                    case MEDIUM:
                        duration = 25;
                        break;
                    case HEAVY:
                        duration = 50;
                        break;
                    case SUCCESS:
                        long[] successPattern = {0, 50, 100, 50, 100, 200};
                        vibrator.vibrate(successPattern, -1);
                        return;
                    case ERROR:
                        long[] errorPattern = {0, 100, 100, 100};
                        vibrator.vibrate(errorPattern, -1);
                        return;
                    default:
                        duration = 25;
                }
                vibrator.vibrate(duration);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error vibrating: " + e.getMessage());
        }
    }

    /**
     * Update settings
     */
    public void updateSettings() {
        vibrationEnabled = sharedPreferences.getBoolean(KEY_VIBRATION, true);
    }

    /**
     * Enable/disable vibration
     */
    public void setVibrationEnabled(boolean enabled) {
        vibrationEnabled = enabled;
    }

    /**
     * Check if vibration is enabled
     */
    public boolean isVibrationEnabled() {
        return vibrationEnabled;
    }

    /**
     * Cancel any ongoing vibration
     */
    @SuppressLint("MissingPermission")
    public void cancel() {
        if (vibrator != null) {
            vibrator.cancel();
        }
    }
}

