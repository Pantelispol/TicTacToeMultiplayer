package com.example.tictactoe

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UserStats::class], version = 1, exportSchema = false)
abstract class MyAppDatabase1 : RoomDatabase() {
    abstract fun userStatsDao(): UserStatsDao

    companion object {
        @Volatile
        private var INSTANCE: MyAppDatabase1? = null

        fun getInstance(context: Context): MyAppDatabase1 {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MyAppDatabase1::class.java,
                    "my_app_database1"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
