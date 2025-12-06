package com.example.courseapp.domain.repository

import com.example.courseapp.data.local.CategoryPreferences
import com.example.courseapp.data.remote.ApiService
import com.example.courseapp.domain.util.Result
import kotlinx.coroutines.flow.first

class CategoryRepository(
    private val apiService: ApiService,
    private val categoryPreferences: CategoryPreferences
) {
    
    suspend fun getCategories(): Result<List<String>> {
        return try {
            // Try to fetch from API
            val categories = apiService.getCategories().map { it.name }
            
            if (categories.isEmpty()) {
                throw Exception("Empty category list from API")
            }
            
            // Cache the categories
            categoryPreferences.saveCategories(categories)
            
            Result.Success(categories)
        } catch (e: Exception) {
            // If API fails, try to load from cache
            try {
                val cachedCategories = categoryPreferences.getCachedCategories().first()
                if (cachedCategories.isNotEmpty()) {
                    Result.Success(cachedCategories)
                } else {
                    // Return default categories if cache is empty
                    val defaultCategories = listOf(
                        "Programming",
                        "Design",
                        "Business",
                        "Marketing",
                        "Data Science",
                        "Mobile Development"
                    )
                    categoryPreferences.saveCategories(defaultCategories)
                    Result.Success(defaultCategories)
                }
            } catch (cacheError: Exception) {
                Result.Error("Failed to load categories: ${cacheError.message}")
            }
        }
    }
}
