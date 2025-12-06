package com.example.courseapp.domain.model

data class Course(
    val id: Int = 0,
    val title: String,
    val description: String,
    val category: String,
    val numberOfLessons: Int,
    val score: Int,
    val createdAt: Long = System.currentTimeMillis()
)
