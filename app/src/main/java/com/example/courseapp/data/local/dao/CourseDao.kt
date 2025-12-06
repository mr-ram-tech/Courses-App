package com.example.courseapp.data.local.dao

import androidx.room.*
import com.example.courseapp.data.local.entity.CourseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {
    @Query("SELECT * FROM courses ORDER BY createdAt DESC")
    fun getAllCourses(): Flow<List<CourseEntity>>
    
    @Query("SELECT * FROM courses WHERE id = :id")
    suspend fun getCourseById(id: Int): CourseEntity?
    
    @Query("SELECT * FROM courses WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    fun searchCourses(query: String): Flow<List<CourseEntity>>
    
    @Query("SELECT * FROM courses WHERE category = :category ORDER BY createdAt DESC")
    fun getCoursesByCategory(category: String): Flow<List<CourseEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: CourseEntity): Long
    
    @Update
    suspend fun updateCourse(course: CourseEntity)
    
    @Delete
    suspend fun deleteCourse(course: CourseEntity)
    
    @Query("DELETE FROM courses WHERE id = :id")
    suspend fun deleteCourseById(id: Int)
}
