package com.example.tabletussle;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tabletussle.database.AppDatabase;
import com.example.tabletussle.database.GameRoom;
import com.example.tabletussle.database.RoomDao;
import com.example.tabletussle.database.User;
import com.example.tabletussle.database.UserDao;
import com.example.tabletussle.database.UserSession;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Random;

public class CreateRoomActivity extends AppCompatActivity {

    private TextView tvRoomCode;
    private TextInputEditText etRoomName;
    private ChipGroup chipGroupGameMode;
    private Chip chipClassic, chipTimed, chipBlitz;
    private MaterialButton btnCopyCode, btnShareCode, btnStartGame;
    private ImageButton btnBack;

    private UserSession userSession;
    private AppDatabase database;
    private UserDao userDao;
    private RoomDao roomDao;

    private String roomCode;
    private String selectedGameMode = "classic";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);

        // Initialize database and session
        database = AppDatabase.getInstance(this);
        userDao = database.userDao();
        roomDao = database.roomDao();
        userSession = new UserSession(this);

        initializeViews();
        generateRoomCode();
        setupClickListeners();
        setupBackPressHandler();
    }

    private void setupBackPressHandler() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Confirm before leaving
                new androidx.appcompat.app.AlertDialog.Builder(CreateRoomActivity.this)
                        .setTitle("Cancel Room Creation")
                        .setMessage("Are you sure you want to cancel creating this room?")
                        .setPositiveButton("Yes", (dialog, which) -> finish())
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }

    private void initializeViews() {
        tvRoomCode = findViewById(R.id.tvRoomCode);
        etRoomName = findViewById(R.id.etRoomName);
        chipGroupGameMode = findViewById(R.id.chipGroupGameMode);
        chipClassic = findViewById(R.id.chipClassic);
        chipTimed = findViewById(R.id.chipTimed);
        chipBlitz = findViewById(R.id.chipBlitz);
        btnCopyCode = findViewById(R.id.btnCopyCode);
        btnShareCode = findViewById(R.id.btnShareCode);
        btnStartGame = findViewById(R.id.btnStartGame);
        btnBack = findViewById(R.id.btnBack);

        // Set default room name
        int userId = userSession.getUserId();
        if (userId != -1) {
            User user = userDao.getUserById(userId);
            if (user != null) {
                etRoomName.setText(user.getUsername() + "'s Room");
            }
        } else {
            etRoomName.setText("Guest's Room");
        }
    }

    private void generateRoomCode() {
        // Generate a 6-character alphanumeric room code
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();

        // Ensure unique room code
        do {
            StringBuilder code = new StringBuilder();
            for (int i = 0; i < 6; i++) {
                code.append(chars.charAt(random.nextInt(chars.length())));
            }
            roomCode = code.toString();
        } while (roomDao.isRoomCodeActive(roomCode));

        tvRoomCode.setText(roomCode);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnCopyCode.setOnClickListener(v -> copyRoomCode());

        btnShareCode.setOnClickListener(v -> shareRoomCode());

        btnStartGame.setOnClickListener(v -> startGame());

        // Game mode selection
        chipClassic.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedGameMode = "classic";
            }
        });

        chipTimed.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedGameMode = "timed";
            }
        });

        chipBlitz.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedGameMode = "blitz";
            }
        });
    }

    private void copyRoomCode() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Room Code", roomCode);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(this, "Room code copied to clipboard", Toast.LENGTH_SHORT).show();
    }

    private void shareRoomCode() {
        String roomName = etRoomName.getText().toString().trim();
        if (roomName.isEmpty()) {
            roomName = "Game Room";
        }

        String shareMessage = "Join my Table Tussle game!\n\n" +
                "Room Name: " + roomName + "\n" +
                "Room Code: " + roomCode + "\n" +
                "Game Mode: " + selectedGameMode.toUpperCase() + "\n\n" +
                "Download Table Tussle and enter the room code to play!";

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Join my Table Tussle game!");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);

        startActivity(Intent.createChooser(shareIntent, "Share room code via"));
    }

    private void startGame() {
        String roomName = etRoomName.getText().toString().trim();

        if (roomName.isEmpty()) {
            Toast.makeText(this, "Please enter a room name", Toast.LENGTH_SHORT).show();
            etRoomName.requestFocus();
            return;
        }

        // Create the room in the database
        int userId = userSession.getUserId();
        if (userId == -1) {
            userId = 0; // Guest user
        }

        GameRoom gameRoom = new GameRoom(roomCode, roomName, selectedGameMode, userId);
        long roomId = roomDao.insertRoom(gameRoom);

        if (roomId > 0) {
            Toast.makeText(this, "Room created! Waiting for players...", Toast.LENGTH_SHORT).show();

            // Navigate to game activity
            Intent intent = new Intent(CreateRoomActivity.this, GameActivity.class);
            intent.putExtra("GAME_MODE", "room");
            intent.putExtra("ROOM_CODE", roomCode);
            intent.putExtra("ROOM_NAME", roomName);
            intent.putExtra("SELECTED_MODE", selectedGameMode);
            intent.putExtra("IS_HOST", true);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Failed to create room. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }
}

