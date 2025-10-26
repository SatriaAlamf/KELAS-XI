# CGPA Calculator

A comprehensive Android application built with Jetpack Compose for calculating Cumulative Grade Point Average (CGPA) for students.

## 🎯 Features

- **Grade Input**: Easy input for course name, grade (A+ to F), and credit hours
- **Real-time CGPA Calculation**: Automatic calculation of overall CGPA
- **Semester Management**: Add, save, and manage multiple semesters
- **Academic Standing**: Display academic status based on CGPA
- **Course Management**: Add/remove courses within semesters
- **Statistics**: View total credits, semesters, and percentage
- **Modern UI**: Built with Jetpack Compose and Material Design 3

## 🛠️ Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM (Model-View-ViewModel)
- **Design System**: Material Design 3
- **State Management**: Compose State with remember/mutableStateOf

## 📱 Screenshots

### Main Interface
- Course input section with grade dropdown
- Current semester course list
- CGPA result display with academic standing
- Semester history with expandable details

## 🚀 Getting Started

### Prerequisites
- Android Studio Electric Eel or newer
- Android SDK 26 or higher
- Kotlin 2.0.21

### Installation

1. Clone the repository:
```bash
git clone https://github.com/yourusername/CGPACalculator.git
```

2. Open the project in Android Studio

3. Build and run the project on an emulator or physical device

## 📋 How to Use

1. **Add Courses**:
   - Enter course name
   - Select grade from dropdown (A+ to F)
   - Input credit hours
   - Click "Add Course"

2. **Manage Semester**:
   - Enter semester name (e.g., "Fall 2023")
   - Add all courses for the semester
   - Click "Save Semester"

3. **View Results**:
   - CGPA automatically calculated and displayed
   - View academic standing
   - Check semester history

4. **Reset Data**:
   - Use "Reset All" to clear all data

## 📊 Grade Scale

| Grade | Grade Point |
|-------|-------------|
| A+, A | 4.0 |
| A-    | 3.7 |
| B+    | 3.3 |
| B     | 3.0 |
| B-    | 2.7 |
| C+    | 2.3 |
| C     | 2.0 |
| C-    | 1.7 |
| D+    | 1.3 |
| D     | 1.0 |
| F     | 0.0 |

## 🏆 Academic Standing

- **Summa Cum Laude**: CGPA ≥ 3.8
- **Magna Cum Laude**: CGPA ≥ 3.5
- **Cum Laude**: CGPA ≥ 3.2
- **Good Standing**: CGPA ≥ 3.0
- **Satisfactory**: CGPA ≥ 2.5
- **Probationary**: CGPA ≥ 2.0
- **Academic Warning**: CGPA < 2.0

## 🔧 Project Structure

```
app/src/main/java/com/example/cgpacalculator/
├── model/                  # Data models
│   ├── Course.kt
│   ├── Semester.kt
│   └── CGPAResult.kt
├── viewmodel/             # State management
│   └── CGPAViewModel.kt
├── ui/
│   ├── components/        # Reusable UI components
│   │   ├── CourseInputSection.kt
│   │   ├── CourseListSection.kt
│   │   ├── CGPAResultSection.kt
│   │   ├── SemesterActionSection.kt
│   │   └── SemesterHistorySection.kt
│   ├── screen/           # Main screens
│   │   └── CGPACalculatorScreen.kt
│   └── theme/            # App theming
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
└── MainActivity.kt       # Entry point
```

## 🤝 Contributing

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Developer

Developed by **Satria Alam Fajar**

## 📞 Support

If you have any questions or need support, please create an issue in the GitHub repository.

---

**Note**: This application is designed for educational purposes and uses a standard 4.0 GPA scale. Different institutions may use different grading systems.