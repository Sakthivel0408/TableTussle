package com.example.tabletussle.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {User.class, GameRoom.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public abstract UserDao userDao();
    public abstract RoomDao roomDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                context.getApplicationContext(),
                AppDatabase.class,
                "table_tussle_database"
            )
            .fallbackToDestructiveMigration() // For simplicity - recreate DB on version change
            .allowMainThreadQueries() // Only for simplicity - use background threads in production
            .build();
        }
        return instance;
    }
}

