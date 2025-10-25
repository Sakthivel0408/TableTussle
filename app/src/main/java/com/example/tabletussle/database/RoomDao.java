package com.example.tabletussle.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface RoomDao {

    @Insert
    long insertRoom(GameRoom room);

    @Update
    void updateRoom(GameRoom room);

    @Query("SELECT * FROM game_rooms WHERE roomCode = :code LIMIT 1")
    GameRoom getRoomByCode(String code);

    @Query("SELECT * FROM game_rooms WHERE hostUserId = :userId AND isActive = 1")
    List<GameRoom> getActiveRoomsByHost(int userId);

    @Query("SELECT * FROM game_rooms WHERE isActive = 1")
    List<GameRoom> getAllActiveRooms();

    @Query("UPDATE game_rooms SET isActive = 0 WHERE roomCode = :code")
    void deactivateRoom(String code);

    @Query("UPDATE game_rooms SET currentPlayers = :count WHERE roomCode = :code")
    void updatePlayerCount(String code, int count);

    @Query("DELETE FROM game_rooms WHERE createdAt < :timestamp")
    void deleteOldRooms(long timestamp);

    @Query("SELECT EXISTS(SELECT 1 FROM game_rooms WHERE roomCode = :code AND isActive = 1)")
    boolean isRoomCodeActive(String code);
}

