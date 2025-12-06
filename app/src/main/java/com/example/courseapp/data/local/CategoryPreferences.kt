package com.example.courseapp.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.categoryDataStore: DataStore<Preferences> by preferencesDataStore(name = "category_preferences")

class CategoryPreferences(private val context: Context) {
    private val gson = Gson()
    
    companion object {
        private val CATEGORIES_KEY = stringPreferencesKey("cached_categories")
    }
    
    suspend fun saveCategories(categories: List<String>) {
        val categoriesJson = gson.toJson(categories)
        context.categoryDataStore.edit { preferences ->
            preferences[CATEGORIES_KEY] = categoriesJson
        }
    }
    
    fun getCachedCategories(): Flow<List<String>> {
        return context.categoryDataStore.data.map { preferences ->
            val categoriesJson = preferences[CATEGORIES_KEY] ?: ""
            if (categoriesJson.isNotEmpty()) {
                val type = object : TypeToken<List<String>>() {}.type
                gson.fromJson(categoriesJson, type)
            } else {
                emptyList()
            }
        }
    }
}
