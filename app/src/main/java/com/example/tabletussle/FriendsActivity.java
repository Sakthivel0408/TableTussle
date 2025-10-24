package com.example.tabletussle;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;

public class FriendsActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private MaterialButton btnAddFriend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        tabLayout = findViewById(R.id.tabLayout);
        btnAddFriend = findViewById(R.id.btnAddFriend);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    private void setupClickListeners() {
        // Tab selection listener
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                switch (position) {
                    case 0: // All Friends
                        showToast("Showing all friends");
                        // TODO: Load all friends from database
                        break;
                    case 1: // Online
                        showToast("Showing online friends");
                        // TODO: Filter online friends
                        break;
                    case 2: // Requests
                        showToast("Showing friend requests");
                        // TODO: Load friend requests
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Not needed
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Not needed
            }
        });

        // Add friend button
        btnAddFriend.setOnClickListener(v -> {
            showToast("Add friend functionality - Coming soon!");
            // TODO: Show dialog to add friend by username/code
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // TODO: Add methods to load friends from database
    // TODO: Add method to send friend request
    // TODO: Add method to accept/reject friend request
    // TODO: Add method to remove friend
    // TODO: Add method to challenge friend to game
}

