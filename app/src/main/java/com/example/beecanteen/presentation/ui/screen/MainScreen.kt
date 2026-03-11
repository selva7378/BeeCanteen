package com.example.beecanteen.presentation.ui.screen

import AdminRoute
import UserRoute
import android.net.http.SslCertificate.restoreState
import android.net.http.SslCertificate.saveState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.beecanteen.presentation.ui.screen.admin.AdminScreen
import com.example.beecanteen.presentation.ui.screen.voting.VotingScreen

@Composable
fun MainScreen() {
    val bottomNavController = rememberNavController()
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            item(
                selected = currentDestination?.hierarchy?.any { it.hasRoute<UserRoute>() } == true,
                icon = { Icon(Icons.Default.Person, contentDescription = "User") },
                label = { Text("User") },
                onClick = {
                    bottomNavController.navigate(UserRoute) {
                        popUpTo(bottomNavController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
            item(
                selected = currentDestination?.hierarchy?.any { it.hasRoute<AdminRoute>() } == true,
                icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin") },
                label = { Text("Admin") },
                onClick = {
                    bottomNavController.navigate(AdminRoute) {
                        popUpTo(bottomNavController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) {
        // Inner NavHost for the Bottom Navigation screens
        NavHost(
            navController = bottomNavController,
            startDestination = UserRoute
        ) {
            composable<UserRoute> {
                VotingScreen() // Replace with your actual composable
            }
            composable<AdminRoute> {
                AdminScreen() // Replace with your actual composable
            }
        }
    }
}