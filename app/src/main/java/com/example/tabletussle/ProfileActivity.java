package com.example.tabletussle;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tabletussle.database.AppDatabase;
import com.example.tabletussle.database.User;
import com.example.tabletussle.database.UserDao;
import com.example.tabletussle.database.UserSession;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvUserName, tvUserEmail, tvMemberSince;
    private MaterialButton btnEditProfile, btnLogout;
    private MaterialCardView cardChangePassword, cardNotifications, cardPrivacy, cardHelp;
    private UserSession userSession;
    private AppDatabase database;
    private UserDao userDao;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_new);

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
        tvUserEmail = findViewById(R.id.tvUserEmail);
        tvMemberSince = findViewById(R.id.tvMemberSince);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnLogout = findViewById(R.id.btnLogout);
        cardChangePassword = findViewById(R.id.cardChangePassword);
        cardNotifications = findViewById(R.id.cardNotifications);
        cardPrivacy = findViewById(R.id.cardPrivacy);
        cardHelp = findViewById(R.id.cardHelp);
    }

    private void loadUserData() {
        int userId = userSession.getUserId();

        if (userId != -1) {
            currentUser = userDao.getUserById(userId);

            if (currentUser != null) {
                tvUserName.setText(currentUser.getUsername());
                tvUserEmail.setText(currentUser.getEmail());
                tvMemberSince.setText("Member since " + currentUser.getMemberSince());
            } else {
                // Guest user
                tvUserName.setText("Guest Player");
                tvUserEmail.setText("guest@tabletussle.com");
                tvMemberSince.setText("Member since October 2025");
            }
        } else {
            // Guest user
            tvUserName.setText("Guest Player");
            tvUserEmail.setText("guest@tabletussle.com");
            tvMemberSince.setText("Member since October 2025");
        }
    }

    private void setupClickListeners() {
        btnEditProfile.setOnClickListener(v -> {
            Toast.makeText(this, "Edit Profile - Coming Soon!", Toast.LENGTH_SHORT).show();
        });

        btnLogout.setOnClickListener(v -> handleLogout());

        cardChangePassword.setOnClickListener(v -> {
            Toast.makeText(this, "Change Password - Coming Soon!", Toast.LENGTH_SHORT).show();
        });

        cardNotifications.setOnClickListener(v -> {
            Toast.makeText(this, "Notification Settings - Coming Soon!", Toast.LENGTH_SHORT).show();
        });

        cardPrivacy.setOnClickListener(v -> {
            Toast.makeText(this, "Privacy Settings - Coming Soon!", Toast.LENGTH_SHORT).show();
        });

        cardHelp.setOnClickListener(v -> {
            Toast.makeText(this, "Help & Support - Coming Soon!", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void handleLogout() {
        userSession.logout();

        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}

