package com.pinjaminperpus.mobile_android.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pinjaminperpus.mobile_android.ui.components.BottomNavItem
import com.pinjaminperpus.mobile_android.ui.components.PinjamInBottomNavBar
import com.pinjaminperpus.mobile_android.ui.screens.catalog.CatalogScreen
import com.pinjaminperpus.mobile_android.ui.screens.detail.BookDetailScreen
import com.pinjaminperpus.mobile_android.ui.screens.history.HistoryScreen
import com.pinjaminperpus.mobile_android.ui.screens.home.HomeScreen
import com.pinjaminperpus.mobile_android.ui.screens.login.LoginScreen
import com.pinjaminperpus.mobile_android.ui.screens.notification.NotificationScreen
import com.pinjaminperpus.mobile_android.ui.screens.profile.ProfileScreen
import com.pinjaminperpus.mobile_android.ui.screens.register.RegisterScreen
import com.pinjaminperpus.mobile_android.ui.screens.scanner.QRScannerScreen
import com.pinjaminperpus.mobile_android.ui.screens.scanner.ScanMode

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Home : Screen("home")
    data object Catalog : Screen("catalog")
    data object History : Screen("history")
    data object Profile : Screen("profile")
    data object BookDetail : Screen("book_detail/{bookId}") {
        fun createRoute(bookId: String) = "book_detail/$bookId"
    }
    data object Notification : Screen("notification")
    data object QRScanner : Screen("qr_scanner?mode={mode}") {
        fun createRoute(mode: ScanMode = ScanMode.BORROW) = "qr_scanner?mode=${mode.name}"
    }
}

@Composable
fun PinjamInNavigation(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Login.route
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    // Routes that should show bottom navigation
    val bottomNavRoutes = listOf(
        Screen.Home.route,
        Screen.Catalog.route,
        Screen.History.route,
        Screen.Profile.route
    )
    
    val showBottomNav = currentRoute in bottomNavRoutes
    
    Scaffold(
        bottomBar = {
            if (showBottomNav) {
                PinjamInBottomNavBar(
                    currentRoute = currentRoute ?: Screen.Home.route,
                    onItemClick = { item ->
                        val route = when (item) {
                            BottomNavItem.Home -> Screen.Home.route
                            BottomNavItem.Catalog -> Screen.Catalog.route
                            BottomNavItem.History -> Screen.History.route
                            BottomNavItem.Profile -> Screen.Profile.route
                        }
                        
                        navController.navigate(route) {
                            popUpTo(Screen.Home.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(if (showBottomNav) innerPadding else innerPadding.apply { }),
            enterTransition = {
                fadeIn(animationSpec = tween(300)) + slideInHorizontally(
                    initialOffsetX = { 100 },
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(300)) + slideOutHorizontally(
                    targetOffsetX = { -100 },
                    animationSpec = tween(300)
                )
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(300)) + slideInHorizontally(
                    initialOffsetX = { -100 },
                    animationSpec = tween(300)
                )
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(300)) + slideOutHorizontally(
                    targetOffsetX = { 100 },
                    animationSpec = tween(300)
                )
            }
        ) {
            // Login Screen
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginClick = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onRegisterClick = {
                        navController.navigate(Screen.Register.route)
                    }
                )
            }
            
            // Register Screen
            composable(Screen.Register.route) {
                RegisterScreen(
                    onRegisterClick = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Register.route) { inclusive = true }
                        }
                    },
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onLoginClick = {
                        navController.popBackStack()
                    }
                )
            }
            
            // Home Screen
            composable(Screen.Home.route) {
                HomeScreen(
                    onBookClick = { bookId ->
                        navController.navigate(Screen.BookDetail.createRoute(bookId))
                    },
                    onNotificationClick = {
                        navController.navigate(Screen.Notification.route)
                    },
                    onProfileClick = {
                        navController.navigate(Screen.Profile.route)
                    },
                    onQrScanClick = {
                        navController.navigate(Screen.QRScanner.createRoute())
                    },
                    onSeeAllClick = {
                        navController.navigate(Screen.Catalog.route)
                    }
                )
            }
            
            // Catalog Screen
            composable(Screen.Catalog.route) {
                CatalogScreen(
                    onBookClick = { bookId ->
                        navController.navigate(Screen.BookDetail.createRoute(bookId))
                    },
                    onNotificationClick = {
                        navController.navigate(Screen.Notification.route)
                    }
                )
            }
            
            // History Screen
            composable(Screen.History.route) {
                HistoryScreen(
                    onNotificationClick = {
                        navController.navigate(Screen.Notification.route)
                    },
                    onReturnClick = { _ ->
                        navController.navigate(Screen.QRScanner.createRoute(ScanMode.RETURN))
                    }
                )
            }
            
            // Profile Screen
            composable(Screen.Profile.route) {
                ProfileScreen(
                    onNotificationClick = {
                        navController.navigate(Screen.Notification.route)
                    },
                    onLogoutClick = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onEditProfileClick = { /* TODO */ },
                    onSettingsClick = { /* TODO */ },
                    onAboutClick = { /* TODO */ },
                    onHelpClick = { /* TODO */ }
                )
            }
            
            // Book Detail Screen
            composable(
                route = Screen.BookDetail.route,
                arguments = listOf(
                    navArgument("bookId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val bookId = backStackEntry.arguments?.getString("bookId") ?: ""
                BookDetailScreen(
                    bookId = bookId,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onBorrowSuccess = {
                        navController.navigate(Screen.History.route) {
                            popUpTo(Screen.Home.route)
                        }
                    }
                )
            }
            
            // Notification Screen
            composable(Screen.Notification.route) {
                NotificationScreen(
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
            
            // QR Scanner Screen
            composable(
                route = Screen.QRScanner.route,
                arguments = listOf(
                    navArgument("mode") {
                        type = NavType.StringType
                        defaultValue = ScanMode.BORROW.name
                    }
                )
            ) { backStackEntry ->
                val modeString = backStackEntry.arguments?.getString("mode") ?: ScanMode.BORROW.name
                var mode by remember { mutableStateOf(ScanMode.valueOf(modeString)) }
                
                QRScannerScreen(
                    mode = mode,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onModeChange = { newMode ->
                        mode = newMode
                    }
                )
            }
        }
    }
}
