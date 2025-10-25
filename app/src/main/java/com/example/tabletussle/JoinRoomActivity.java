package com.example.tabletussle;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tabletussle.database.AppDatabase;
import com.example.tabletussle.database.GameRoom;
import com.example.tabletussle.database.RoomDao;
import com.example.tabletussle.database.UserSession;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class JoinRoomActivity extends AppCompatActivity {

    private TextInputLayout tilRoomCode;
    private TextInputEditText etRoomCode;
    private MaterialButton btnJoinRoom, btnScanQR;
    private ImageButton btnBack;

    private UserSession userSession;
    private AppDatabase database;
    private RoomDao roomDao;

    private ActivityResultLauncher<Intent> qrScannerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_room);

        // Initialize QR scanner launcher
        qrScannerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String roomCode = result.getData().getStringExtra("ROOM_CODE");
                    if (roomCode != null) {
                        etRoomCode.setText(roomCode);
                        joinRoom();
                    }
                }
            }
        );

        // Initialize database and session
        database = AppDatabase.getInstance(this);
        roomDao = database.roomDao();
        userSession = new UserSession(this);

        initializeViews();
        setupClickListeners();

        // Setup back press handler
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
    }

    private void initializeViews() {
        tilRoomCode = findViewById(R.id.tilRoomCode);
        etRoomCode = findViewById(R.id.etRoomCode);
        btnJoinRoom = findViewById(R.id.btnJoinRoom);
        btnScanQR = findViewById(R.id.btnScanQR);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnJoinRoom.setOnClickListener(v -> joinRoom());

        btnScanQR.setOnClickListener(v -> {
            Intent intent = new Intent(JoinRoomActivity.this, QRScannerActivity.class);
            qrScannerLauncher.launch(intent);
        });
    }

    private void joinRoom() {
        String roomCode = etRoomCode.getText().toString().trim().toUpperCase();

        // Validate room code
        if (roomCode.isEmpty()) {
            tilRoomCode.setError("Please enter a room code");
            etRoomCode.requestFocus();
            return;
        }

        if (roomCode.length() != 6) {
            tilRoomCode.setError("Room code must be 6 characters");
            etRoomCode.requestFocus();
            return;
        }

        // Clear any previous errors
        tilRoomCode.setError(null);

        // Verify the room exists in the database
        GameRoom room = roomDao.getRoomByCode(roomCode);

        if (room != null && room.isActive()) {
            // Check if room is full
            if (room.getCurrentPlayers() >= room.getMaxPlayers()) {
                tilRoomCode.setError("Room is full");
                etRoomCode.requestFocus();
                return;
            }

            // Update player count
            roomDao.updatePlayerCount(roomCode, room.getCurrentPlayers() + 1);

            Toast.makeText(this, "Joining room " + roomCode + "...", Toast.LENGTH_SHORT).show();

            // Navigate to game activity
            Intent intent = new Intent(JoinRoomActivity.this, GameActivity.class);
            intent.putExtra("GAME_MODE", "room");
            intent.putExtra("ROOM_CODE", roomCode);
            intent.putExtra("ROOM_NAME", room.getRoomName());
            intent.putExtra("SELECTED_MODE", room.getGameMode());
            intent.putExtra("IS_HOST", false);
            startActivity(intent);
            finish();
        } else {
            tilRoomCode.setError("Room not found or inactive. Please check the code.");
            etRoomCode.requestFocus();
        }
    }
}

