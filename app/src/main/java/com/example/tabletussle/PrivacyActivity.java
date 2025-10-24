package com.example.tabletussle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tabletussle.database.AppDatabase;
import com.example.tabletussle.database.UserDao;
import com.example.tabletussle.database.UserSession;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class PrivacyActivity extends AppCompatActivity {

    private SwitchMaterial switchProfileVisibility, switchOnlineStatus, switchGameHistory, switchShowStats;
    private MaterialCardView cardDeleteAccount, cardDataExport;
    private SharedPreferences preferences;
    private UserSession userSession;
    private AppDatabase database;
    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);

        preferences = getSharedPreferences("PrivacySettings", MODE_PRIVATE);
        database = AppDatabase.getInstance(this);
        userDao = database.userDao();
        userSession = new UserSession(this);

        initializeViews();
        loadSettings();
        setupListeners();
    }

    private void initializeViews() {
        switchProfileVisibility = findViewById(R.id.switchProfileVisibility);
        switchOnlineStatus = findViewById(R.id.switchOnlineStatus);
        switchGameHistory = findViewById(R.id.switchGameHistory);
        switchShowStats = findViewById(R.id.switchShowStats);
        cardDeleteAccount = findViewById(R.id.cardDeleteAccount);
        cardDataExport = findViewById(R.id.cardDataExport);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void loadSettings() {
        switchProfileVisibility.setChecked(preferences.getBoolean("profile_visibility", true));
        switchOnlineStatus.setChecked(preferences.getBoolean("online_status", true));
        switchGameHistory.setChecked(preferences.getBoolean("game_history", true));
        switchShowStats.setChecked(preferences.getBoolean("show_stats", true));
    }

    private void setupListeners() {
        switchProfileVisibility.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.edit().putBoolean("profile_visibility", isChecked).apply();
            Toast.makeText(this, isChecked ? "Profile is now public" : "Profile is now private", Toast.LENGTH_SHORT).show();
        });

        switchOnlineStatus.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.edit().putBoolean("online_status", isChecked).apply();
        });

        switchGameHistory.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.edit().putBoolean("game_history", isChecked).apply();
        });

        switchShowStats.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.edit().putBoolean("show_stats", isChecked).apply();
        });

        cardDeleteAccount.setOnClickListener(v -> showDeleteAccountDialog());
        cardDataExport.setOnClickListener(v -> exportUserData());
    }

    private void showDeleteAccountDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Delete Account")
            .setMessage("Are you sure you want to permanently delete your account? This action cannot be undone.\n\nAll your data including:\n• Game history\n• Statistics\n• Friends\n• Achievements\n\nwill be permanently deleted.")
            .setPositiveButton("Delete", (dialog, which) -> confirmDeleteAccount())
            .setNegativeButton("Cancel", null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show();
    }

    private void confirmDeleteAccount() {
        new AlertDialog.Builder(this)
            .setTitle("Final Confirmation")
            .setMessage("This is your last chance. Delete your account permanently?")
            .setPositiveButton("Yes, Delete Forever", (dialog, which) -> deleteAccount())
            .setNegativeButton("No, Keep My Account", null)
            .setIcon(android.R.drawable.ic_delete)
            .show();
    }

    private void deleteAccount() {
        int userId = userSession.getUserId();
        if (userId != -1) {
            // Delete user from database
            userDao.deleteUser(userId);

            // Clear session
            userSession.logout();

            // Clear all preferences
            preferences.edit().clear().apply();

            Toast.makeText(this, "Account deleted successfully", Toast.LENGTH_LONG).show();

            // Navigate to login
            Intent intent = new Intent(PrivacyActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void exportUserData() {
        Toast.makeText(this, "Exporting your data... This feature will be available soon!", Toast.LENGTH_LONG).show();
        // In a real app, this would create a JSON/CSV file with all user data
    }
}

