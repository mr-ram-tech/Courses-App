package com.example.courseapp.presentation.navigation

sealed class Screen(val route: String) {
    object CourseList : Screen("course_list")
    object AddCourse : Screen("add_course")
    object EditCourse : Screen("edit_course/{courseId}") {
        fun createRoute(courseId: Int) = "edit_course/$courseId"
    }
    object CourseDetail : Screen("course_detail/{courseId}") {
        fun createRoute(courseId: Int) = "course_detail/$courseId"
    }
}
