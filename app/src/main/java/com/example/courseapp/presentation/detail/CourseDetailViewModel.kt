package com.example.courseapp.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.courseapp.domain.model.Course
import com.example.courseapp.domain.repository.CourseRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CourseDetailState(
    val course: Course? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

class CourseDetailViewModel(
    private val courseRepository: CourseRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(CourseDetailState())
    val state: StateFlow<CourseDetailState> = _state.asStateFlow()
    
    fun loadCourse(courseId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            try {
                val course = courseRepository.getCourseById(courseId)
                _state.update { 
                    it.copy(
                        course = course,
                        isLoading = false,
                        error = if (course == null) "Course not found" else null
                    )
                }
            } catch (e: Exception) {
                _state.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load course"
                    )
                }
            }
        }
    }
}
