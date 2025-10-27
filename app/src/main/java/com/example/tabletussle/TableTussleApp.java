package com.example.tabletussle;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

public class TableTussleApp extends Application {

    private static final String PREFS_NAME = "TableTussleSettings";
    private static final String KEY_DARK_MODE = "dark_mode";

    @Override
    public void onCreate() {
        super.onCreate();

        // Apply dark mode setting before any activity is created
        applyThemeFromPreferences();
    }

    private void applyThemeFromPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isDarkMode = sharedPreferences.getBoolean(KEY_DARK_MODE, true);

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}

