package com.example.cgpacalculator.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.cgpacalculator.model.Course
import com.example.cgpacalculator.model.Semester
import com.example.cgpacalculator.model.CGPAResult
import java.util.UUID

/**
 * ViewModel for managing CGPA Calculator state and business logic
 */
class CGPAViewModel : ViewModel() {
    
    // Current input states
    private val _courseName = mutableStateOf("")
    val courseName: State<String> = _courseName
    
    private val _selectedGrade = mutableStateOf("")
    val selectedGrade: State<String> = _selectedGrade
    
    private val _creditHours = mutableStateOf("")
    val creditHours: State<String> = _creditHours
    
    private val _semesterName = mutableStateOf("")
    val semesterName: State<String> = _semesterName
    
    // Current semester courses
    private val _currentCourses = mutableStateOf<List<Course>>(emptyList())
    val currentCourses: State<List<Course>> = _currentCourses
    
    // All semesters
    private val _semesters = mutableStateOf<List<Semester>>(emptyList())
    val semesters: State<List<Semester>> = _semesters
    
    // CGPA Result
    private val _cgpaResult = mutableStateOf(CGPAResult())
    val cgpaResult: State<CGPAResult> = _cgpaResult
    
    // Available grades
    val availableGrades = listOf("A+", "A", "A-", "B+", "B", "B-", "C+", "C", "C-", "D+", "D", "F")
    
    /**
     * Updates course name input
     */
    fun updateCourseName(name: String) {
        _courseName.value = name
    }
    
    /**
     * Updates selected grade
     */
    fun updateSelectedGrade(grade: String) {
        _selectedGrade.value = grade
    }
    
    /**
     * Updates credit hours input
     */
    fun updateCreditHours(credits: String) {
        _creditHours.value = credits
    }
    
    /**
     * Updates semester name
     */
    fun updateSemesterName(name: String) {
        _semesterName.value = name
    }
    
    /**
     * Adds a new course to current semester
     */
    fun addCourse() {
        val creditValue = _creditHours.value.toDoubleOrNull()
        
        if (_courseName.value.isNotBlank() && 
            _selectedGrade.value.isNotBlank() && 
            creditValue != null && creditValue > 0) {
            
            val newCourse = Course(
                id = UUID.randomUUID().toString(),
                courseName = _courseName.value,
                grade = _selectedGrade.value,
                credit = creditValue
            )
            
            _currentCourses.value = _currentCourses.value + newCourse
            
            // Clear inputs
            _courseName.value = ""
            _selectedGrade.value = ""
            _creditHours.value = ""
        }
    }
    
    /**
     * Removes a course from current semester
     */
    fun removeCourse(courseId: String) {
        _currentCourses.value = _currentCourses.value.filter { it.id != courseId }
    }
    
    /**
     * Saves current semester and clears current courses
     */
    fun saveSemester() {
        if (_semesterName.value.isNotBlank() && _currentCourses.value.isNotEmpty()) {
            val newSemester = Semester(
                id = UUID.randomUUID().toString(),
                semesterName = _semesterName.value,
                courses = _currentCourses.value
            )
            
            _semesters.value = _semesters.value + newSemester
            
            // Clear current semester data
            _semesterName.value = ""
            _currentCourses.value = emptyList()
            
            // Recalculate CGPA
            calculateCGPA()
        }
    }
    
    /**
     * Removes a semester
     */
    fun removeSemester(semesterId: String) {
        _semesters.value = _semesters.value.filter { it.id != semesterId }
        calculateCGPA()
    }
    
    /**
     * Calculates overall CGPA from all semesters
     */
    fun calculateCGPA() {
        val validSemesters = _semesters.value.filter { it.isValid() }
        
        if (validSemesters.isEmpty()) {
            _cgpaResult.value = CGPAResult()
            return
        }
        
        val totalQualityPoints = validSemesters.sumOf { it.getTotalQualityPoints() }
        val totalCredits = validSemesters.sumOf { it.getTotalCredits() }
        
        val cgpa = if (totalCredits > 0) totalQualityPoints / totalCredits else 0.0
        
        _cgpaResult.value = CGPAResult(
            cgpa = cgpa,
            totalCredits = totalCredits,
            totalQualityPoints = totalQualityPoints,
            totalSemesters = validSemesters.size
        )
    }
    
    /**
     * Resets all data
     */
    fun resetAllData() {
        _courseName.value = ""
        _selectedGrade.value = ""
        _creditHours.value = ""
        _semesterName.value = ""
        _currentCourses.value = emptyList()
        _semesters.value = emptyList()
        _cgpaResult.value = CGPAResult()
    }
    
    /**
     * Validates current course input
     */
    fun isCurrentCourseValid(): Boolean {
        val creditValue = _creditHours.value.toDoubleOrNull()
        return _courseName.value.isNotBlank() && 
               _selectedGrade.value.isNotBlank() && 
               creditValue != null && creditValue > 0
    }
    
    /**
     * Validates current semester data
     */
    fun isCurrentSemesterValid(): Boolean {
        return _semesterName.value.isNotBlank() && _currentCourses.value.isNotEmpty()
    }
}