package com.example.courseapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.courseapp.data.local.dao.CourseDao
import com.example.courseapp.data.local.entity.CourseEntity

@Database(
    entities = [CourseEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun courseDao(): CourseDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "course_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
