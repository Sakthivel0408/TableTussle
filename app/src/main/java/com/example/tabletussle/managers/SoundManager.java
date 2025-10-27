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
    private ToneGenerator musicGenerator;
    private AudioManager audioManager;
    private Thread musicThread;
    private volatile boolean isMusicPlaying = false;

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
     * Start background music (synthesized melody)
     */
    public void startBackgroundMusic() {
        if (!backgroundMusicEnabled || isMusicPlaying) {
            Log.d(TAG, "Background music not started. Enabled: " + backgroundMusicEnabled + ", Already playing: " + isMusicPlaying);
            return;
        }

        try {
            // Create a separate ToneGenerator for music with lower volume
            if (musicGenerator == null) {
                int musicVolume = ToneGenerator.MAX_VOLUME - 60; // 40% volume (quieter than SFX)
                musicGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, musicVolume);
                Log.d(TAG, "Music ToneGenerator initialized with volume: " + musicVolume);
            }

            // Start background music thread with a pleasant melody pattern
            isMusicPlaying = true;
            musicThread = new Thread(() -> {
                Log.d(TAG, "Background music thread started");

                // Melody pattern - a simple pleasant tune
                // Using DTMF tones which sound more musical
                int[] melodyPattern = {
                    ToneGenerator.TONE_DTMF_1,  // C
                    ToneGenerator.TONE_DTMF_3,  // E
                    ToneGenerator.TONE_DTMF_5,  // G
                    ToneGenerator.TONE_DTMF_3,  // E
                    ToneGenerator.TONE_DTMF_6,  // A
                    ToneGenerator.TONE_DTMF_5,  // G
                    ToneGenerator.TONE_DTMF_3,  // E
                    ToneGenerator.TONE_DTMF_1,  // C
                };

                int[] durations = {400, 400, 400, 400, 400, 400, 400, 600}; // milliseconds

                try {
                    while (isMusicPlaying && backgroundMusicEnabled) {
                        // Check if device is not in silent mode
                        if (audioManager != null) {
                            int ringerMode = audioManager.getRingerMode();
                            if (ringerMode == AudioManager.RINGER_MODE_SILENT) {
                                Thread.sleep(1000); // Wait and check again
                                continue;
                            }
                        }

                        // Play the melody
                        for (int i = 0; i < melodyPattern.length && isMusicPlaying; i++) {
                            if (musicGenerator != null) {
                                musicGenerator.startTone(melodyPattern[i], durations[i]);
                                Thread.sleep(durations[i] + 100); // Add small gap between notes
                            }
                        }

                        // Pause between melody repetitions
                        Thread.sleep(800);
                    }
                } catch (InterruptedException e) {
                    Log.d(TAG, "Background music thread interrupted");
                } catch (Exception e) {
                    Log.e(TAG, "Error in background music playback: " + e.getMessage());
                }

                Log.d(TAG, "Background music thread stopped");
            });

            musicThread.setDaemon(true);
            musicThread.start();
            Log.d(TAG, "Background music started (synthesized melody)");

        } catch (Exception e) {
            Log.e(TAG, "Error starting background music: " + e.getMessage());
            isMusicPlaying = false;
        }
    }

    /**
     * Stop background music
     */
    public void stopBackgroundMusic() {
        Log.d(TAG, "Stopping background music");
        isMusicPlaying = false;

        if (musicThread != null) {
            try {
                musicThread.interrupt();
                musicThread = null;
            } catch (Exception e) {
                Log.e(TAG, "Error stopping music thread: " + e.getMessage());
            }
        }

        if (musicGenerator != null) {
            try {
                musicGenerator.release();
                musicGenerator = null;
                Log.d(TAG, "Music ToneGenerator released");
            } catch (Exception e) {
                Log.e(TAG, "Error releasing music generator: " + e.getMessage());
            }
        }

        // Keep this for compatibility if we add MediaPlayer later
        if (backgroundMusicPlayer != null) {
            try {
                if (backgroundMusicPlayer.isPlaying()) {
                    backgroundMusicPlayer.stop();
                }
                backgroundMusicPlayer.release();
                backgroundMusicPlayer = null;
            } catch (Exception e) {
                Log.e(TAG, "Error stopping media player: " + e.getMessage());
            }
        }

        Log.d(TAG, "Background music stopped");
    }

    /**
     * Pause background music
     */
    public void pauseBackgroundMusic() {
        if (isMusicPlaying) {
            Log.d(TAG, "Pausing background music");
            isMusicPlaying = false;

            if (musicThread != null) {
                try {
                    musicThread.interrupt();
                    musicThread = null;
                } catch (Exception e) {
                    Log.e(TAG, "Error pausing music thread: " + e.getMessage());
                }
            }
        }

        // Keep this for compatibility
        if (backgroundMusicPlayer != null && backgroundMusicPlayer.isPlaying()) {
            try {
                backgroundMusicPlayer.pause();
                Log.d(TAG, "MediaPlayer paused");
            } catch (Exception e) {
                Log.e(TAG, "Error pausing background music: " + e.getMessage());
            }
        }
    }

    /**
     * Resume background music
     */
    public void resumeBackgroundMusic() {
        if (backgroundMusicEnabled && !isMusicPlaying) {
            Log.d(TAG, "Resuming background music");
            startBackgroundMusic();
        }

        // Keep this for compatibility
        if (backgroundMusicPlayer != null && backgroundMusicEnabled) {
            try {
                backgroundMusicPlayer.start();
                Log.d(TAG, "MediaPlayer resumed");
            } catch (Exception e) {
                Log.e(TAG, "Error resuming background music: " + e.getMessage());
            }
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
            if (!isMusicPlaying) {
                startBackgroundMusic();
            }
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

        // Stop background music
        stopBackgroundMusic();

        if (toneGenerator != null) {
            toneGenerator.release();
            toneGenerator = null;
        }

        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }

        isInitialized = false;
    }
}

