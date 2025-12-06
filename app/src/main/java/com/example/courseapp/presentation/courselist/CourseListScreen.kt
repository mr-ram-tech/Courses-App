package com.example.courseapp.presentation.courselist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.courseapp.domain.model.Course

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseListScreen(
    state: CourseListState,
    categories: List<String>,
    onSearchQueryChange: (String) -> Unit,
    onCategoryFilterChange: (String?) -> Unit,
    onCourseClick: (Int) -> Unit,
    onAddCourseClick: () -> Unit,
    onDeleteCourse: (Int) -> Unit
) {
    var showCategoryFilter by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf<Int?>(null) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Courses") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddCourseClick,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Course")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search and Filter
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Search courses...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (state.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true
                )
                
                FilterChip(
                    selected = state.selectedCategory != null,
                    onClick = { showCategoryFilter = true },
                    label = { 
                        Text(
                            state.selectedCategory ?: "Category",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        ) 
                    },
                    leadingIcon = {
                        Icon(
                            if (state.selectedCategory != null) Icons.Default.Done else Icons.Default.Menu,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )
            }
            
            // Content
            when {
                state.isLoading -> {
                    LoadingView()
                }
                state.error != null -> {
                    ErrorView(message = state.error)
                }
                state.courses.isEmpty() -> {
                    EmptyStateView(
                        searchQuery = state.searchQuery,
                        selectedCategory = state.selectedCategory
                    )
                }
                else -> {
                    CourseList(
                        courses = state.courses,
                        onCourseClick = onCourseClick,
                        onDeleteClick = { showDeleteDialog = it }
                    )
                }
            }
        }
    }
    
    // Category Filter Dialog
    if (showCategoryFilter) {
        AlertDialog(
            onDismissRequest = { showCategoryFilter = false },
            title = { Text("Filter by Category") },
            text = {
                LazyColumn {
                    item {
                        FilterOption(
                            text = "All Categories",
                            isSelected = state.selectedCategory == null,
                            onClick = {
                                onCategoryFilterChange(null)
                                showCategoryFilter = false
                            }
                        )
                    }
                    items(categories) { category ->
                        FilterOption(
                            text = category,
                            isSelected = state.selectedCategory == category,
                            onClick = {
                                onCategoryFilterChange(category)
                                showCategoryFilter = false
                            }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCategoryFilter = false }) {
                    Text("Close")
                }
            }
        )
    }
    
    // Delete Confirmation Dialog
    showDeleteDialog?.let { courseId ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Delete Course") },
            text = { Text("Are you sure you want to delete this course? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteCourse(courseId)
                        showDeleteDialog = null
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun LoadingView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator()
            Text(
                text = "Loading courses...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun EmptyStateView(searchQuery: String, selectedCategory: String?) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.outline
            )
            Text(
                text = when {
                    searchQuery.isNotEmpty() -> "No courses found"
                    selectedCategory != null -> "No courses in this category"
                    else -> "No courses yet"
                },
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = when {
                    searchQuery.isNotEmpty() -> "Try a different search term"
                    selectedCategory != null -> "Try selecting a different category"
                    else -> "Tap the + button to add your first course"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
private fun ErrorView(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Text(
                text = "Error",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CourseList(
    courses: List<Course>,
    onCourseClick: (Int) -> Unit,
    onDeleteClick: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(courses, key = { it.id }) { course ->
            CourseCard(
                course = course,
                onClick = { onCourseClick(course.id) },
                onDeleteClick = { onDeleteClick(course.id) }
            )
        }
    }
}

@Composable
private fun CourseCard(
    course: Course,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = course.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = course.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Delete") },
                            onClick = {
                                showMenu = false
                                onDeleteClick()
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SuggestionChip(
                    onClick = {},
                    label = { Text(course.category, style = MaterialTheme.typography.labelSmall) }
                )
                SuggestionChip(
                    onClick = {},
                    label = { 
                        Text(
                            "${course.numberOfLessons} lessons",
                            style = MaterialTheme.typography.labelSmall
                        ) 
                    }
                )
                Spacer(modifier = Modifier.weight(1f))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = "Score: ${course.score}",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterOption(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(
                if (isSelected) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surface
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge
        )
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}
