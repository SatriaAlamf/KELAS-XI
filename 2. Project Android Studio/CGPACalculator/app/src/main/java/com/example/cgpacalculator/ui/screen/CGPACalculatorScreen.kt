package com.example.cgpacalculator.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cgpacalculator.ui.components.*
import com.example.cgpacalculator.viewmodel.CGPAViewModel

/**
 * Main screen for CGPA Calculator application
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CGPACalculatorScreen(
    viewModel: CGPAViewModel = viewModel()
) {
    val courseName by viewModel.courseName
    val selectedGrade by viewModel.selectedGrade
    val creditHours by viewModel.creditHours
    val semesterName by viewModel.semesterName
    val currentCourses by viewModel.currentCourses
    val semesters by viewModel.semesters
    val cgpaResult by viewModel.cgpaResult
    
    val scrollState = rememberScrollState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "CGPA Calculator",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Calculate your Cumulative GPA",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            
            // CGPA Result Section (Top Priority)
            CGPAResultSection(
                cgpaResult = cgpaResult
            )
            
            // Course Input Section
            CourseInputSection(
                courseName = courseName,
                onCourseNameChange = viewModel::updateCourseName,
                selectedGrade = selectedGrade,
                onGradeChange = viewModel::updateSelectedGrade,
                creditHours = creditHours,
                onCreditHoursChange = viewModel::updateCreditHours,
                availableGrades = viewModel.availableGrades,
                onAddCourse = viewModel::addCourse,
                isValidInput = viewModel.isCurrentCourseValid()
            )
            
            // Current Courses List
            CourseListSection(
                courses = currentCourses,
                onRemoveCourse = viewModel::removeCourse
            )
            
            // Semester Management
            SemesterActionSection(
                semesterName = semesterName,
                onSemesterNameChange = viewModel::updateSemesterName,
                onSaveSemester = viewModel::saveSemester,
                onResetAll = {
                    viewModel.resetAllData()
                },
                isSemesterValid = viewModel.isCurrentSemesterValid(),
                hasAnySemesters = semesters.isNotEmpty() || currentCourses.isNotEmpty()
            )
            
            // Semester History
            SemesterHistorySection(
                semesters = semesters,
                onRemoveSemester = viewModel::removeSemester
            )
            
            // Instructions/Help Section
            if (semesters.isEmpty() && currentCourses.isEmpty()) {
                InstructionsSection()
            }
            
            // Add some bottom spacing
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Instructions section for new users
 */
@Composable
private fun InstructionsSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "How to Use",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            val instructions = listOf(
                "1. Enter course name, select grade, and input credit hours",
                "2. Click 'Add Course' to add it to current semester",
                "3. Enter semester name (e.g., 'Fall 2023')",
                "4. Click 'Save Semester' to finalize the semester",
                "5. Repeat for all semesters to calculate overall CGPA"
            )
            
            instructions.forEach { instruction ->
                Text(
                    text = instruction,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    textAlign = TextAlign.Start
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Grade Scale: A+ (4.0), A (4.0), A- (3.7), B+ (3.3), B (3.0), B- (2.7), C+ (2.3), C (2.0), C- (1.7), D+ (1.3), D (1.0), F (0.0)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}