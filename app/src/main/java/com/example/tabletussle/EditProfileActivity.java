package com.example.tabletussle;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tabletussle.database.AppDatabase;
import com.example.tabletussle.database.User;
import com.example.tabletussle.database.UserDao;
import com.example.tabletussle.database.UserSession;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class EditProfileActivity extends AppCompatActivity {

    private TextInputEditText etUsername, etEmail;
    private MaterialButton btnSaveChanges, btnCancel;
    private UserSession userSession;
    private AppDatabase database;
    private UserDao userDao;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize database
        database = AppDatabase.getInstance(this);
        userDao = database.userDao();
        userSession = new UserSession(this);

        initializeViews();
        loadCurrentUserData();
        setupClickListeners();
    }

    private void initializeViews() {
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        btnCancel = findViewById(R.id.btnCancel);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void loadCurrentUserData() {
        int userId = userSession.getUserId();
        if (userId != -1) {
            currentUser = userDao.getUserById(userId);
            if (currentUser != null) {
                etUsername.setText(currentUser.getUsername());
                etEmail.setText(currentUser.getEmail());
            }
        }
    }

    private void setupClickListeners() {
        btnSaveChanges.setOnClickListener(v -> saveChanges());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void saveChanges() {
        String newUsername = etUsername.getText().toString().trim();
        String newEmail = etEmail.getText().toString().trim();

        // Validation
        if (TextUtils.isEmpty(newUsername)) {
            etUsername.setError("Username is required");
            etUsername.requestFocus();
            return;
        }

        if (newUsername.length() < 3) {
            etUsername.setError("Username must be at least 3 characters");
            etUsername.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(newEmail)) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
            etEmail.setError("Please enter a valid email");
            etEmail.requestFocus();
            return;
        }

        // Check if email is already taken by another user
        if (!newEmail.equals(currentUser.getEmail())) {
            User existingUser = userDao.getUserByEmail(newEmail);
            if (existingUser != null && existingUser.getId() != currentUser.getId()) {
                etEmail.setError("Email already in use");
                etEmail.requestFocus();
                Toast.makeText(this, "This email is already registered", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Update user data
        currentUser.setUsername(newUsername);
        currentUser.setEmail(newEmail);
        userDao.updateUser(currentUser);

        Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }
}

