package com.example.courseapp.domain.repository

import com.example.courseapp.data.local.dao.CourseDao
import com.example.courseapp.data.mapper.toDomain
import com.example.courseapp.data.mapper.toEntity
import com.example.courseapp.domain.model.Course
import com.example.courseapp.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CourseRepository(private val courseDao: CourseDao) {
    
    fun getAllCourses(): Flow<List<Course>> {
        return courseDao.getAllCourses().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    suspend fun getCourseById(id: Int): Course? {
        return courseDao.getCourseById(id)?.toDomain()
    }
    
    fun searchCourses(query: String): Flow<List<Course>> {
        return courseDao.searchCourses(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    fun getCoursesByCategory(category: String): Flow<List<Course>> {
        return courseDao.getCoursesByCategory(category).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    suspend fun insertCourse(course: Course): Result<Long> {
        return try {
            val id = courseDao.insertCourse(course.toEntity())
            Result.Success(id)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to insert course")
        }
    }
    
    suspend fun updateCourse(course: Course): Result<Unit> {
        return try {
            courseDao.updateCourse(course.toEntity())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to update course")
        }
    }
    
    suspend fun deleteCourse(course: Course): Result<Unit> {
        return try {
            courseDao.deleteCourse(course.toEntity())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to delete course")
        }
    }
    
    suspend fun deleteCourseById(id: Int): Result<Unit> {
        return try {
            courseDao.deleteCourseById(id)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to delete course")
        }
    }
}
