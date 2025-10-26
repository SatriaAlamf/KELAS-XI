package com.example.cgpacalculator.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

/**
 * Composable for inputting course information
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseInputSection(
    courseName: String,
    onCourseNameChange: (String) -> Unit,
    selectedGrade: String,
    onGradeChange: (String) -> Unit,
    creditHours: String,
    onCreditHoursChange: (String) -> Unit,
    availableGrades: List<String>,
    onAddCourse: () -> Unit,
    isValidInput: Boolean,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Add Course",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
            
            // Course Name Input
            OutlinedTextField(
                value = courseName,
                onValueChange = onCourseNameChange,
                label = { Text("Course Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            // Grade Selection Dropdown
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedGrade,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Grade") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    availableGrades.forEach { grade ->
                        DropdownMenuItem(
                            text = { Text(grade) },
                            onClick = {
                                onGradeChange(grade)
                                expanded = false
                            }
                        )
                    }
                }
            }
            
            // Credit Hours Input
            OutlinedTextField(
                value = creditHours,
                onValueChange = onCreditHoursChange,
                label = { Text("Credit Hours") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            // Add Course Button
            Button(
                onClick = onAddCourse,
                enabled = isValidInput,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Course")
            }
        }
    }
}