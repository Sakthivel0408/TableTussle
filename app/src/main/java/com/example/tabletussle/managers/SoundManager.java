package com.example.tabletussle.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.media.ToneGenerator;
import android.os.Build;
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
    private ToneGenerator toneGenerator;
    private AudioManager audioManager;

    private boolean soundEffectsEnabled;
    private boolean backgroundMusicEnabled;
    private boolean isInitialized = false;

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
        this.audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        // Load preferences
        soundEffectsEnabled = sharedPreferences.getBoolean(KEY_SOUND_EFFECTS, true);
        backgroundMusicEnabled = sharedPreferences.getBoolean(KEY_BACKGROUND_MUSIC, true);

        initializeSoundPool();
        loadSoundEffects();
        initializeToneGenerator();

        isInitialized = true;
        Log.d(TAG, "SoundManager initialized - SFX: " + soundEffectsEnabled + ", Music: " + backgroundMusicEnabled);
    }

    public static synchronized SoundManager getInstance(Context context) {
        if (instance == null) {
            instance = new SoundManager(context);
        }
        return instance;
    }

    private void initializeSoundPool() {
        try {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            soundPool = new SoundPool.Builder()
                    .setMaxStreams(5)
                    .setAudioAttributes(audioAttributes)
                    .build();

            soundEffects = new HashMap<>();
            Log.d(TAG, "SoundPool initialized");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing SoundPool: " + e.getMessage());
        }
    }

    private void initializeToneGenerator() {
        try {
            // Initialize with a reasonable volume
            int volume = ToneGenerator.MAX_VOLUME - 20; // 80% volume
            toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, volume);
            Log.d(TAG, "ToneGenerator initialized with volume: " + volume);
        } catch (Exception e) {
            Log.e(TAG, "Error initializing ToneGenerator: " + e.getMessage());
            toneGenerator = null;
        }
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

            Log.d(TAG, "Sound effects loaded (using tone generator)");
        } catch (Exception e) {
            Log.e(TAG, "Error loading sound effects: " + e.getMessage());
        }
    }

    /**
     * Play a sound effect
     */
    public void playSound(SoundEffect effect) {
        if (!isInitialized || !soundEffectsEnabled) {
            Log.d(TAG, "Sound disabled or not initialized. Enabled: " + soundEffectsEnabled + ", Init: " + isInitialized);
            return;
        }

        // Check if audio is not muted
        if (audioManager != null) {
            int ringerMode = audioManager.getRingerMode();
            if (ringerMode == AudioManager.RINGER_MODE_SILENT) {
                Log.d(TAG, "Device is in silent mode, skipping sound");
                return;
            }
        }

        try {
            if (toneGenerator == null) {
                initializeToneGenerator();
            }

            if (toneGenerator != null) {
                int toneType;
                int duration;

                switch (effect) {
                    case CLICK:
                        toneType = ToneGenerator.TONE_PROP_BEEP;
                        duration = 50;
                        break;
                    case MOVE:
                        toneType = ToneGenerator.TONE_PROP_ACK;
                        duration = 100;
                        break;
                    case WIN:
                        toneType = ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD;
                        duration = 200;
                        break;
                    case LOSE:
                        toneType = ToneGenerator.TONE_CDMA_ABBR_ALERT;
                        duration = 200;
                        break;
                    case DRAW:
                        toneType = ToneGenerator.TONE_PROP_NACK;
                        duration = 150;
                        break;
                    default:
                        toneType = ToneGenerator.TONE_PROP_BEEP;
                        duration = 50;
                        break;
                }

                toneGenerator.startTone(toneType, duration);
                Log.d(TAG, "Played sound: " + effect.name());
            } else {
                Log.w(TAG, "ToneGenerator is null, cannot play sound");
            }

        } catch (Exception e) {
            Log.e(TAG, "Error playing sound: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Start background music (simple implementation)
     */
    public void startBackgroundMusic() {
        if (!backgroundMusicEnabled || backgroundMusicPlayer != null) {
            Log.d(TAG, "Background music not started. Enabled: " + backgroundMusicEnabled + ", Player exists: " + (backgroundMusicPlayer != null));
            return;
        }

        try {
            // For now, we'll skip background music as we don't have audio files
            // In a real implementation:
            // backgroundMusicPlayer = MediaPlayer.create(context, R.raw.background_music);
            // if (backgroundMusicPlayer != null) {
            //     backgroundMusicPlayer.setLooping(true);
            //     backgroundMusicPlayer.setVolume(0.3f, 0.3f); // 30% volume
            //     backgroundMusicPlayer.start();
            //     Log.d(TAG, "Background music started");
            // }

            Log.d(TAG, "Background music disabled (no audio file)");
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
                if (backgroundMusicPlayer.isPlaying()) {
                    backgroundMusicPlayer.stop();
                }
                backgroundMusicPlayer.release();
                backgroundMusicPlayer = null;
                Log.d(TAG, "Background music stopped");
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
            try {
                backgroundMusicPlayer.pause();
                Log.d(TAG, "Background music paused");
            } catch (Exception e) {
                Log.e(TAG, "Error pausing background music: " + e.getMessage());
            }
        }
    }

    /**
     * Resume background music
     */
    public void resumeBackgroundMusic() {
        if (backgroundMusicPlayer != null && backgroundMusicEnabled) {
            try {
                backgroundMusicPlayer.start();
                Log.d(TAG, "Background music resumed");
            } catch (Exception e) {
                Log.e(TAG, "Error resuming background music: " + e.getMessage());
            }
        } else if (backgroundMusicEnabled && backgroundMusicPlayer == null) {
            startBackgroundMusic();
        }
    }

    /**
     * Update settings from SharedPreferences
     */
    public void updateSettings() {
        boolean oldSfxEnabled = soundEffectsEnabled;
        boolean oldMusicEnabled = backgroundMusicEnabled;

        soundEffectsEnabled = sharedPreferences.getBoolean(KEY_SOUND_EFFECTS, true);
        backgroundMusicEnabled = sharedPreferences.getBoolean(KEY_BACKGROUND_MUSIC, true);

        Log.d(TAG, "Settings updated - SFX: " + oldSfxEnabled + " -> " + soundEffectsEnabled +
                   ", Music: " + oldMusicEnabled + " -> " + backgroundMusicEnabled);

        if (!backgroundMusicEnabled) {
            stopBackgroundMusic();
        } else if (backgroundMusicEnabled && !oldMusicEnabled) {
            startBackgroundMusic();
        }

        // Reinitialize ToneGenerator if it's null
        if (soundEffectsEnabled && toneGenerator == null) {
            initializeToneGenerator();
        }
    }

    /**
     * Enable/disable sound effects
     */
    public void setSoundEffectsEnabled(boolean enabled) {
        soundEffectsEnabled = enabled;
        sharedPreferences.edit().putBoolean(KEY_SOUND_EFFECTS, enabled).apply();
        Log.d(TAG, "Sound effects " + (enabled ? "enabled" : "disabled"));

        if (enabled && toneGenerator == null) {
            initializeToneGenerator();
        }
    }

    /**
     * Enable/disable background music
     */
    public void setBackgroundMusicEnabled(boolean enabled) {
        backgroundMusicEnabled = enabled;
        sharedPreferences.edit().putBoolean(KEY_BACKGROUND_MUSIC, enabled).apply();
        Log.d(TAG, "Background music " + (enabled ? "enabled" : "disabled"));

        if (!enabled) {
            stopBackgroundMusic();
        } else {
            startBackgroundMusic();
        }
    }

    /**
     * Check if sound effects are enabled
     */
    public boolean isSoundEffectsEnabled() {
        return soundEffectsEnabled;
    }

    /**
     * Check if background music is enabled
     */
    public boolean isBackgroundMusicEnabled() {
        return backgroundMusicEnabled;
    }

    /**
     * Release all resources
     */
    public void release() {
        Log.d(TAG, "Releasing SoundManager resources");

        if (toneGenerator != null) {
            toneGenerator.release();
            toneGenerator = null;
        }

        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }

        stopBackgroundMusic();
        isInitialized = false;
    }
}

