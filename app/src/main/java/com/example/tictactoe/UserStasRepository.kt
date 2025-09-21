package com.example.tictactoe

class UserStatsRepository(private val userStatsDao: UserStatsDao) {

    suspend fun getUserStats(): UserStats {
        // Retrieve the UserStats object from the database
        return userStatsDao.getUserStats()
    }

    suspend fun updateUserStats(userStats: UserStats) {
        // Update the UserStats object in the database
        userStatsDao.updateUserStats(userStats)
    }
}
