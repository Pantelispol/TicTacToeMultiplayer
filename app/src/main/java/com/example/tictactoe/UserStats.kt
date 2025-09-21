package com.example.tictactoe

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_stats")
data class UserStats(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long = 0, // Foreign key to link with User table
    var wins: Int = 0,
    var draws: Int = 0,
    var losses: Int = 0
)
