package com.example.courseapp.presentation.addedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.courseapp.domain.model.Course
import com.example.courseapp.domain.repository.CategoryRepository
import com.example.courseapp.domain.repository.CourseRepository
import com.example.courseapp.domain.util.Result
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class AddEditCourseState(
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val numberOfLessons: String = "",
    val categories: List<String> = emptyList(),
    val isLoadingCategories: Boolean = true,
    val isSaving: Boolean = false,
    val error: String? = null,
    val isEditMode: Boolean = false,
    val courseId: Int? = null,
    val savedSuccessfully: Boolean = false
)

class AddEditCourseViewModel(
    private val courseRepository: CourseRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(AddEditCourseState())
    val state: StateFlow<AddEditCourseState> = _state.asStateFlow()
    
    init {
        loadCategories()
    }
    
    fun loadCourse(courseId: Int) {
        viewModelScope.launch {
            val course = courseRepository.getCourseById(courseId)
            if (course != null) {
                _state.update { 
                    it.copy(
                        title = course.title,
                        description = course.description,
                        category = course.category,
                        numberOfLessons = course.numberOfLessons.toString(),
                        isEditMode = true,
                        courseId = course.id
                    )
                }
            }
        }
    }
    
    private fun loadCategories() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingCategories = true) }
            
            when (val result = categoryRepository.getCategories()) {
                is Result.Success -> {
                    _state.update { 
                        it.copy(
                            categories = result.data,
                            isLoadingCategories = false
                        )
                    }
                }
                is Result.Error -> {
                    _state.update { 
                        it.copy(
                            isLoadingCategories = false,
                            error = result.message
                        )
                    }
                }
                else -> {}
            }
        }
    }
    
    fun onTitleChange(title: String) {
        _state.update { it.copy(title = title) }
    }
    
    fun onDescriptionChange(description: String) {
        _state.update { it.copy(description = description) }
    }
    
    fun onCategoryChange(category: String) {
        _state.update { it.copy(category = category) }
    }
    
    fun onNumberOfLessonsChange(numberOfLessons: String) {
        _state.update { it.copy(numberOfLessons = numberOfLessons) }
    }
    
    fun saveCourse() {
        val currentState = _state.value
        
        // Validation
        if (currentState.title.isBlank()) {
            _state.update { it.copy(error = "Title is required") }
            return
        }
        if (currentState.description.isBlank()) {
            _state.update { it.copy(error = "Description is required") }
            return
        }
        if (currentState.category.isBlank()) {
            _state.update { it.copy(error = "Category is required") }
            return
        }
        val lessonsCount = currentState.numberOfLessons.toIntOrNull()
        if (lessonsCount == null || lessonsCount <= 0) {
            _state.update { it.copy(error = "Number of lessons must be a positive number") }
            return
        }
        
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }
            
            // Calculate score: title length × number of lessons
            val score = currentState.title.length * lessonsCount
            
            val course = Course(
                id = currentState.courseId ?: 0,
                title = currentState.title,
                description = currentState.description,
                category = currentState.category,
                numberOfLessons = lessonsCount,
                score = score
            )
            
            val result = if (currentState.isEditMode) {
                courseRepository.updateCourse(course)
            } else {
                courseRepository.insertCourse(course)
            }
            
            when (result) {
                is Result.Success -> {
                    _state.update { 
                        it.copy(
                            isSaving = false,
                            savedSuccessfully = true
                        )
                    }
                }
                is Result.Error -> {
                    _state.update { 
                        it.copy(
                            isSaving = false,
                            error = result.message
                        )
                    }
                }
                else -> {}
            }
        }
    }
    
    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
