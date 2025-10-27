package com.example.tabletussle;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tabletussle.managers.AnimationManager;
import com.example.tabletussle.managers.SoundManager;
import com.example.tabletussle.managers.VibrationManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class MainActivity extends AppCompatActivity {

    private SoundManager soundManager;
    private VibrationManager vibrationManager;
    private AnimationManager animationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize managers
        soundManager = SoundManager.getInstance(this);
        vibrationManager = VibrationManager.getInstance(this);
        animationManager = AnimationManager.getInstance(this);

        // Start background music if enabled
        soundManager.startBackgroundMusic();

        setupClickListeners();
    }

    private void setupClickListeners() {
        // Profile button (top-right)
        MaterialCardView btnProfile = findViewById(R.id.btnProfile);

        // Main action buttons
        MaterialButton btnPlayNow = findViewById(R.id.btnPlayNow);
        MaterialButton btnQuickMatch = findViewById(R.id.btnQuickMatch);
        MaterialButton btnCreateRoom = findViewById(R.id.btnCreateRoom);
        MaterialButton btnJoinRoom = findViewById(R.id.btnJoinRoom);

        // Bottom navigation cards
        MaterialCardView cardHowToPlay = findViewById(R.id.cardHowToPlay);
        MaterialCardView cardStatistics = findViewById(R.id.cardStatistics);
        MaterialCardView cardSettings = findViewById(R.id.cardSettings);

        btnProfile.setOnClickListener(v -> {
            soundManager.playSound(SoundManager.SoundEffect.CLICK);
            vibrationManager.vibrate(VibrationManager.VibrationType.LIGHT);
            animationManager.animateButtonClick(v);
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        btnPlayNow.setOnClickListener(v -> {
            soundManager.playSound(SoundManager.SoundEffect.CLICK);
            vibrationManager.vibrate(VibrationManager.VibrationType.MEDIUM);
            animationManager.animateButtonClick(v);
            // Start single player game (vs AI)
            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            intent.putExtra("GAME_MODE", "single");
            startActivity(intent);
        });

        btnQuickMatch.setOnClickListener(v -> {
            soundManager.playSound(SoundManager.SoundEffect.CLICK);
            vibrationManager.vibrate(VibrationManager.VibrationType.LIGHT);
            animationManager.animateButtonClick(v);
            // Start quick match (matchmaking)
            Intent intent = new Intent(MainActivity.this, QuickMatchActivity.class);
            startActivity(intent);
        });

        btnCreateRoom.setOnClickListener(v -> {
            soundManager.playSound(SoundManager.SoundEffect.CLICK);
            vibrationManager.vibrate(VibrationManager.VibrationType.LIGHT);
            animationManager.animateButtonClick(v);
            // Create a new room
            Intent intent = new Intent(MainActivity.this, CreateRoomActivity.class);
            startActivity(intent);
        });

        btnJoinRoom.setOnClickListener(v -> {
            soundManager.playSound(SoundManager.SoundEffect.CLICK);
            vibrationManager.vibrate(VibrationManager.VibrationType.LIGHT);
            animationManager.animateButtonClick(v);
            // Join an existing room
            Intent intent = new Intent(MainActivity.this, JoinRoomActivity.class);
            startActivity(intent);
        });

        cardHowToPlay.setOnClickListener(v -> {
            soundManager.playSound(SoundManager.SoundEffect.CLICK);
            vibrationManager.vibrate(VibrationManager.VibrationType.LIGHT);
            animationManager.animateButtonClick(v);
            Intent intent = new Intent(MainActivity.this, HowToPlayActivity.class);
            startActivity(intent);
        });

        cardStatistics.setOnClickListener(v -> {
            soundManager.playSound(SoundManager.SoundEffect.CLICK);
            vibrationManager.vibrate(VibrationManager.VibrationType.LIGHT);
            animationManager.animateButtonClick(v);
            // Navigate to Statistics Activity
            Intent intent = new Intent(MainActivity.this, StatisticsActivity.class);
            startActivity(intent);
        });

        cardSettings.setOnClickListener(v -> {
            soundManager.playSound(SoundManager.SoundEffect.CLICK);
            vibrationManager.vibrate(VibrationManager.VibrationType.LIGHT);
            animationManager.animateButtonClick(v);
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Update settings when returning to main activity
        if (soundManager != null) {
            soundManager.updateSettings();
            soundManager.resumeBackgroundMusic();
        }
        if (vibrationManager != null) {
            vibrationManager.updateSettings();
        }
        if (animationManager != null) {
            animationManager.updateSettings();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Pause background music when activity goes to background
        if (soundManager != null) {
            soundManager.pauseBackgroundMusic();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Don't release sound manager here as it's a singleton used across activities
    }
}