package com.example.tabletussle;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tabletussle.database.AppDatabase;
import com.example.tabletussle.database.User;
import com.example.tabletussle.database.UserDao;
import com.example.tabletussle.database.UserSession;
import com.google.android.material.card.MaterialCardView;

public class StatisticsActivity extends AppCompatActivity {

    private TextView tvUserName, tvGamesPlayed, tvGamesWon, tvWinRate, tvTotalScore;
    private MaterialCardView cardAchievements, cardFriends, cardHistory;
    private UserSession userSession;
    private AppDatabase database;
    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        // Initialize database and session
        database = AppDatabase.getInstance(this);
        userDao = database.userDao();
        userSession = new UserSession(this);

        initializeViews();
        loadUserData();
        setupClickListeners();
    }

    private void initializeViews() {
        tvUserName = findViewById(R.id.tvUserName);
        tvGamesPlayed = findViewById(R.id.tvGamesPlayed);
        tvGamesWon = findViewById(R.id.tvGamesWon);
        tvWinRate = findViewById(R.id.tvWinRate);
        tvTotalScore = findViewById(R.id.tvTotalScore);
        cardAchievements = findViewById(R.id.cardAchievements);
        cardFriends = findViewById(R.id.cardFriends);
        cardHistory = findViewById(R.id.cardHistory);
    }

    private void loadUserData() {
        int userId = userSession.getUserId();

        if (userId != -1) {
            User user = userDao.getUserById(userId);

            if (user != null) {
                tvUserName.setText(user.getUsername());
                tvGamesPlayed.setText(String.valueOf(user.getGamesPlayed()));
                tvGamesWon.setText(String.valueOf(user.getGamesWon()));
                tvTotalScore.setText(String.valueOf(user.getTotalScore()));

                // Calculate win rate
                if (user.getGamesPlayed() > 0) {
                    double winRate = (user.getGamesWon() * 100.0) / user.getGamesPlayed();
                    tvWinRate.setText(String.format("%.1f%%", winRate));
                } else {
                    tvWinRate.setText("0%");
                }
                return;
            }
        }

        // Guest user or no data
        tvUserName.setText("Guest Player");
        tvGamesPlayed.setText("0");
        tvGamesWon.setText("0");
        tvWinRate.setText("0%");
        tvTotalScore.setText("0");
    }

    private void setupClickListeners() {
        cardAchievements.setOnClickListener(v -> {
            Toast.makeText(this, "Achievements - Coming Soon!", Toast.LENGTH_SHORT).show();
        });

        cardFriends.setOnClickListener(v -> {
            Toast.makeText(this, "Friends - Coming Soon!", Toast.LENGTH_SHORT).show();
        });

        cardHistory.setOnClickListener(v -> {
            Toast.makeText(this, "Game History - Coming Soon!", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }
}

