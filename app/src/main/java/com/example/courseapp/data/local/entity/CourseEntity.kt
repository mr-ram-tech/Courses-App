package com.example.courseapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courses")
data class CourseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val category: String,
    val numberOfLessons: Int,
    val score: Int,
    val createdAt: Long = System.currentTimeMillis()
)
