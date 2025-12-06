package com.example.courseapp

import android.app.Application

class CourseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }
    
    companion object {
        lateinit var instance: CourseApplication
            private set
    }
}
