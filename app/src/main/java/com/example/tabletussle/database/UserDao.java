package com.example.tabletussle.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface UserDao {

    @Insert
    long insertUser(User user);

    @Update
    void updateUser(User user);

    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    User login(String email, String password);

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    User getUserByEmail(String email);

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    User getUserById(int userId);

    @Query("UPDATE users SET gamesPlayed = gamesPlayed + 1 WHERE id = :userId")
    void incrementGamesPlayed(int userId);

    @Query("UPDATE users SET gamesWon = gamesWon + 1 WHERE id = :userId")
    void incrementGamesWon(int userId);

    @Query("UPDATE users SET totalScore = totalScore + :score WHERE id = :userId")
    void addScore(int userId, int score);

    @Query("UPDATE users SET lastLoginAt = :timestamp WHERE id = :userId")
    void updateLastLogin(int userId, long timestamp);

    @Query("DELETE FROM users WHERE id = :userId")
    void deleteUser(int userId);
}

