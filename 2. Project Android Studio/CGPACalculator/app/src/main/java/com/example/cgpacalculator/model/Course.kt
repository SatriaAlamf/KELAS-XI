package com.example.cgpacalculator.model

/**
 * Data class representing a course with its grade and credit hours
 * 
 * @property courseName Name of the course
 * @property grade Letter grade (A+, A, A-, B+, B, B-, C+, C, C-, D+, D, F)
 * @property credit Credit hours for the course
 */
data class Course(
    val id: String = "",
    val courseName: String = "",
    val grade: String = "",
    val credit: Double = 0.0
) {
    /**
     * Converts letter grade to grade point based on standard 4.0 scale
     */
    fun getGradePoint(): Double {
        return when (grade.uppercase()) {
            "A+", "A" -> 4.0
            "A-" -> 3.7
            "B+" -> 3.3
            "B" -> 3.0
            "B-" -> 2.7
            "C+" -> 2.3
            "C" -> 2.0
            "C-" -> 1.7
            "D+" -> 1.3
            "D" -> 1.0
            "F" -> 0.0
            else -> 0.0
        }
    }
    
    /**
     * Calculates quality points (Grade Point × Credit Hours)
     */
    fun getQualityPoints(): Double {
        return getGradePoint() * credit
    }
    
    /**
     * Validates if the course data is complete and valid
     */
    fun isValid(): Boolean {
        return courseName.isNotBlank() && 
               grade.isNotBlank() && 
               credit > 0.0 && 
               getGradePoint() >= 0.0
    }
}