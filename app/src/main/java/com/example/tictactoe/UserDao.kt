package com.example.tictactoe


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("DELETE FROM users WHERE email = :email")
    suspend fun deleteUserByEmail(email: String)

    @Query("DELETE FROM users") // Assuming "users" is the table name
    suspend fun deleteAllEmails()

    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT id FROM users WHERE email = :email")
    suspend fun getUserIdByEmail(email: String): Long

    @Update
    suspend fun updateUser(user: User)

    @Query("SELECT username FROM users WHERE id = :id")
    fun getUsernameById(id: Long): String?


}