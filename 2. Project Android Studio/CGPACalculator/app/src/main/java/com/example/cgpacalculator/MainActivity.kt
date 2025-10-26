package com.example.cgpacalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.cgpacalculator.ui.screen.CGPACalculatorScreen
import com.example.cgpacalculator.ui.theme.CGPACalculatorTheme

/**
 * Main Activity for CGPA Calculator Application
 * 
 * This activity serves as the entry point for the application and hosts
 * the main CGPA Calculator screen using Jetpack Compose.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            CGPACalculatorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CGPACalculatorScreen()
                }
            }
        }
    }
}