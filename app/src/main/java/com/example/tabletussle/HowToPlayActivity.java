package com.example.tabletussle;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class HowToPlayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to_play);

        // Back button
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }
}

