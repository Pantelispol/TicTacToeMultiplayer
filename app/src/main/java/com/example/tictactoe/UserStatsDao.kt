package com.example.tictactoe

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserStatsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(userStats: UserStats)

    @Update
    suspend fun updateUserStats(userStats: UserStats)

    @Delete
    suspend fun deleteUserStats(userStats: UserStats)

    @Query("SELECT * FROM user_stats")
    suspend fun getAllUserStats(): List<UserStats>

    @Query("SELECT * FROM user_stats WHERE userId = :userId")
    suspend fun getUserStatsByUserId(userId: Long): UserStats?

    @Query("SELECT SUM(wins) FROM user_stats WHERE userId = :userId")
    suspend fun getTotalWinsByUserId(userId: Long): Int

    @Query("SELECT SUM(draws) FROM user_stats WHERE userId = :userId")
    suspend fun getTotalDrawsByUserId(userId: Long): Int

    @Query("SELECT SUM(losses) FROM user_stats WHERE userId = :userId")
    suspend fun getTotalLossesByUserId(userId: Long): Int

    @Query("UPDATE user_stats SET wins = wins + 1 WHERE userId = :userId")
    suspend fun incrementWins(userId: Long)


    @Query("SELECT * FROM user_stats LIMIT 1")
    suspend fun getUserStats(): UserStats

    @Query("SELECT userId FROM user_stats WHERE id = :userStatsId")
    suspend fun getUserIdByStatsId(userStatsId: Long): Long

    @Query("DELETE FROM user_stats WHERE userId = :userId")
    suspend fun deleteByUserId(userId: Long)


}
