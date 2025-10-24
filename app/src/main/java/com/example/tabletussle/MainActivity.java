package com.example.tabletussle;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class MainActivity extends AppCompatActivity {

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
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        btnPlayNow.setOnClickListener(v ->
            showToast("Starting game...")
        );

        btnQuickMatch.setOnClickListener(v ->
            showToast("Finding a quick match...")
        );

        btnCreateRoom.setOnClickListener(v ->
            showToast("Creating a new room...")
        );

        btnJoinRoom.setOnClickListener(v ->
            showToast("Join an existing room...")
        );

        cardHowToPlay.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HowToPlayActivity.class);
            startActivity(intent);
        });

        cardStatistics.setOnClickListener(v -> {
            // Navigate to Statistics Activity
            Intent intent = new Intent(MainActivity.this, StatisticsActivity.class);
            startActivity(intent);
        });

        cardSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}