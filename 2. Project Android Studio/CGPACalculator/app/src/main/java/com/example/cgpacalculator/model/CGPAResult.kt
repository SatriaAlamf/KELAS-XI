package com.example.cgpacalculator.model

/**
 * Data class for CGPA calculation result
 * 
 * @property cgpa The calculated Cumulative Grade Point Average
 * @property totalCredits Total credit hours across all semesters
 * @property totalQualityPoints Total quality points across all semesters
 * @property totalSemesters Number of semesters included in calculation
 */
data class CGPAResult(
    val cgpa: Double = 0.0,
    val totalCredits: Double = 0.0,
    val totalQualityPoints: Double = 0.0,
    val totalSemesters: Int = 0
) {
    /**
     * Formats CGPA to 2 decimal places
     */
    fun getFormattedCGPA(): String {
        return String.format("%.2f", cgpa)
    }
    
    /**
     * Gets CGPA as a percentage (out of 100)
     */
    fun getCGPAPercentage(): Double {
        return (cgpa / 4.0) * 100
    }
    
    /**
     * Gets formatted percentage string
     */
    fun getFormattedPercentage(): String {
        return String.format("%.1f%%", getCGPAPercentage())
    }
    
    /**
     * Determines academic standing based on CGPA
     */
    fun getAcademicStanding(): String {
        return when {
            cgpa >= 3.8 -> "Summa Cum Laude"
            cgpa >= 3.5 -> "Magna Cum Laude"
            cgpa >= 3.2 -> "Cum Laude"
            cgpa >= 3.0 -> "Good Standing"
            cgpa >= 2.5 -> "Satisfactory"
            cgpa >= 2.0 -> "Probationary"
            else -> "Academic Warning"
        }
    }
}