package com.example.tabletussle;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;

public class HelpActivity extends AppCompatActivity {

    private MaterialCardView cardFAQ, cardContactSupport, cardReportBug, cardTerms, cardPrivacyPolicy, cardAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        cardFAQ = findViewById(R.id.cardFAQ);
        cardContactSupport = findViewById(R.id.cardContactSupport);
        cardReportBug = findViewById(R.id.cardReportBug);
        cardTerms = findViewById(R.id.cardTerms);
        cardPrivacyPolicy = findViewById(R.id.cardPrivacyPolicy);
        cardAbout = findViewById(R.id.cardAbout);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void setupClickListeners() {
        cardFAQ.setOnClickListener(v -> {
            Toast.makeText(this, "Opening FAQ...", Toast.LENGTH_SHORT).show();
            // In a real app, this would open an FAQ activity or web page
        });

        cardContactSupport.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:support@tabletussle.com"));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Table Tussle Support Request");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Please describe your issue:\n\n");

            try {
                startActivity(Intent.createChooser(emailIntent, "Send email via..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show();
            }
        });

        cardReportBug.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:bugs@tabletussle.com"));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Bug Report - Table Tussle");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Bug Description:\n\n\nSteps to Reproduce:\n1. \n2. \n3. \n\nExpected Behavior:\n\n\nActual Behavior:\n\n");

            try {
                startActivity(Intent.createChooser(emailIntent, "Send email via..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show();
            }
        });

        cardTerms.setOnClickListener(v -> {
            Toast.makeText(this, "Opening Terms of Service...", Toast.LENGTH_SHORT).show();
            // In a real app, open terms URL or activity
        });

        cardPrivacyPolicy.setOnClickListener(v -> {
            Toast.makeText(this, "Opening Privacy Policy...", Toast.LENGTH_SHORT).show();
            // In a real app, open privacy policy URL or activity
        });

        cardAbout.setOnClickListener(v -> {
            showAboutDialog();
        });
    }

    private void showAboutDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("About Table Tussle")
            .setMessage("Table Tussle - Tic Tac Toe Game\n\n" +
                    "Version: 1.0.0\n" +
                    "Build: October 2025\n\n" +
                    "A digital adaptation of the classic Tic Tac Toe game with AI opponent!\n\n" +
                    "Developed with ❤️ for strategy game enthusiasts\n\n" +
                    "© 2025 Table Tussle. All rights reserved.")
            .setPositiveButton("OK", null)
            .setIcon(R.mipmap.ic_launcher)
            .show();
    }
}

