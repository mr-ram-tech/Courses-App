package com.example.courseapp
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.courseapp.data.local.AppDatabase
import com.example.courseapp.data.local.CategoryPreferences
import com.example.courseapp.data.remote.RetrofitClient
import com.example.courseapp.domain.repository.CategoryRepository
import com.example.courseapp.domain.repository.CourseRepository
import com.example.courseapp.domain.util.Result
import com.example.courseapp.presentation.addedit.AddEditCourseScreen
import com.example.courseapp.presentation.addedit.AddEditCourseViewModel
import com.example.courseapp.presentation.courselist.CourseListScreen
import com.example.courseapp.presentation.courselist.CourseListViewModel
import com.example.courseapp.presentation.detail.CourseDetailScreen
import com.example.courseapp.presentation.detail.CourseDetailViewModel
import com.example.courseapp.presentation.navigation.Screen
import com.example.courseapp.ui.theme.CourseAppTheme
import kotlinx.coroutines.launch
// ViewModel Factories
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class MainActivity : ComponentActivity() {
    
    private val database by lazy { AppDatabase.getDatabase(this) }
    private val courseRepository by lazy { CourseRepository(database.courseDao()) }
    private val categoryRepository by lazy { 
        CategoryRepository(
            RetrofitClient.api,
            CategoryPreferences(this)
        )
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CourseAppTheme {
                CourseApp(
                    courseRepository = courseRepository,
                    categoryRepository = categoryRepository
                )
            }
        }
    }
}

@Composable
fun CourseApp(
    courseRepository: CourseRepository,
    categoryRepository: CategoryRepository
) {
    val navController = rememberNavController()
    val categories = remember { mutableStateOf<List<String>>(emptyList()) }
    val scope = rememberCoroutineScope()
    
    // Load categories once
    LaunchedEffect(Unit) {
        scope.launch {
            when (val result = categoryRepository.getCategories()) {
                is Result.Success -> categories.value = result.data
                else -> {}
            }
        }
    }
    
    NavHost(
        navController = navController,
        startDestination = Screen.CourseList.route
    ) {
        // Course List Screen
        composable(Screen.CourseList.route) {
            val viewModel: CourseListViewModel = viewModel(
                factory = CourseListViewModelFactory(courseRepository)
            )
            val state by viewModel.state.collectAsState()
            
            CourseListScreen(
                state = state,
                categories = categories.value,
                onSearchQueryChange = viewModel::onSearchQueryChange,
                onCategoryFilterChange = viewModel::onCategoryFilterChange,
                onCourseClick = { courseId ->
                    navController.navigate(Screen.CourseDetail.createRoute(courseId))
                },
                onAddCourseClick = {
                    navController.navigate(Screen.AddCourse.route)
                },
                onDeleteCourse = viewModel::deleteCourse
            )
        }
        
        // Add Course Screen
        composable(Screen.AddCourse.route) {
            val viewModel: AddEditCourseViewModel = viewModel(
                factory = AddEditCourseViewModelFactory(courseRepository, categoryRepository)
            )
            val state by viewModel.state.collectAsState()
            
            AddEditCourseScreen(
                state = state,
                onTitleChange = viewModel::onTitleChange,
                onDescriptionChange = viewModel::onDescriptionChange,
                onCategoryChange = viewModel::onCategoryChange,
                onNumberOfLessonsChange = viewModel::onNumberOfLessonsChange,
                onSaveClick = viewModel::saveCourse,
                onBackClick = { navController.popBackStack() },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Edit Course Screen
        composable(
            route = Screen.EditCourse.route,
            arguments = listOf(
                navArgument("courseId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getInt("courseId") ?: return@composable
            val viewModel: AddEditCourseViewModel = viewModel(
                factory = AddEditCourseViewModelFactory(courseRepository, categoryRepository)
            )
            val state by viewModel.state.collectAsState()
            
            LaunchedEffect(courseId) {
                viewModel.loadCourse(courseId)
            }
            
            AddEditCourseScreen(
                state = state,
                onTitleChange = viewModel::onTitleChange,
                onDescriptionChange = viewModel::onDescriptionChange,
                onCategoryChange = viewModel::onCategoryChange,
                onNumberOfLessonsChange = viewModel::onNumberOfLessonsChange,
                onSaveClick = viewModel::saveCourse,
                onBackClick = { navController.popBackStack() },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Course Detail Screen
        composable(
            route = Screen.CourseDetail.route,
            arguments = listOf(
                navArgument("courseId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getInt("courseId") ?: return@composable
            val viewModel: CourseDetailViewModel = viewModel(
                factory = CourseDetailViewModelFactory(courseRepository)
            )
            val state by viewModel.state.collectAsState()
            
            LaunchedEffect(courseId) {
                viewModel.loadCourse(courseId)
            }
            
            CourseDetailScreen(
                state = state,
                onBackClick = { navController.popBackStack() },
                onEditClick = {
                    navController.navigate(Screen.EditCourse.createRoute(courseId))
                }
            )
        }
    }
}


class CourseListViewModelFactory(
    private val courseRepository: CourseRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CourseListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CourseListViewModel(courseRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class AddEditCourseViewModelFactory(
    private val courseRepository: CourseRepository,
    private val categoryRepository: CategoryRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddEditCourseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddEditCourseViewModel(courseRepository, categoryRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class CourseDetailViewModelFactory(
    private val courseRepository: CourseRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CourseDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CourseDetailViewModel(courseRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
