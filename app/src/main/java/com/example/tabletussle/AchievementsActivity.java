package com.example.tabletussle;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tabletussle.database.AppDatabase;
import com.example.tabletussle.database.User;
import com.example.tabletussle.database.UserDao;
import com.example.tabletussle.database.UserSession;

public class AchievementsActivity extends AppCompatActivity {

    private TextView tvAchievementProgress;

    // Progress TextViews
    private TextView progressFirstVictory, progressFirstSteps, progressWinningStreak;
    private TextView progressTenVictories, progressCenturyClub, progressChampion, progressPerfectScore;

    // Status TextViews (lock/unlock indicators)
    private TextView statusFirstVictory, statusFirstSteps, statusWinningStreak;
    private TextView statusTenVictories, statusCenturyClub, statusChampion, statusPerfectScore;

    // Icon TextViews (for visual effects)
    private TextView iconFirstVictory, iconFirstSteps, iconWinningStreak;
    private TextView iconTenVictories, iconCenturyClub, iconChampion, iconPerfectScore;

    private UserSession userSession;
    private AppDatabase database;
    private UserDao userDao;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);

        // Initialize database and session
        database = AppDatabase.getInstance(this);
        userDao = database.userDao();
        userSession = new UserSession(this);

        initializeViews();
        loadUserData();
        updateAchievements();

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    private void initializeViews() {
        tvAchievementProgress = findViewById(R.id.tvAchievementProgress);

        // Progress TextViews
        progressFirstVictory = findViewById(R.id.progressFirstVictory);
        progressFirstSteps = findViewById(R.id.progressFirstSteps);
        progressWinningStreak = findViewById(R.id.progressWinningStreak);
        progressTenVictories = findViewById(R.id.progressTenVictories);
        progressCenturyClub = findViewById(R.id.progressCenturyClub);
        progressChampion = findViewById(R.id.progressChampion);
        progressPerfectScore = findViewById(R.id.progressPerfectScore);

        // Status indicators
        statusFirstVictory = findViewById(R.id.statusFirstVictory);
        statusFirstSteps = findViewById(R.id.statusFirstSteps);
        statusWinningStreak = findViewById(R.id.statusWinningStreak);
        statusTenVictories = findViewById(R.id.statusTenVictories);
        statusCenturyClub = findViewById(R.id.statusCenturyClub);
        statusChampion = findViewById(R.id.statusChampion);
        statusPerfectScore = findViewById(R.id.statusPerfectScore);

        // Icon TextViews
        iconFirstVictory = findViewById(R.id.iconFirstVictory);
        iconFirstSteps = findViewById(R.id.iconFirstSteps);
        iconWinningStreak = findViewById(R.id.iconWinningStreak);
        iconTenVictories = findViewById(R.id.iconTenVictories);
        iconCenturyClub = findViewById(R.id.iconCenturyClub);
        iconChampion = findViewById(R.id.iconChampion);
        iconPerfectScore = findViewById(R.id.iconPerfectScore);
    }

    private void loadUserData() {
        int userId = userSession.getUserId();
        if (userId != -1) {
            currentUser = userDao.getUserById(userId);
        }
    }

    private void updateAchievements() {
        if (currentUser == null) {
            // Guest user - all achievements locked
            tvAchievementProgress.setText("0/12");
            setDefaultProgress();
            return;
        }

        int gamesPlayed = currentUser.getGamesPlayed();
        int gamesWon = currentUser.getGamesWon();
        int totalScore = currentUser.getTotalScore();

        int unlockedCount = 0;

        // First Victory Achievement
        if (gamesWon >= 1) {
            unlockAchievement(statusFirstVictory, iconFirstVictory);
            progressFirstVictory.setText("1/1 games won ✓");
            unlockedCount++;
        } else {
            progressFirstVictory.setText(gamesWon + "/1 games won");
        }

        // First Steps Achievement
        if (gamesPlayed >= 1) {
            unlockAchievement(statusFirstSteps, iconFirstSteps);
            progressFirstSteps.setText("1/1 games played ✓");
            unlockedCount++;
        } else {
            progressFirstSteps.setText(gamesPlayed + "/1 games played");
        }

        // Winning Streak Achievement (placeholder - needs streak tracking)
        // For now, we'll check if they have 5+ wins
        int currentStreak = Math.min(gamesWon, 5); // Simplified
        if (gamesWon >= 5) {
            unlockAchievement(statusWinningStreak, iconWinningStreak);
            progressWinningStreak.setText("5/5 streak ✓");
            unlockedCount++;
        } else {
            progressWinningStreak.setText(currentStreak + "/5 streak");
        }

        // Ten Victories Achievement
        if (gamesWon >= 10) {
            unlockAchievement(statusTenVictories, iconTenVictories);
            progressTenVictories.setText("10/10 games won ✓");
            unlockedCount++;
        } else {
            progressTenVictories.setText(gamesWon + "/10 games won");
        }

        // Century Club Achievement
        if (gamesPlayed >= 100) {
            unlockAchievement(statusCenturyClub, iconCenturyClub);
            progressCenturyClub.setText("100/100 games played ✓");
            unlockedCount++;
        } else {
            progressCenturyClub.setText(gamesPlayed + "/100 games played");
        }

        // Champion Achievement
        if (gamesWon >= 50) {
            unlockAchievement(statusChampion, iconChampion);
            progressChampion.setText("50/50 games won ✓");
            unlockedCount++;
        } else {
            progressChampion.setText(gamesWon + "/50 games won");
        }

        // Perfect Score Achievement
        if (totalScore >= 10000) {
            unlockAchievement(statusPerfectScore, iconPerfectScore);
            progressPerfectScore.setText("10,000/10,000 score ✓");
            unlockedCount++;
        } else {
            progressPerfectScore.setText(String.format("%,d/10,000 score", totalScore));
        }

        // Update total progress
        tvAchievementProgress.setText(unlockedCount + "/7");
    }

    private void setDefaultProgress() {
        progressFirstVictory.setText("0/1 games won");
        progressFirstSteps.setText("0/1 games played");
        progressWinningStreak.setText("0/5 streak");
        progressTenVictories.setText("0/10 games won");
        progressCenturyClub.setText("0/100 games played");
        progressChampion.setText("0/50 games won");
        progressPerfectScore.setText("0/10,000 score");
    }

    private void unlockAchievement(TextView statusView, TextView iconView) {
        statusView.setText("✓");
        statusView.setTextColor(getResources().getColor(R.color.accent_gold, null));
        // Make icon brighter/more visible when unlocked
        iconView.setAlpha(1.0f);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload data when returning to this activity
        loadUserData();
        updateAchievements();
    }
}

