package com.example.tabletussle.database;

import android.content.Context;

/**
 * Helper class to manage game statistics in the database
 * Use this class when a game ends to update player stats
 */
public class GameStatsManager {

    private AppDatabase database;
    private UserDao userDao;
    private UserSession userSession;

    public GameStatsManager(Context context) {
        database = AppDatabase.getInstance(context);
        userDao = database.userDao();
        userSession = new UserSession(context);
    }

    /**
     * Call this when a game is completed
     * @param won - true if the user won, false if they lost
     * @param score - the score earned in this game
     */
    public void recordGameResult(boolean won, int score) {
        int userId = userSession.getUserId();

        if (userId != -1) {
            // Increment games played
            userDao.incrementGamesPlayed(userId);

            // Increment games won if player won
            if (won) {
                userDao.incrementGamesWon(userId);
            }

            // Add score
            userDao.addScore(userId, score);
        }
    }

    /**
     * Get the current user's statistics
     * @return User object with current stats, or null if not logged in
     */
    public User getCurrentUserStats() {
        int userId = userSession.getUserId();

        if (userId != -1) {
            return userDao.getUserById(userId);
        }

        return null;
    }

    /**
     * Check if user is logged in
     * @return true if logged in, false otherwise
     */
    public boolean isUserLoggedIn() {
        return userSession.isLoggedIn();
    }
}

