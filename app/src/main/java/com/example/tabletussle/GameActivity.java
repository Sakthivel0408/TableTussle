package com.example.tabletussle;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tabletussle.database.AppDatabase;
import com.example.tabletussle.database.GameStatsManager;
import com.example.tabletussle.database.User;
import com.example.tabletussle.database.UserDao;
import com.example.tabletussle.database.UserSession;

public class GameActivity extends AppCompatActivity {

    private TextView tvPlayer1Name, tvPlayer2Name;
    private TextView tvPlayer1Score, tvPlayer2Score;
    private TextView tvCurrentTurn;
    private ImageButton btnMenu, btnInfo;

    private UserSession userSession;
    private AppDatabase database;
    private UserDao userDao;
    private GameStatsManager statsManager;

    private String gameMode; // "single", "quick", "room"
    private String roomCode;
    private int player1Score = 0;
    private int player2Score = 0;
    private boolean isPlayer1Turn = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Initialize database and session
        database = AppDatabase.getInstance(this);
        userDao = database.userDao();
        userSession = new UserSession(this);
        statsManager = new GameStatsManager(this);

        // Get game mode from intent
        Intent intent = getIntent();
        gameMode = intent.getStringExtra("GAME_MODE");
        roomCode = intent.getStringExtra("ROOM_CODE");

        initializeViews();
        loadPlayerInfo();
        setupClickListeners();
    }

    private void initializeViews() {
        tvPlayer1Name = findViewById(R.id.tvPlayer1Name);
        tvPlayer2Name = findViewById(R.id.tvPlayer2Name);
        tvPlayer1Score = findViewById(R.id.tvPlayer1Score);
        tvPlayer2Score = findViewById(R.id.tvPlayer2Score);
        tvCurrentTurn = findViewById(R.id.tvCurrentTurn);
        btnMenu = findViewById(R.id.btnMenu);
        btnInfo = findViewById(R.id.btnInfo);
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
    }

    private void showMenuDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Game Menu");

        String[] options = {"Resume", "Restart", "Settings", "Quit"};

        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0: // Resume
                    dialog.dismiss();
                    break;
                case 1: // Restart
                    restartGame();
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

    private void restartGame() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Restart Game");
        builder.setMessage("Are you sure you want to restart the game? Current progress will be lost.");

        builder.setPositiveButton("Restart", (dialog, which) -> {
            player1Score = 0;
            player2Score = 0;
            isPlayer1Turn = true;
            updateScoreDisplay();
            updateTurnDisplay();
            Toast.makeText(this, "Game restarted", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showQuitConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quit Game");
        builder.setMessage("Are you sure you want to quit? Game progress will be lost.");

        builder.setPositiveButton("Quit", (dialog, which) -> {
            // Record game as incomplete/quit
            finish();
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void updateScoreDisplay() {
        tvPlayer1Score.setText(String.valueOf(player1Score));
        tvPlayer2Score.setText(String.valueOf(player2Score));
    }

    private void updateTurnDisplay() {
        if (isPlayer1Turn) {
            tvCurrentTurn.setText(tvPlayer1Name.getText() + "'s Turn");
        } else {
            tvCurrentTurn.setText(tvPlayer2Name.getText() + "'s Turn");
        }
    }

    private void endGame(boolean player1Won) {
        int userId = userSession.getUserId();

        if (userId != -1) {
            // Update user stats - pass won status and final score
            int finalScore = player1Won ? player1Score : player2Score;
            statsManager.recordGameResult(player1Won, finalScore);
        }

        // Show game over dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Game Over");

        String winner = player1Won ? tvPlayer1Name.getText().toString() : tvPlayer2Name.getText().toString();
        builder.setMessage(winner + " wins!\n\nFinal Score:\n" +
                          tvPlayer1Name.getText() + ": " + player1Score + "\n" +
                          tvPlayer2Name.getText() + ": " + player2Score);

        builder.setPositiveButton("Play Again", (dialog, which) -> restartGame());

        builder.setNegativeButton("Exit", (dialog, which) -> finish());

        builder.setCancelable(false);
        builder.show();
    }

    @Override
    public void onBackPressed() {
        showQuitConfirmation();
    }
}

