package com.pinjaminperpus.mobile_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import com.pinjaminperpus.mobile_android.ui.navigation.PinjamInNavigation
import com.pinjaminperpus.mobile_android.ui.theme.PinjamInPerpusTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable edge-to-edge display
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        setContent {
            PinjamInPerpusTheme {
                PinjamInNavigation()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    PinjamInPerpusTheme {
        PinjamInNavigation()
    }
}