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

public class ChangePasswordActivity extends AppCompatActivity {

    private TextInputEditText etCurrentPassword, etNewPassword, etConfirmPassword;
    private MaterialButton btnChangePassword, btnCancel;
    private UserSession userSession;
    private AppDatabase database;
    private UserDao userDao;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // Initialize database
        database = AppDatabase.getInstance(this);
        userDao = database.userDao();
        userSession = new UserSession(this);

        initializeViews();
        loadCurrentUser();
        setupClickListeners();
    }

    private void initializeViews() {
        etCurrentPassword = findViewById(R.id.etCurrentPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnCancel = findViewById(R.id.btnCancel);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void loadCurrentUser() {
        int userId = userSession.getUserId();
        if (userId != -1) {
            currentUser = userDao.getUserById(userId);
        }
    }

    private void setupClickListeners() {
        btnChangePassword.setOnClickListener(v -> changePassword());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void changePassword() {
        String currentPassword = etCurrentPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validation
        if (TextUtils.isEmpty(currentPassword)) {
            etCurrentPassword.setError("Current password is required");
            etCurrentPassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(newPassword)) {
            etNewPassword.setError("New password is required");
            etNewPassword.requestFocus();
            return;
        }

        if (newPassword.length() < 6) {
            etNewPassword.setError("Password must be at least 6 characters");
            etNewPassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            etConfirmPassword.setError("Please confirm your password");
            etConfirmPassword.requestFocus();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return;
        }

        // Verify current password
        if (currentUser == null || !currentUser.getPassword().equals(currentPassword)) {
            etCurrentPassword.setError("Current password is incorrect");
            etCurrentPassword.requestFocus();
            Toast.makeText(this, "Current password is incorrect", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if new password is same as old
        if (currentPassword.equals(newPassword)) {
            etNewPassword.setError("New password must be different from current password");
            etNewPassword.requestFocus();
            return;
        }

        // Update password
        currentUser.setPassword(newPassword);
        userDao.updateUser(currentUser);

        Toast.makeText(this, "Password changed successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }
}

