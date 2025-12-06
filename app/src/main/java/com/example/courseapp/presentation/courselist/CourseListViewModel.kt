package com.example.courseapp.presentation.courselist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.courseapp.domain.model.Course
import com.example.courseapp.domain.repository.CourseRepository
import com.example.courseapp.domain.util.Result
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CourseListState(
    val courses: List<Course> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val searchQuery: String = "",
    val selectedCategory: String? = null
)

class CourseListViewModel(
    private val courseRepository: CourseRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(CourseListState())
    val state: StateFlow<CourseListState> = _state.asStateFlow()
    
    init {
        loadCourses()
    }
    
    private fun loadCourses() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            try {
                val searchQuery = _state.value.searchQuery
                val selectedCategory = _state.value.selectedCategory
                
                val coursesFlow = when {
                    searchQuery.isNotEmpty() -> courseRepository.searchCourses(searchQuery)
                    selectedCategory != null -> courseRepository.getCoursesByCategory(selectedCategory)
                    else -> courseRepository.getAllCourses()
                }
                
                coursesFlow.collect { courses ->
                    _state.update { 
                        it.copy(
                            courses = courses,
                            isLoading = false,
                            error = null
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "An error occurred"
                    )
                }
            }
        }
    }
    
    fun onSearchQueryChange(query: String) {
        _state.update { it.copy(searchQuery = query) }
        loadCourses()
    }
    
    fun onCategoryFilterChange(category: String?) {
        _state.update { it.copy(selectedCategory = category) }
        loadCourses()
    }
    
    fun deleteCourse(courseId: Int) {
        viewModelScope.launch {
            when (courseRepository.deleteCourseById(courseId)) {
                is Result.Success -> {
                    // Course deleted, flow will update automatically
                }
                is Result.Error -> {
                    _state.update { it.copy(error = "Failed to delete course") }
                }
                else -> {}
            }
        }
    }
}
