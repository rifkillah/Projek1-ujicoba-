package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.LibraryBookDao
import com.example.data.dao.UserDao
import com.example.data.model.LibraryBookEntity
import com.example.data.model.UserEntity

@Database(entities = [UserEntity::class, LibraryBookEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun libraryBookDao(): LibraryBookDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pocketlib_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
