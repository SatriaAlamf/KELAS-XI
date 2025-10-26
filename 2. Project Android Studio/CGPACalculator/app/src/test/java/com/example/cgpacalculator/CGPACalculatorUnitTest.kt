package com.example.cgpacalculator

import com.example.cgpacalculator.model.Course
import com.example.cgpacalculator.model.Semester
import com.example.cgpacalculator.model.CGPAResult
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for CGPA Calculator models and calculations
 */
class CGPACalculatorUnitTest {

    @Test
    fun course_gradePointConversion_isCorrect() {
        // Test A+ grade
        val courseAPlus = Course("1", "Math", "A+", 3.0)
        assertEquals(4.0, courseAPlus.getGradePoint(), 0.01)
        
        // Test B grade
        val courseB = Course("2", "Physics", "B", 4.0)
        assertEquals(3.0, courseB.getGradePoint(), 0.01)
        
        // Test F grade
        val courseF = Course("3", "Chemistry", "F", 2.0)
        assertEquals(0.0, courseF.getGradePoint(), 0.01)
    }

    @Test
    fun course_qualityPointsCalculation_isCorrect() {
        val course = Course("1", "Math", "A", 3.0)
        val expectedQualityPoints = 4.0 * 3.0 // Grade point * Credits
        assertEquals(expectedQualityPoints, course.getQualityPoints(), 0.01)
    }

    @Test
    fun course_validation_worksCorrectly() {
        // Valid course
        val validCourse = Course("1", "Math", "A", 3.0)
        assertTrue(validCourse.isValid())
        
        // Invalid course - empty name
        val invalidCourse1 = Course("2", "", "A", 3.0)
        assertFalse(invalidCourse1.isValid())
        
        // Invalid course - empty grade
        val invalidCourse2 = Course("3", "Physics", "", 3.0)
        assertFalse(invalidCourse2.isValid())
        
        // Invalid course - zero credits
        val invalidCourse3 = Course("4", "Chemistry", "B", 0.0)
        assertFalse(invalidCourse3.isValid())
    }

    @Test
    fun semester_gpaCalculation_isCorrect() {
        val courses = listOf(
            Course("1", "Math", "A", 3.0),      // 4.0 * 3.0 = 12.0
            Course("2", "Physics", "B", 4.0),   // 3.0 * 4.0 = 12.0
            Course("3", "Chemistry", "A-", 3.0) // 3.7 * 3.0 = 11.1
        )
        
        val semester = Semester("1", "Fall 2023", courses)
        
        val expectedTotalCredits = 10.0
        val expectedTotalQualityPoints = 35.1
        val expectedGPA = expectedTotalQualityPoints / expectedTotalCredits
        
        assertEquals(expectedTotalCredits, semester.getTotalCredits(), 0.01)
        assertEquals(expectedTotalQualityPoints, semester.getTotalQualityPoints(), 0.01)
        assertEquals(expectedGPA, semester.getSemesterGPA(), 0.01)
    }

    @Test
    fun cgpaResult_academicStanding_isCorrect() {
        // Test Summa Cum Laude
        val summaCumLaude = CGPAResult(cgpa = 3.9, totalCredits = 120.0, totalQualityPoints = 468.0, totalSemesters = 8)
        assertEquals("Summa Cum Laude", summaCumLaude.getAcademicStanding())
        
        // Test Good Standing
        val goodStanding = CGPAResult(cgpa = 3.1, totalCredits = 60.0, totalQualityPoints = 186.0, totalSemesters = 4)
        assertEquals("Good Standing", goodStanding.getAcademicStanding())
        
        // Test Academic Warning
        val academicWarning = CGPAResult(cgpa = 1.8, totalCredits = 30.0, totalQualityPoints = 54.0, totalSemesters = 2)
        assertEquals("Academic Warning", academicWarning.getAcademicStanding())
    }

    @Test
    fun cgpaResult_percentageCalculation_isCorrect() {
        val cgpaResult = CGPAResult(cgpa = 3.2, totalCredits = 90.0, totalQualityPoints = 288.0, totalSemesters = 6)
        val expectedPercentage = (3.2 / 4.0) * 100
        assertEquals(expectedPercentage, cgpaResult.getCGPAPercentage(), 0.01)
        assertEquals("80.0%", cgpaResult.getFormattedPercentage())
    }

    @Test
    fun multiSemester_cgpaCalculation_isCorrect() {
        // Semester 1
        val semester1Courses = listOf(
            Course("1", "Math 1", "A", 3.0),
            Course("2", "Physics 1", "B+", 4.0)
        )
        val semester1 = Semester("1", "Fall 2023", semester1Courses)
        
        // Semester 2
        val semester2Courses = listOf(
            Course("3", "Math 2", "A-", 3.0),
            Course("4", "Physics 2", "B", 3.0)
        )
        val semester2 = Semester("2", "Spring 2024", semester2Courses)
        
        val semesters = listOf(semester1, semester2)
        
        val totalCredits = semesters.sumOf { it.getTotalCredits() }
        val totalQualityPoints = semesters.sumOf { it.getTotalQualityPoints() }
        val overallCGPA = totalQualityPoints / totalCredits
        
        // Expected calculation:
        // Semester 1: (4.0*3 + 3.3*4) = 12 + 13.2 = 25.2, Credits: 7
        // Semester 2: (3.7*3 + 3.0*3) = 11.1 + 9 = 20.1, Credits: 6
        // Total: 25.2 + 20.1 = 45.3, Total Credits: 13
        // CGPA: 45.3 / 13 = 3.485
        
        assertEquals(13.0, totalCredits, 0.01)
        assertEquals(45.3, totalQualityPoints, 0.01)
        assertEquals(3.485, overallCGPA, 0.01)
    }
}