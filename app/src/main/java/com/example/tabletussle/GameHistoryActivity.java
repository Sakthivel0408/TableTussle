package com.example.tabletussle;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tabletussle.database.AppDatabase;
import com.example.tabletussle.database.User;
import com.example.tabletussle.database.UserDao;
import com.example.tabletussle.database.UserSession;
import com.google.android.material.chip.Chip;

public class GameHistoryActivity extends AppCompatActivity {

    private TextView tvTotalGames;
    private Chip chipAll, chipWins, chipLosses;
    private UserSession userSession;
    private AppDatabase database;
    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_history);

        // Initialize database and session
        database = AppDatabase.getInstance(this);
        userDao = database.userDao();
        userSession = new UserSession(this);

        initializeViews();
        loadGameHistory();
        setupClickListeners();
    }

    private void initializeViews() {
        tvTotalGames = findViewById(R.id.tvTotalGames);
        chipAll = findViewById(R.id.chipAll);
        chipWins = findViewById(R.id.chipWins);
        chipLosses = findViewById(R.id.chipLosses);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadGameHistory() {
        int userId = userSession.getUserId();

        if (userId != -1) {
            User user = userDao.getUserById(userId);
            if (user != null) {
                int totalGames = user.getGamesPlayed();
                tvTotalGames.setText("Total: " + totalGames);
                // TODO: Load actual game history from database
                return;
            }
        }

        // Guest user or no data
        tvTotalGames.setText("Total: 0");
        // TODO: Show empty state
    }

    private void setupClickListeners() {
        // Filter chips
        chipAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                showToast("Showing all games");
                // TODO: Load all games from database
            }
        });

        chipWins.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                showToast("Showing wins only");
                // TODO: Filter to show only wins
            }
        });

        chipLosses.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                showToast("Showing losses only");
                // TODO: Filter to show only losses
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload data when returning to this activity
        loadGameHistory();
    }

    // TODO: Add method to load game history from database
    // TODO: Add GameHistory entity and DAO
    // TODO: Add method to display game details
    // TODO: Add pagination for large histories
}

