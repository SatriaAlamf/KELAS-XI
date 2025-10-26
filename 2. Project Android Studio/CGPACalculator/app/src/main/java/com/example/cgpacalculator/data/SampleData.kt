package com.example.cgpacalculator.data

import com.example.cgpacalculator.model.Course
import com.example.cgpacalculator.model.Semester

/**
 * Sample data for testing and demonstration purposes
 */
object SampleData {
    
    /**
     * Sample courses for Computer Science program
     */
    val sampleCourses = listOf(
        // Programming courses
        Course("1", "Introduction to Programming", "A", 4.0),
        Course("2", "Data Structures", "A-", 3.0),
        Course("3", "Object-Oriented Programming", "B+", 3.0),
        Course("4", "Database Systems", "A", 3.0),
        Course("5", "Software Engineering", "B+", 4.0),
        
        // Mathematics courses
        Course("6", "Calculus I", "B", 4.0),
        Course("7", "Calculus II", "B+", 4.0),
        Course("8", "Linear Algebra", "A-", 3.0),
        Course("9", "Statistics", "B", 3.0),
        
        // Core CS courses
        Course("10", "Computer Architecture", "B-", 3.0),
        Course("11", "Operating Systems", "A-", 3.0),
        Course("12", "Algorithms", "A", 4.0),
        Course("13", "Networks", "B+", 3.0),
        
        // General Education
        Course("14", "English Composition", "A", 3.0),
        Course("15", "Physics I", "B", 4.0),
        Course("16", "Chemistry", "B-", 3.0)
    )
    
    /**
     * Sample semesters with realistic course distribution
     */
    val sampleSemesters = listOf(
        Semester(
            id = "sem1",
            semesterName = "Fall 2023 - Semester 1",
            courses = listOf(
                Course("1", "Introduction to Programming", "A", 4.0),
                Course("6", "Calculus I", "B", 4.0),
                Course("14", "English Composition", "A", 3.0),
                Course("15", "Physics I", "B", 4.0)
            )
        ),
        
        Semester(
            id = "sem2", 
            semesterName = "Spring 2024 - Semester 2",
            courses = listOf(
                Course("2", "Data Structures", "A-", 3.0),
                Course("7", "Calculus II", "B+", 4.0),
                Course("8", "Linear Algebra", "A-", 3.0),
                Course("16", "Chemistry", "B-", 3.0)
            )
        ),
        
        Semester(
            id = "sem3",
            semesterName = "Fall 2024 - Semester 3", 
            courses = listOf(
                Course("3", "Object-Oriented Programming", "B+", 3.0),
                Course("4", "Database Systems", "A", 3.0),
                Course("10", "Computer Architecture", "B-", 3.0),
                Course("9", "Statistics", "B", 3.0)
            )
        ),
        
        Semester(
            id = "sem4",
            semesterName = "Spring 2025 - Semester 4",
            courses = listOf(
                Course("5", "Software Engineering", "B+", 4.0),
                Course("11", "Operating Systems", "A-", 3.0),
                Course("12", "Algorithms", "A", 4.0),
                Course("13", "Networks", "B+", 3.0)
            )
        )
    )
    
    /**
     * Different GPA scenarios for testing
     */
    object TestScenarios {
        
        /**
         * Excellent student - High GPA scenario
         */
        val excellentStudent = listOf(
            Semester(
                id = "ex1",
                semesterName = "Semester 1",
                courses = listOf(
                    Course("1", "Math", "A+", 4.0),
                    Course("2", "Physics", "A", 3.0),
                    Course("3", "Chemistry", "A", 3.0),
                    Course("4", "English", "A-", 3.0)
                )
            ),
            Semester(
                id = "ex2",
                semesterName = "Semester 2", 
                courses = listOf(
                    Course("5", "Programming", "A", 4.0),
                    Course("6", "Calculus", "A", 4.0),
                    Course("7", "Biology", "A-", 3.0)
                )
            )
        )
        
        /**
         * Average student - Medium GPA scenario  
         */
        val averageStudent = listOf(
            Semester(
                id = "av1",
                semesterName = "Semester 1",
                courses = listOf(
                    Course("1", "Math", "B", 4.0),
                    Course("2", "Physics", "B+", 3.0),
                    Course("3", "Chemistry", "C+", 3.0),
                    Course("4", "English", "B-", 3.0)
                )
            ),
            Semester(
                id = "av2", 
                semesterName = "Semester 2",
                courses = listOf(
                    Course("5", "Programming", "B+", 4.0),
                    Course("6", "Calculus", "C", 4.0),
                    Course("7", "Biology", "B", 3.0)
                )
            )
        )
        
        /**
         * Struggling student - Lower GPA scenario
         */
        val strugglingStudent = listOf(
            Semester(
                id = "st1",
                semesterName = "Semester 1",
                courses = listOf(
                    Course("1", "Math", "C", 4.0),
                    Course("2", "Physics", "D+", 3.0),
                    Course("3", "Chemistry", "C-", 3.0),
                    Course("4", "English", "B-", 3.0)
                )
            ),
            Semester(
                id = "st2",
                semesterName = "Semester 2",
                courses = listOf(
                    Course("5", "Programming", "D", 4.0),
                    Course("6", "Calculus", "F", 4.0),
                    Course("7", "Biology", "C+", 3.0)
                )
            )
        )
    }
    
    /**
     * Grade distribution examples
     */
    val gradeExamples = mapOf(
        "A+" to listOf("Advanced Calculus", "Honors Physics"),
        "A" to listOf("Programming Fundamentals", "Linear Algebra"),
        "A-" to listOf("Data Structures", "Statistics"),  
        "B+" to listOf("Database Design", "Software Engineering"),
        "B" to listOf("Computer Networks", "Web Development"),
        "B-" to listOf("Computer Graphics", "Human-Computer Interaction"),
        "C+" to listOf("Discrete Mathematics", "Ethics in Technology"),
        "C" to listOf("Technical Writing", "Business Management"),
        "C-" to listOf("Economics", "Psychology"),
        "D+" to listOf("Art History", "Music Appreciation"),
        "D" to listOf("Physical Education", "Foreign Language"),
        "F" to listOf("Failed Course Example")
    )
    
    /**
     * Credit hour examples by course type
     */
    val creditExamples = mapOf(
        "Major Courses" to 3.0..4.0,
        "Mathematics" to 3.0..4.0, 
        "Laboratory Courses" to 1.0..2.0,
        "General Education" to 3.0,
        "Electives" to 1.0..3.0,
        "Capstone Project" to 6.0,
        "Internship" to 3.0..12.0
    )
}