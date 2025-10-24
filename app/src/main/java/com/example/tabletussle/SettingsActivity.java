package com.example.tabletussle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "TableTussleSettings";

    // Settings keys
    private static final String KEY_SOUND_EFFECTS = "sound_effects";
    private static final String KEY_BACKGROUND_MUSIC = "background_music";
    private static final String KEY_VIBRATION = "vibration";
    private static final String KEY_DARK_MODE = "dark_mode";
    private static final String KEY_ANIMATIONS = "animations";

    // UI Components
    private SwitchMaterial switchSoundEffects;
    private SwitchMaterial switchBackgroundMusic;
    private SwitchMaterial switchVibration;
    private SwitchMaterial switchDarkMode;
    private SwitchMaterial switchAnimations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        initializeViews();
        loadSettings();
        setupClickListeners();
    }

    private void initializeViews() {
        // Back button
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Switches
        switchSoundEffects = findViewById(R.id.switchSoundEffects);
        switchBackgroundMusic = findViewById(R.id.switchBackgroundMusic);
        switchVibration = findViewById(R.id.switchVibration);
        switchDarkMode = findViewById(R.id.switchDarkMode);
        switchAnimations = findViewById(R.id.switchAnimations);

        // Set app version
        TextView tvAppVersion = findViewById(R.id.tvAppVersion);
        try {
            String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            tvAppVersion.setText("Version " + versionName);
        } catch (Exception e) {
            tvAppVersion.setText("Version 1.0.0");
        }
    }

    private void loadSettings() {
        // Load saved preferences
        switchSoundEffects.setChecked(sharedPreferences.getBoolean(KEY_SOUND_EFFECTS, true));
        switchBackgroundMusic.setChecked(sharedPreferences.getBoolean(KEY_BACKGROUND_MUSIC, true));
        switchVibration.setChecked(sharedPreferences.getBoolean(KEY_VIBRATION, true));
        switchDarkMode.setChecked(sharedPreferences.getBoolean(KEY_DARK_MODE, true));
        switchAnimations.setChecked(sharedPreferences.getBoolean(KEY_ANIMATIONS, true));
    }

    private void setupClickListeners() {
        // Sound Effects
        switchSoundEffects.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveSetting(KEY_SOUND_EFFECTS, isChecked);
            showToast(isChecked ? "Sound effects enabled" : "Sound effects disabled");
        });

        // Background Music
        switchBackgroundMusic.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveSetting(KEY_BACKGROUND_MUSIC, isChecked);
            showToast(isChecked ? "Background music enabled" : "Background music disabled");
        });

        // Vibration
        switchVibration.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveSetting(KEY_VIBRATION, isChecked);
            showToast(isChecked ? "Vibration enabled" : "Vibration disabled");
        });

        // Dark Mode
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveSetting(KEY_DARK_MODE, isChecked);
            applyDarkMode(isChecked);
            showToast(isChecked ? "Dark mode enabled" : "Light mode enabled");
        });

        // Animations
        switchAnimations.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveSetting(KEY_ANIMATIONS, isChecked);
            showToast(isChecked ? "Animations enabled" : "Animations disabled");
        });

        // Notifications
        MaterialCardView cardNotifications = findViewById(R.id.cardNotifications);
        cardNotifications.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, NotificationsActivity.class);
            startActivity(intent);
        });

        // Privacy
        MaterialCardView cardPrivacy = findViewById(R.id.cardPrivacy);
        cardPrivacy.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, PrivacyActivity.class);
            startActivity(intent);
        });

        // Help & Support
        MaterialCardView cardHelp = findViewById(R.id.cardHelp);
        cardHelp.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, HelpActivity.class);
            startActivity(intent);
        });
    }

    private void saveSetting(String key, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    private void applyDarkMode(boolean isDarkMode) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Public method to get settings
    public static boolean getSoundEffectsSetting(android.content.Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getBoolean(KEY_SOUND_EFFECTS, true);
    }

    public static boolean getBackgroundMusicSetting(android.content.Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getBoolean(KEY_BACKGROUND_MUSIC, true);
    }

    public static boolean getVibrationSetting(android.content.Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getBoolean(KEY_VIBRATION, true);
    }

    public static boolean getAnimationsSetting(android.content.Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getBoolean(KEY_ANIMATIONS, true);
    }
}

