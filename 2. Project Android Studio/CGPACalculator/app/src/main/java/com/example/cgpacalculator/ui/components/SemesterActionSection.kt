package com.example.cgpacalculator.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Composable for semester management actions
 */
@Composable
fun SemesterActionSection(
    semesterName: String,
    onSemesterNameChange: (String) -> Unit,
    onSaveSemester: () -> Unit,
    onResetAll: () -> Unit,
    isSemesterValid: Boolean,
    hasAnySemesters: Boolean,
    modifier: Modifier = Modifier
) {
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
                text = "Semester Management",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
            
            // Semester Name Input
            OutlinedTextField(
                value = semesterName,
                onValueChange = onSemesterNameChange,
                label = { Text("Semester Name") },
                placeholder = { Text("e.g., Fall 2023, Semester 1") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Save Semester Button
                Button(
                    onClick = onSaveSemester,
                    enabled = isSemesterValid,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Save Semester")
                }
                
                // Reset All Button
                OutlinedButton(
                    onClick = onResetAll,
                    enabled = hasAnySemesters,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Reset All")
                }
            }
        }
    }
}