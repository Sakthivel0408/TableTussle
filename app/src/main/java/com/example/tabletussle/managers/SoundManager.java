package com.example.tabletussle.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages sound effects and background music for the game
 */
public class SoundManager {
    private static final String TAG = "SoundManager";
    private static final String PREFS_NAME = "TableTussleSettings";
    private static final String KEY_SOUND_EFFECTS = "sound_effects";
    private static final String KEY_BACKGROUND_MUSIC = "background_music";

    private static SoundManager instance;
    private Context context;
    private SharedPreferences sharedPreferences;

    private SoundPool soundPool;
    private MediaPlayer backgroundMusicPlayer;
    private Map<String, Integer> soundEffects;

    private boolean soundEffectsEnabled;
    private boolean backgroundMusicEnabled;

    public enum SoundEffect {
        CLICK,
        MOVE,
        WIN,
        LOSE,
        DRAW
    }

    private SoundManager(Context context) {
        this.context = context.getApplicationContext();
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Load preferences
        soundEffectsEnabled = sharedPreferences.getBoolean(KEY_SOUND_EFFECTS, true);
        backgroundMusicEnabled = sharedPreferences.getBoolean(KEY_BACKGROUND_MUSIC, true);

        initializeSoundPool();
        loadSoundEffects();
    }

    public static synchronized SoundManager getInstance(Context context) {
        if (instance == null) {
            instance = new SoundManager(context);
        }
        return instance;
    }

    private void initializeSoundPool() {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(5)
                .setAudioAttributes(audioAttributes)
                .build();

        soundEffects = new HashMap<>();
    }

    /**
     * Load sound effects from raw resources
     * Note: For this implementation, we'll use system sounds as placeholders
     * You can replace these with actual sound files in res/raw/
     */
    private void loadSoundEffects() {
        try {
            // Since we don't have actual sound files, we'll use a simple beep sound
            // generated programmatically or use notification sounds as placeholders

            // In a real implementation, you would do:
            // soundEffects.put("CLICK", soundPool.load(context, R.raw.click_sound, 1));
            // soundEffects.put("MOVE", soundPool.load(context, R.raw.move_sound, 1));
            // etc.

            Log.d(TAG, "Sound effects loaded (using system sounds)");
        } catch (Exception e) {
            Log.e(TAG, "Error loading sound effects: " + e.getMessage());
        }
    }

    /**
     * Play a sound effect
     */
    public void playSound(SoundEffect effect) {
        if (!soundEffectsEnabled) {
            return;
        }

        try {
            // Simple beep using ToneGenerator as placeholder
            android.media.ToneGenerator toneGen = new android.media.ToneGenerator(
                    android.media.AudioManager.STREAM_MUSIC, 50);

            switch (effect) {
                case CLICK:
                    toneGen.startTone(android.media.ToneGenerator.TONE_PROP_BEEP, 50);
                    break;
                case MOVE:
                    toneGen.startTone(android.media.ToneGenerator.TONE_PROP_ACK, 100);
                    break;
                case WIN:
                    toneGen.startTone(android.media.ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
                    break;
                case LOSE:
                    toneGen.startTone(android.media.ToneGenerator.TONE_CDMA_ABBR_ALERT, 200);
                    break;
                case DRAW:
                    toneGen.startTone(android.media.ToneGenerator.TONE_PROP_NACK, 150);
                    break;
            }

            // Release tone generator after a delay
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(
                    toneGen::release, 300);

        } catch (Exception e) {
            Log.e(TAG, "Error playing sound: " + e.getMessage());
        }
    }

    /**
     * Start background music (simple implementation)
     */
    public void startBackgroundMusic() {
        if (!backgroundMusicEnabled || backgroundMusicPlayer != null) {
            return;
        }

        try {
            // For now, we'll skip background music as we don't have audio files
            // In a real implementation:
            // backgroundMusicPlayer = MediaPlayer.create(context, R.raw.background_music);
            // backgroundMusicPlayer.setLooping(true);
            // backgroundMusicPlayer.start();

            Log.d(TAG, "Background music started (placeholder)");
        } catch (Exception e) {
            Log.e(TAG, "Error starting background music: " + e.getMessage());
        }
    }

    /**
     * Stop background music
     */
    public void stopBackgroundMusic() {
        if (backgroundMusicPlayer != null) {
            try {
                backgroundMusicPlayer.stop();
                backgroundMusicPlayer.release();
                backgroundMusicPlayer = null;
            } catch (Exception e) {
                Log.e(TAG, "Error stopping background music: " + e.getMessage());
            }
        }
    }

    /**
     * Pause background music
     */
    public void pauseBackgroundMusic() {
        if (backgroundMusicPlayer != null && backgroundMusicPlayer.isPlaying()) {
            backgroundMusicPlayer.pause();
        }
    }

    /**
     * Resume background music
     */
    public void resumeBackgroundMusic() {
        if (backgroundMusicPlayer != null && backgroundMusicEnabled) {
            backgroundMusicPlayer.start();
        }
    }

    /**
     * Update settings
     */
    public void updateSettings() {
        soundEffectsEnabled = sharedPreferences.getBoolean(KEY_SOUND_EFFECTS, true);
        backgroundMusicEnabled = sharedPreferences.getBoolean(KEY_BACKGROUND_MUSIC, true);

        if (!backgroundMusicEnabled) {
            stopBackgroundMusic();
        }
    }

    /**
     * Enable/disable sound effects
     */
    public void setSoundEffectsEnabled(boolean enabled) {
        soundEffectsEnabled = enabled;
    }

    /**
     * Enable/disable background music
     */
    public void setBackgroundMusicEnabled(boolean enabled) {
        backgroundMusicEnabled = enabled;
        if (!enabled) {
            stopBackgroundMusic();
        } else {
            startBackgroundMusic();
        }
    }

    /**
     * Release all resources
     */
    public void release() {
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
        stopBackgroundMusic();
    }
}

