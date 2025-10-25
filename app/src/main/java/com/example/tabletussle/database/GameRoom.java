package com.example.tabletussle.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "game_rooms")
public class GameRoom {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String roomCode;
    private String roomName;
    private String gameMode; // classic, timed, blitz
    private int hostUserId;
    private long createdAt;
    private boolean isActive;
    private int maxPlayers;
    private int currentPlayers;

    public GameRoom(String roomCode, String roomName, String gameMode, int hostUserId) {
        this.roomCode = roomCode;
        this.roomName = roomName;
        this.gameMode = gameMode;
        this.hostUserId = hostUserId;
        this.createdAt = System.currentTimeMillis();
        this.isActive = true;
        this.maxPlayers = 2;
        this.currentPlayers = 1;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getGameMode() {
        return gameMode;
    }

    public void setGameMode(String gameMode) {
        this.gameMode = gameMode;
    }

    public int getHostUserId() {
        return hostUserId;
    }

    public void setHostUserId(int hostUserId) {
        this.hostUserId = hostUserId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public int getCurrentPlayers() {
        return currentPlayers;
    }

    public void setCurrentPlayers(int currentPlayers) {
        this.currentPlayers = currentPlayers;
    }
}

