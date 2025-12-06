package com.example.courseapp.data.remote

import com.example.courseapp.data.remote.dto.CategoryDto
import retrofit2.http.GET

interface ApiService {
    @GET("catagory")
    suspend fun getCategories(): List<CategoryDto>
}
