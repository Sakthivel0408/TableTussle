package com.example.tabletussle;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.tabletussle.database.AppDatabase;
import com.example.tabletussle.database.GameStatsManager;
import com.example.tabletussle.database.User;
import com.example.tabletussle.database.UserDao;
import com.example.tabletussle.database.UserSession;
import com.example.tabletussle.managers.SoundManager;
import com.example.tabletussle.managers.VibrationManager;
import com.example.tabletussle.managers.AnimationManager;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private TextView tvPlayer1Name, tvPlayer2Name;
    private TextView tvPlayer1Score, tvPlayer2Score;
    private TextView tvCurrentTurn;
    private ImageButton btnMenu, btnInfo;

    // Game board cells
    private MaterialButton[][] cells = new MaterialButton[3][3];

    // Game state
    private String[][] board = new String[3][3]; // "X", "O", or null
    private static final String PLAYER_X = "X"; // Human player
    private static final String PLAYER_O = "O"; // AI player
    private String currentPlayer = PLAYER_X;
    private boolean gameActive = true;
    private int movesCount = 0;

    private UserSession userSession;
    private AppDatabase database;
    private UserDao userDao;
    private GameStatsManager statsManager;

    // Feature managers
    private SoundManager soundManager;
    private VibrationManager vibrationManager;
    private AnimationManager animationManager;

    private String gameMode; // "single", "quick", "room"
    private String roomCode;
    private int player1Score = 0;
    private int player2Score = 0;

    private Handler handler = new Handler(Looper.getMainLooper());
    private Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Initialize database and session
        database = AppDatabase.getInstance(this);
        userDao = database.userDao();
        userSession = new UserSession(this);
        statsManager = new GameStatsManager(this);

        // Initialize feature managers
        soundManager = SoundManager.getInstance(this);
        vibrationManager = VibrationManager.getInstance(this);
        animationManager = AnimationManager.getInstance(this);

        // Get game mode from intent
        Intent intent = getIntent();
        gameMode = intent.getStringExtra("GAME_MODE");
        if (gameMode == null) {
            gameMode = "single"; // Default to single player
        }
        roomCode = intent.getStringExtra("ROOM_CODE");

        initializeViews();
        initializeBoard();
        loadPlayerInfo();
        setupClickListeners();
        setupBoardClickListeners();

        // Handle back button press
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showQuitConfirmation();
            }
        });
    }

    private void initializeViews() {
        tvPlayer1Name = findViewById(R.id.tvPlayer1Name);
        tvPlayer2Name = findViewById(R.id.tvPlayer2Name);
        tvPlayer1Score = findViewById(R.id.tvPlayer1Score);
        tvPlayer2Score = findViewById(R.id.tvPlayer2Score);
        tvCurrentTurn = findViewById(R.id.tvCurrentTurn);
        btnMenu = findViewById(R.id.btnMenu);
        btnInfo = findViewById(R.id.btnInfo);

        // Initialize cell buttons
        cells[0][0] = findViewById(R.id.cell_0_0);
        cells[0][1] = findViewById(R.id.cell_0_1);
        cells[0][2] = findViewById(R.id.cell_0_2);
        cells[1][0] = findViewById(R.id.cell_1_0);
        cells[1][1] = findViewById(R.id.cell_1_1);
        cells[1][2] = findViewById(R.id.cell_1_2);
        cells[2][0] = findViewById(R.id.cell_2_0);
        cells[2][1] = findViewById(R.id.cell_2_1);
        cells[2][2] = findViewById(R.id.cell_2_2);
    }

    private void initializeBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = null;
                cells[i][j].setText("");
                cells[i][j].setEnabled(true);
            }
        }
        currentPlayer = PLAYER_X;
        gameActive = true;
        movesCount = 0;
    }

    private void setupBoardClickListeners() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                final int row = i;
                final int col = j;
                cells[i][j].setOnClickListener(v -> onCellClicked(row, col));
            }
        }
    }

    private void onCellClicked(int row, int col) {
        if (!gameActive || board[row][col] != null || currentPlayer != PLAYER_X) {
            // Play error sound and shake animation for invalid move
            if (board[row][col] != null) {
                soundManager.playSound(SoundManager.SoundEffect.CLICK);
                vibrationManager.vibrate(VibrationManager.VibrationType.LIGHT);
                animationManager.animateShake(cells[row][col]);
            }
            return; // Invalid move
        }

        // Play sound and vibration for valid move
        soundManager.playSound(SoundManager.SoundEffect.MOVE);
        vibrationManager.vibrate(VibrationManager.VibrationType.MEDIUM);

        makeMove(row, col, PLAYER_X);

        if (gameActive && currentPlayer == PLAYER_O && gameMode.equals("single")) {
            // AI's turn - delay for better UX
            disableBoardInteraction();
            handler.postDelayed(() -> {
                makeAIMove();
                enableBoardInteraction();
            }, 500 + random.nextInt(500)); // Random delay 500-1000ms
        }
    }

    private void makeMove(int row, int col, String player) {
        board[row][col] = player;
        cells[row][col].setText(player);

        // Animate the cell fill
        animationManager.animateCellFill(cells[row][col]);

        // Style the cell based on player
        if (player.equals(PLAYER_X)) {
            cells[row][col].setTextColor(ContextCompat.getColor(this, R.color.primary));
        } else {
            cells[row][col].setTextColor(ContextCompat.getColor(this, R.color.secondary));
        }

        cells[row][col].setEnabled(false);
        movesCount++;

        // Check for win or draw
        if (checkWinner(player)) {
            gameActive = false;
            onGameEnd(player);
        } else if (movesCount == 9) {
            gameActive = false;
            onGameEnd(null); // Draw
        } else {
            // Switch player
            currentPlayer = currentPlayer.equals(PLAYER_X) ? PLAYER_O : PLAYER_X;
            updateTurnDisplay();
        }
    }

    private void makeAIMove() {
        if (!gameActive) return;

        // AI difficulty: 70% smart moves, 30% random moves
        // This makes the AI beatable while still challenging
        int[] move;

        // 70% of the time, use intelligent strategy
        if (random.nextInt(100) < 70) {
            move = findSmartMove();
        } else {
            // 30% of the time, make a random move
            move = findRandomMove();
        }

        if (move != null) {
            makeMove(move[0], move[1], PLAYER_O);
        }
    }

    /**
     * AI makes a smart strategic move
     * Priority: Win > Block > Center > Corner > Random
     */
    private int[] findSmartMove() {
        // 1st Priority: Check if AI can win
        int[] winMove = findWinningMove(PLAYER_O);
        if (winMove != null) {
            android.util.Log.d("GameAI", "AI: Found winning move!");
            return winMove;
        }

        // 2nd Priority: Block player from winning
        int[] blockMove = findWinningMove(PLAYER_X);
        if (blockMove != null) {
            android.util.Log.d("GameAI", "AI: Blocking player's winning move");
            return blockMove;
        }

        // 3rd Priority: Take center if available
        if (board[1][1] == null) {
            android.util.Log.d("GameAI", "AI: Taking center");
            return new int[]{1, 1};
        }

        // 4th Priority: Take a corner
        int[][] corners = {{0,0}, {0,2}, {2,0}, {2,2}};
        java.util.List<int[]> availableCorners = new java.util.ArrayList<>();
        for (int[] corner : corners) {
            if (board[corner[0]][corner[1]] == null) {
                availableCorners.add(corner);
            }
        }
        if (!availableCorners.isEmpty()) {
            android.util.Log.d("GameAI", "AI: Taking a corner");
            return availableCorners.get(random.nextInt(availableCorners.size()));
        }

        // 5th Priority: Take any available space
        android.util.Log.d("GameAI", "AI: Taking random available space");
        return findRandomMove();
    }

    /**
     * Find a winning move for the specified player
     */
    private int[] findWinningMove(String player) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == null) {
                    // Try this move
                    board[i][j] = player;
                    boolean wins = checkWinner(player);
                    board[i][j] = null; // Undo move

                    if (wins) {
                        return new int[]{i, j};
                    }
                }
            }
        }
        return null; // No winning move found
    }

    /**
     * Make a random move from available spaces
     */
    private int[] findRandomMove() {
        java.util.List<int[]> availableMoves = new java.util.ArrayList<>();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == null) {
                    availableMoves.add(new int[]{i, j});
                }
            }
        }

        if (!availableMoves.isEmpty()) {
            return availableMoves.get(random.nextInt(availableMoves.size()));
        }

        return null;
    }

    private boolean checkWinner(String player) {
        // Check rows
        for (int i = 0; i < 3; i++) {
            if (board[i][0] != null && board[i][0].equals(player) &&
                board[i][1] != null && board[i][1].equals(player) &&
                board[i][2] != null && board[i][2].equals(player)) {
                highlightWinningCells(new int[][]{{i,0}, {i,1}, {i,2}});
                return true;
            }
        }

        // Check columns
        for (int j = 0; j < 3; j++) {
            if (board[0][j] != null && board[0][j].equals(player) &&
                board[1][j] != null && board[1][j].equals(player) &&
                board[2][j] != null && board[2][j].equals(player)) {
                highlightWinningCells(new int[][]{{0,j}, {1,j}, {2,j}});
                return true;
            }
        }

        // Check diagonals
        if (board[0][0] != null && board[0][0].equals(player) &&
            board[1][1] != null && board[1][1].equals(player) &&
            board[2][2] != null && board[2][2].equals(player)) {
            highlightWinningCells(new int[][]{{0,0}, {1,1}, {2,2}});
            return true;
        }

        if (board[0][2] != null && board[0][2].equals(player) &&
            board[1][1] != null && board[1][1].equals(player) &&
            board[2][0] != null && board[2][0].equals(player)) {
            highlightWinningCells(new int[][]{{0,2}, {1,1}, {2,0}});
            return true;
        }

        return false;
    }

    private void highlightWinningCells(int[][] winningCells) {
        int highlightColor = ContextCompat.getColor(this, R.color.accent);

        // Create array of views for animation
        View[] winningViews = new View[winningCells.length];

        for (int i = 0; i < winningCells.length; i++) {
            int[] cell = winningCells[i];
            cells[cell[0]][cell[1]].setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(highlightColor)
            );
            winningViews[i] = cells[cell[0]][cell[1]];
        }

        // Animate winning cells
        animationManager.animateWinningCells(winningViews);
    }

    private boolean isBoardFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == null) {
                    return false;
                }
            }
        }
        return true;
    }

    private void disableBoardInteraction() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                cells[i][j].setClickable(false);
            }
        }
    }

    private void enableBoardInteraction() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == null) {
                    cells[i][j].setClickable(true);
                }
            }
        }
    }

    private void onGameEnd(String winner) {
        disableBoardInteraction();

        if (winner != null) {
            if (winner.equals(PLAYER_X)) {
                // Player wins
                soundManager.playSound(SoundManager.SoundEffect.WIN);
                vibrationManager.vibrate(VibrationManager.VibrationType.SUCCESS);
                player1Score++;
                updateScoreDisplay();
                handler.postDelayed(() -> showGameEndDialog(true), 800);
            } else {
                // AI wins
                soundManager.playSound(SoundManager.SoundEffect.LOSE);
                vibrationManager.vibrate(VibrationManager.VibrationType.ERROR);
                player2Score++;
                updateScoreDisplay();
                handler.postDelayed(() -> showGameEndDialog(false), 800);
            }
        } else {
            // Draw
            soundManager.playSound(SoundManager.SoundEffect.DRAW);
            vibrationManager.vibrate(VibrationManager.VibrationType.MEDIUM);
            handler.postDelayed(() -> showGameEndDialog(null), 800);
        }
    }

    private void showGameEndDialog(Boolean player1Won) {
        android.util.Log.d("GameStats", "====================================");
        android.util.Log.d("GameStats", "showGameEndDialog called");
        android.util.Log.d("GameStats", "player1Won = " + player1Won);

        // Update statistics in background thread
        updateGameStatistics(player1Won);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (player1Won == null) {
            builder.setTitle("It's a Draw!");
            builder.setMessage("Good game!\n\nFinal Score:\n" +
                    tvPlayer1Name.getText() + ": " + player1Score + "\n" +
                    tvPlayer2Name.getText() + ": " + player2Score);
        } else if (player1Won) {
            builder.setTitle("You Win! 🎉");
            builder.setMessage("Congratulations!\n\nFinal Score:\n" +
                    tvPlayer1Name.getText() + ": " + player1Score + "\n" +
                    tvPlayer2Name.getText() + ": " + player2Score);
        } else {
            builder.setTitle("AI Wins!");
            builder.setMessage("Better luck next time!\n\nFinal Score:\n" +
                    tvPlayer1Name.getText() + ": " + player1Score + "\n" +
                    tvPlayer2Name.getText() + ": " + player2Score);
        }

        builder.setPositiveButton("Play Again", (dialog, which) -> resetRound());
        builder.setNegativeButton("Exit", (dialog, which) -> finish());
        builder.setCancelable(false);
        builder.show();
    }

    /**
     * Update game statistics in the database
     * Runs on background thread to avoid blocking UI
     */
    private void updateGameStatistics(Boolean player1Won) {
        int userId = userSession.getUserId();

        android.util.Log.d("GameStats", "=== updateGameStatistics called ===");
        android.util.Log.d("GameStats", "player1Won = " + player1Won);
        android.util.Log.d("GameStats", "userId = " + userId);

        // Only track stats for logged-in users
        if (userId == -1) {
            android.util.Log.d("GameStats", "Skipping stats update - user not logged in");
            Toast.makeText(this, "Not logged in - stats not saved", Toast.LENGTH_SHORT).show();
            return;
        }

        // Run database operations on background thread
        new Thread(() -> {
            try {
                android.util.Log.d("GameStats", "Starting database operations...");

                // Always increment games played for any game completion
                try {
                    userDao.incrementGamesPlayed(userId);
                    android.util.Log.d("GameStats", "✓ Incremented games played for user " + userId);
                } catch (Exception e) {
                    android.util.Log.e("GameStats", "✗ Failed to increment games played: " + e.getMessage());
                    throw e;
                }

                // Increment games won only if player won
                if (player1Won != null && player1Won) {
                    try {
                        userDao.incrementGamesWon(userId);
                        android.util.Log.d("GameStats", "✓ Incremented games won for user " + userId);
                    } catch (Exception e) {
                        android.util.Log.e("GameStats", "✗ Failed to increment games won: " + e.getMessage());
                        throw e;
                    }
                }

                // Add score points:
                // Win = 10 points, Draw = 5 points, Loss = 0 points
                final int scoreToAdd;
                if (player1Won == null) {
                    scoreToAdd = 5; // Draw
                } else if (player1Won) {
                    scoreToAdd = 10; // Win
                } else {
                    scoreToAdd = 0; // Loss
                }

                if (scoreToAdd > 0) {
                    try {
                        userDao.addScore(userId, scoreToAdd);
                        android.util.Log.d("GameStats", "✓ Added " + scoreToAdd + " points for user " + userId);
                    } catch (Exception e) {
                        android.util.Log.e("GameStats", "✗ Failed to add score: " + e.getMessage());
                        throw e;
                    }
                }

                // Verify the update by reading back the user data
                try {
                    User updatedUser = userDao.getUserById(userId);
                    if (updatedUser != null) {
                        android.util.Log.d("GameStats", "✓ Verified - Games: " + updatedUser.getGamesPlayed() +
                                         ", Wins: " + updatedUser.getGamesWon() +
                                         ", Score: " + updatedUser.getTotalScore());
                    } else {
                        android.util.Log.e("GameStats", "✗ User not found after update!");
                    }
                } catch (Exception e) {
                    android.util.Log.e("GameStats", "✗ Failed to verify update: " + e.getMessage());
                }

                // Log the result for debugging
                final String result = player1Won == null ? "Draw" : (player1Won ? "Win" : "Loss");
                runOnUiThread(() -> {
                    Toast.makeText(this, "Stats updated: " + result + " (+" + scoreToAdd + " pts)",
                                   Toast.LENGTH_SHORT).show();
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                    Toast.makeText(this, "Failed to update statistics", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }

    private void resetRound() {
        // Clear the board for next round
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                cells[i][j].setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(
                        ContextCompat.getColor(this, R.color.cardBackground)
                    )
                );
            }
        }
        initializeBoard();
        enableBoardInteraction();
        updateTurnDisplay();
    }

    private void loadPlayerInfo() {
        int userId = userSession.getUserId();

        if (userId != -1) {
            User user = userDao.getUserById(userId);
            if (user != null) {
                tvPlayer1Name.setText(user.getUsername());
            }
        } else {
            tvPlayer1Name.setText("Guest");
        }

        // Set opponent based on game mode
        switch (gameMode) {
            case "single":
                tvPlayer2Name.setText("AI Opponent");
                break;
            case "quick":
            case "room":
                tvPlayer2Name.setText("Waiting for opponent...");
                break;
            default:
                tvPlayer2Name.setText("Player 2");
                break;
        }

        updateScoreDisplay();
        updateTurnDisplay();
    }

    private void setupClickListeners() {
        btnMenu.setOnClickListener(v -> showMenuDialog());

        btnInfo.setOnClickListener(v -> {
            // Show game info or rules
            Intent intent = new Intent(GameActivity.this, HowToPlayActivity.class);
            startActivity(intent);
        });

        // DEBUG: Add long press on info button to test stats
        btnInfo.setOnLongClickListener(v -> {
            testStatsUpdate();
            return true;
        });
    }

    /**
     * DEBUG METHOD: Test if database updates are working
     * Long press the Info button to trigger this
     */
    private void testStatsUpdate() {
        int userId = userSession.getUserId();

        if (userId == -1) {
            Toast.makeText(this, "DEBUG: Not logged in! userId = -1", Toast.LENGTH_LONG).show();
            return;
        }

        new Thread(() -> {
            try {
                // Read current stats
                User beforeUser = userDao.getUserById(userId);
                int gamesBefore = beforeUser != null ? beforeUser.getGamesPlayed() : 0;
                int winsBefore = beforeUser != null ? beforeUser.getGamesWon() : 0;

                // Update stats
                userDao.incrementGamesPlayed(userId);
                userDao.incrementGamesWon(userId);
                userDao.addScore(userId, 10);

                // Read updated stats
                User afterUser = userDao.getUserById(userId);
                int gamesAfter = afterUser != null ? afterUser.getGamesPlayed() : 0;
                int winsAfter = afterUser != null ? afterUser.getGamesWon() : 0;
                int scoreAfter = afterUser != null ? afterUser.getTotalScore() : 0;

                runOnUiThread(() -> {
                    String message = "DEBUG Stats Test:\n" +
                                   "Before: Games=" + gamesBefore + ", Wins=" + winsBefore + "\n" +
                                   "After: Games=" + gamesAfter + ", Wins=" + winsAfter + "\n" +
                                   "Score: " + scoreAfter + "\n" +
                                   (gamesAfter > gamesBefore ? "✅ UPDATE WORKED!" : "❌ UPDATE FAILED!");

                    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                    android.util.Log.d("GameStats", message);
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "DEBUG ERROR: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
                e.printStackTrace();
            }
        }).start();
    }

    private void showMenuDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Game Menu");

        String[] options = {"Resume", "New Game", "Settings", "Quit"};

        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0: // Resume
                    dialog.dismiss();
                    break;
                case 1: // New Game
                    showNewGameConfirmation();
                    break;
                case 2: // Settings
                    Intent intent = new Intent(GameActivity.this, SettingsActivity.class);
                    startActivity(intent);
                    break;
                case 3: // Quit
                    showQuitConfirmation();
                    break;
            }
        });

        builder.show();
    }

    private void showNewGameConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New Game");
        builder.setMessage("Are you sure you want to start a new game? Current scores will be reset.");

        builder.setPositiveButton("New Game", (dialog, which) -> {
            player1Score = 0;
            player2Score = 0;
            updateScoreDisplay();
            resetRound();
            Toast.makeText(this, "New game started", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showQuitConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quit Game");

        // Show session summary if user is logged in
        int userId = userSession.getUserId();
        String message = "Are you sure you want to quit?";

        if (userId != -1) {
            message = "Session Summary:\n" +
                     "Games Played: " + (player1Score + player2Score) + "\n" +
                     "Your Wins: " + player1Score + "\n" +
                     "AI Wins: " + player2Score + "\n\n" +
                     "Are you sure you want to quit?";
        }

        builder.setMessage(message);

        builder.setPositiveButton("Quit", (dialog, which) -> {
            // Show final statistics summary before exiting
            showFinalStatsSummary();
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    /**
     * Show final statistics summary when user exits the game
     */
    private void showFinalStatsSummary() {
        int userId = userSession.getUserId();

        if (userId != -1) {
            new Thread(() -> {
                try {
                    User user = userDao.getUserById(userId);

                    runOnUiThread(() -> {
                        if (user != null) {
                            int totalGames = user.getGamesPlayed();
                            int totalWins = user.getGamesWon();
                            int totalScore = user.getTotalScore();
                            double winRate = totalGames > 0 ? (totalWins * 100.0 / totalGames) : 0;

                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setTitle("Your Overall Statistics");
                            builder.setMessage(
                                "Total Games: " + totalGames + "\n" +
                                "Total Wins: " + totalWins + "\n" +
                                "Win Rate: " + String.format("%.1f", winRate) + "%\n" +
                                "Total Score: " + totalScore + "\n\n" +
                                "Great playing, " + user.getUsername() + "!"
                            );
                            builder.setPositiveButton("OK", (dialog, which) -> finish());
                            builder.setCancelable(false);
                            builder.show();
                        } else {
                            finish();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(this::finish);
                }
            }).start();
        } else {
            finish();
        }
    }

    private void updateScoreDisplay() {
        tvPlayer1Score.setText(String.valueOf(player1Score));
        tvPlayer2Score.setText(String.valueOf(player2Score));
    }

    private void updateTurnDisplay() {
        if (currentPlayer.equals(PLAYER_X)) {
            tvCurrentTurn.setText(tvPlayer1Name.getText() + "'s Turn");
        } else {
            tvCurrentTurn.setText(tvPlayer2Name.getText() + "'s Turn");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Resume background music when activity comes to foreground
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
        // Clean up resources
        if (soundManager != null) {
            soundManager.stopBackgroundMusic();
        }
        if (vibrationManager != null) {
            vibrationManager.cancel();
        }
    }
}
