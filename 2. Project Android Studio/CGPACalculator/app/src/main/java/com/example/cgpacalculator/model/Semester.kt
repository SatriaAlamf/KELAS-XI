package com.example.cgpacalculator.model

/**
 * Data class representing a semester with its courses and calculated statistics
 * 
 * @property semesterName Name/identifier of the semester (e.g., "Fall 2023", "Semester 1")
 * @property courses List of courses taken in this semester
 */
data class Semester(
    val id: String = "",
    val semesterName: String = "",
    val courses: List<Course> = emptyList()
) {
    /**
     * Calculates the GPA for this semester
     */
    fun getSemesterGPA(): Double {
        val validCourses = courses.filter { it.isValid() }
        if (validCourses.isEmpty()) return 0.0
        
        val totalQualityPoints = validCourses.sumOf { it.getQualityPoints() }
        val totalCredits = validCourses.sumOf { it.credit }
        
        return if (totalCredits > 0) totalQualityPoints / totalCredits else 0.0
    }
    
    /**
     * Gets total credit hours for this semester
     */
    fun getTotalCredits(): Double {
        return courses.filter { it.isValid() }.sumOf { it.credit }
    }
    
    /**
     * Gets total quality points for this semester
     */
    fun getTotalQualityPoints(): Double {
        return courses.filter { it.isValid() }.sumOf { it.getQualityPoints() }
    }
    
    /**
     * Validates if the semester has valid data
     */
    fun isValid(): Boolean {
        return semesterName.isNotBlank() && courses.any { it.isValid() }
    }
}