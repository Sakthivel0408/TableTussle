package com.example.tabletussle;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tabletussle.database.AppDatabase;
import com.example.tabletussle.database.UserDao;
import com.example.tabletussle.database.UserSession;
import com.google.android.material.button.MaterialButton;


public class QuickMatchActivity extends AppCompatActivity {

    private TextView tvStatus;
    private ProgressBar progressBar;
    private MaterialButton btnCancel;
    private ImageButton btnBack;

    private UserSession userSession;
    private AppDatabase database;
    private UserDao userDao;

    private Handler handler;
    private boolean isSearching = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_match);

        // Initialize database and session
        database = AppDatabase.getInstance(this);
        userDao = database.userDao();
        userSession = new UserSession(this);
        handler = new Handler();

        initializeViews();
        setupClickListeners();
        startMatchmaking();
    }

    private void initializeViews() {
        tvStatus = findViewById(R.id.tvStatus);
        progressBar = findViewById(R.id.progressBar);
        btnCancel = findViewById(R.id.btnCancel);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupClickListeners() {
        btnCancel.setOnClickListener(v -> cancelSearch());
        btnBack.setOnClickListener(v -> cancelSearch());
    }

    private void startMatchmaking() {
        tvStatus.setText("Searching for opponent...");
        progressBar.setIndeterminate(true);

        // Simulate matchmaking process
        handler.postDelayed(() -> {
            if (isSearching) {
                updateSearchStatus("Analyzing player skill...");
            }
        }, 1000);

        handler.postDelayed(() -> {
            if (isSearching) {
                updateSearchStatus("Finding suitable opponent...");
            }
        }, 2500);

        handler.postDelayed(() -> {
            if (isSearching) {
                updateSearchStatus("Connecting to match...");
            }
        }, 4000);

        // Simulate finding a match after 5 seconds
        handler.postDelayed(() -> {
            if (isSearching) {
                matchFound();
            }
        }, 5500);
    }

    private void updateSearchStatus(String status) {
        tvStatus.setText(status);
    }

    private void matchFound() {
        isSearching = false;
        progressBar.setIndeterminate(false);
        progressBar.setProgress(100);
        tvStatus.setText("Match Found!");

        Toast.makeText(this, "Opponent found! Starting game...", Toast.LENGTH_SHORT).show();

        // Wait a moment before starting the game
        handler.postDelayed(() -> {
            Intent intent = new Intent(QuickMatchActivity.this, GameActivity.class);
            intent.putExtra("GAME_MODE", "quick");
            startActivity(intent);
            finish();
        }, 1500);
    }

    private void cancelSearch() {
        isSearching = false;
        handler.removeCallbacksAndMessages(null);
        Toast.makeText(this, "Search cancelled", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onBackPressed() {
        cancelSearch();
    }
}

