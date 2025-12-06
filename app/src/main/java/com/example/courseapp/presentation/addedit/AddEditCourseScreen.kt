package com.example.courseapp.presentation.addedit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditCourseScreen(
    state: AddEditCourseState,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onNumberOfLessonsChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onBackClick: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var showCategoryDialog by remember { mutableStateOf(false) }
    
    // Navigate back when save is successful
    LaunchedEffect(state.savedSuccessfully) {
        if (state.savedSuccessfully) {
            onNavigateBack()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.isEditMode) "Edit Course" else "Add Course") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = onSaveClick,
                        enabled = !state.isSaving
                    ) {
                        if (state.isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.Check, contentDescription = "Save")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title Field
            OutlinedTextField(
                value = state.title,
                onValueChange = onTitleChange,
                label = { Text("Course Title *") },
                placeholder = { Text("e.g., Advanced Kotlin Programming") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !state.isSaving
            )
            
            // Description Field
            OutlinedTextField(
                value = state.description,
                onValueChange = onDescriptionChange,
                label = { Text("Description *") },
                placeholder = { Text("Describe what this course covers...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp),
                minLines = 4,
                maxLines = 8,
                enabled = !state.isSaving
            )
            
            // Category Field
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = state.category,
                    onValueChange = {},
                    label = { Text("Category *") },
                    placeholder = { Text("Select a category") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        if (state.isLoadingCategories) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            // Add a dropdown arrow icon to indicate it's selectable
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Select Category"
                            )
                        }
                    }
                )
                
                // Transparent clickable overlay
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable(
                            onClick = {
                                if (!state.isSaving && !state.isLoadingCategories) {
                                    showCategoryDialog = true
                                }
                            }
                        )
                )
            }
            
            // Number of Lessons Field
            OutlinedTextField(
                value = state.numberOfLessons,
                onValueChange = onNumberOfLessonsChange,
                label = { Text("Number of Lessons *") },
                placeholder = { Text("e.g., 10") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                enabled = !state.isSaving
            )
            
            // Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Score Calculation",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = "Score = Title Length × Number of Lessons",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    
                    val lessons = state.numberOfLessons.toIntOrNull() ?: 0
                    val calculatedScore = state.title.length * lessons
                    
                    if (state.title.isNotEmpty() && lessons > 0) {
                        Divider(modifier = Modifier.padding(vertical = 4.dp))
                        Text(
                            text = "Current score: ${state.title.length} × $lessons = $calculatedScore",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            
            // Save Button (Alternative to top bar)
            Button(
                onClick = onSaveClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isSaving
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(if (state.isSaving) "Saving..." else "Save Course")
            }
        }
    }
    
    // Category Selection Dialog
    if (showCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showCategoryDialog = false },
            title = { Text("Select Category") },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    state.categories.forEach { category ->
                        TextButton(
                            onClick = {
                                onCategoryChange(category)
                                showCategoryDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = category,
                                modifier = Modifier.fillMaxWidth(),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCategoryDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Error Snackbar
    state.error?.let { error ->
        LaunchedEffect(error) {
            kotlinx.coroutines.delay(3000)
            // Error will be cleared by the delay
        }
        
        Snackbar(
            modifier = Modifier.padding(16.dp),
            action = {
                TextButton(onClick = { /* Error auto-clears */ }) {
                    Text("OK")
                }
            }
        ) {
            Text(error)
        }
    }
}
