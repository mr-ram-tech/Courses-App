# Course Management App

A complete Android course management application built with Jetpack Compose, implementing clean architecture with offline-first capabilities.

## Features

### Complete Feature Set
- **Course List Screen**
  - Display all courses with title, description, category, and score
  - Search functionality (searches title and description)
  - Category filter
  - Loading state with skeleton/spinner
  - Empty state when no courses exist
  - Pull data from local Room database (offline-first)

- **Add/Edit Course**
  - Form fields: Title, Description, Category, Number of Lessons
  - Categories fetched from MockAPI (with offline fallback)
  - Score calculation: `Title Length × Number of Lessons`
  - Form validation
  - Real-time score preview

- **Course Details Screen**
  - Display all course information in a clean layout
  - Edit button to modify course
  - Formatted creation date

- **Delete Course**
  - Confirmation dialog before deletion
  - Cascade delete from database

### Architecture

**Clean Architecture with 3 layers:**
```
presentation/     # UI Layer (Compose screens + ViewModels)
  ├── courselist/
  ├── addedit/
  ├── detail/
  └── navigation/

domain/          # Business Logic Layer
  ├── model/     # Domain models
  ├── repository/ # Repository interfaces
  └── util/      # Result wrapper

data/            # Data Layer
  ├── local/     # Room database, DAOs, DataStore
  ├── remote/    # Retrofit API service
  └── mapper/    # Entity-Domain mappers
```

**Key Patterns:**
- Repository Pattern for data abstraction
- MVVM with StateFlow for reactive UI
- Offline-first approach with Room + DataStore
- Single source of truth (database)
- Separation of concerns (UI ← ViewModel ← Repository ← Data Source)

### UI/UX

- **Material 3 Design** with dynamic color scheme
- **Responsive layouts** for different screen sizes
- **Loading indicators** during data operations
- **Empty states** with helpful messages
- **Error handling** with user-friendly messages
- **Smooth navigation** with Jetpack Navigation Compose
- **Delete confirmation** dialogs
- **Search with clear button**
- **Category filter** with visual feedback

### Offline Support

- All course data stored locally in **Room database**
- Categories cached in **DataStore Preferences**
- App works completely offline after initial category fetch
- Falls back to cached/default categories if API unavailable
- Data persists across app restarts

## Setup Instructions

### 1. Clone the Repository
```bash
git clone <repository-url>
cd CourseApp
```

2. Create `db.json`:
   ```json
   {
     "categories": [
       {"id": "1", "name": "Programming"},
       {"id": "2", "name": "Design"},
       {"id": "3", "name": "Business"},
       {"id": "4", "name": "Marketing"},
       {"id": "5", "name": "Data Science"},
       {"id": "6", "name": "Mobile Development"}
     ]
   }
   ```

### 4. Build and Run

1. Open project in Android Studio
2. Sync Gradle files
3. Run the app on emulator or device (Android 7.0+, API 24+)

## Testing the App

### Test Offline Functionality
1. Launch the app with internet connection (categories will be fetched and cached)
2. Add a few courses
3. Turn off internet/airplane mode
4. Close and reopen the app
5.App should work perfectly offline with all features

### Test Features
1. **Add Course**: Tap FAB (+) → Fill form → Save
2. **Search**: Type in search bar → Results filter instantly
3. **Filter**: Tap category chip → Select category → View filtered courses
4. **View Details**: Tap any course card → See full details
5. **Edit**: From details screen → Tap edit icon → Modify → Save
6. **Delete**: From list → Tap ⋮ → Delete → Confirm

### Test Edge Cases
- Try adding course with empty fields (validation should prevent)
- Try searching with no matches (empty state should show)
- Try filtering category with no courses (empty state should show)
- Restart app (data should persist)

## Technology Stack

- **UI**: Jetpack Compose (Material 3)
- **Architecture**: MVVM + Clean Architecture
- **Navigation**: Jetpack Navigation Compose
- **Database**: Room (SQLite)
- **Networking**: Retrofit + OkHttp
- **Local Storage**: DataStore Preferences
- **Async**: Kotlin Coroutines + Flow
- **DI**: Manual (ViewModelFactory pattern)
- **Language**: Kotlin

## Project Structure

```
com.example.courseapp/
├── CourseApplication.kt          # Application class
├── MainActivity.kt                # Entry point + Navigation setup
├── data/
│   ├── local/
│   │   ├── entity/
│   │   │   └── CourseEntity.kt   # Room entity
│   │   ├── dao/
│   │   │   └── CourseDao.kt      # Database queries
│   │   ├── AppDatabase.kt         # Room database
│   │   └── CategoryPreferences.kt # DataStore for caching
│   ├── remote/
│   │   ├── dto/
│   │   │   └── CategoryDto.kt    # API response model
│   │   ├── ApiService.kt          # Retrofit interface
│   │   └── RetrofitClient.kt      # Retrofit instance
│   └── mapper/
│       └── CourseMapper.kt        # Entity ↔ Domain mappers
├── domain/
│   ├── model/
│   │   └── Course.kt              # Domain model
│   ├── repository/
│   │   ├── CourseRepository.kt
│   │   └── CategoryRepository.kt
│   └── util/
│       └── Result.kt              # Result wrapper
└── presentation/
    ├── courselist/
    │   ├── CourseListScreen.kt
    │   └── CourseListViewModel.kt
    ├── addedit/
    │   ├── AddEditCourseScreen.kt
    │   └── AddEditCourseViewModel.kt
    ├── detail/
    │   ├── CourseDetailScreen.kt
    │   └── CourseDetailViewModel.kt
    └── navigation/
        └── Screen.kt              # Navigation routes
```

## API Endpoint

The app expects a single endpoint:

**GET** `/categories`

Response format:
```json
[
  {
    "id": "1",
    "name": "Programming"
  },
  {
    "id": "2",
    "name": "Design"
  }
]
```

## Score Calculation

The course score is calculated using the formula:
```
Score = (Length of Course Title) × (Number of Lessons)
```

Example:
- Title: "Advanced Kotlin" (15 characters)
- Lessons: 20
- Score: 15 × 20 = **300**

## Requirements Met

 Course list with search and category filter  
 Loading state on app start  
 Empty state when no courses  
 Add course with all required fields  
 Edit course functionality  
 Delete with confirmation dialog  
 Course details screen  
 Score calculation and display  
 Offline support (Room + DataStore)  
 Data persists after restart  
 Mock API integration with offline fallback  
 Clean architecture (3 layers)  
 No business logic in UI  
 Repository pattern for data  
 Proper error handling  
 Clean, professional UI  
 Smooth navigation  
 Responsive layouts  

## Future Enhancements

- Add course sorting options
- Implement course progress tracking
- Add course images
- Export/Import courses
- Dark/Light theme toggle
- Add animations and transitions
- Unit and UI tests
- Dependency Injection with Hilt/Koin

## License

This project is created for educational/assignment purposes.
