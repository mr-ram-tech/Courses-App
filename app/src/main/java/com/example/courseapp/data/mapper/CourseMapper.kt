package com.example.courseapp.data.mapper

import com.example.courseapp.data.local.entity.CourseEntity
import com.example.courseapp.domain.model.Course

fun CourseEntity.toDomain(): Course {
    return Course(
        id = id,
        title = title,
        description = description,
        category = category,
        numberOfLessons = numberOfLessons,
        score = score,
        createdAt = createdAt
    )
}

fun Course.toEntity(): CourseEntity {
    return CourseEntity(
        id = id,
        title = title,
        description = description,
        category = category,
        numberOfLessons = numberOfLessons,
        score = score,
        createdAt = createdAt
    )
}
