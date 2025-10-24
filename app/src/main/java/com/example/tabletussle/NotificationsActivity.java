package com.example.tabletussle;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class NotificationsActivity extends AppCompatActivity {

    private SwitchMaterial switchGameInvites, switchGameUpdates, switchFriendRequests,
                           switchAchievements, switchNewMessages, switchPromotions;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        preferences = getSharedPreferences("NotificationSettings", MODE_PRIVATE);

        initializeViews();
        loadSettings();
        setupListeners();
    }

    private void initializeViews() {
        switchGameInvites = findViewById(R.id.switchGameInvites);
        switchGameUpdates = findViewById(R.id.switchGameUpdates);
        switchFriendRequests = findViewById(R.id.switchFriendRequests);
        switchAchievements = findViewById(R.id.switchAchievements);
        switchNewMessages = findViewById(R.id.switchNewMessages);
        switchPromotions = findViewById(R.id.switchPromotions);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void loadSettings() {
        switchGameInvites.setChecked(preferences.getBoolean("game_invites", true));
        switchGameUpdates.setChecked(preferences.getBoolean("game_updates", true));
        switchFriendRequests.setChecked(preferences.getBoolean("friend_requests", true));
        switchAchievements.setChecked(preferences.getBoolean("achievements", true));
        switchNewMessages.setChecked(preferences.getBoolean("new_messages", true));
        switchPromotions.setChecked(preferences.getBoolean("promotions", false));
    }

    private void setupListeners() {
        switchGameInvites.setOnCheckedChangeListener(createListener("game_invites"));
        switchGameUpdates.setOnCheckedChangeListener(createListener("game_updates"));
        switchFriendRequests.setOnCheckedChangeListener(createListener("friend_requests"));
        switchAchievements.setOnCheckedChangeListener(createListener("achievements"));
        switchNewMessages.setOnCheckedChangeListener(createListener("new_messages"));
        switchPromotions.setOnCheckedChangeListener(createListener("promotions"));
    }

    private CompoundButton.OnCheckedChangeListener createListener(String key) {
        return (buttonView, isChecked) -> {
            preferences.edit().putBoolean(key, isChecked).apply();
        };
    }
}

