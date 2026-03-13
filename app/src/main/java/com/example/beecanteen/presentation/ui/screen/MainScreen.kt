package com.example.beecanteen.presentation.ui.screen

import AddCategoryRoute
import AdminRoute
import UserRoute
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowSize
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.beecanteen.presentation.ui.screen.admin.addcatagory.AddCategoryScreen
import com.example.beecanteen.presentation.ui.screen.admin.AdminScreen
import com.example.beecanteen.presentation.ui.screen.voting.VotingScreen
import com.example.beecanteen.presentation.ui.theme.BeeCanteenTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    isAdmin: Boolean,
    name: String,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by rememberSaveable { mutableStateOf(false) }
    val bottomNavController = rememberNavController()
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // 2. Get Context for the Toast
    val context = LocalContext.current

    val windowSize = with(LocalDensity.current) {
        currentWindowSize().toSize().toDpSize()
    }
    val layoutType = if (windowSize.width >= 1200.dp) {
        NavigationSuiteType.NavigationDrawer
    } else if (windowSize.height < 480.dp) {
        NavigationSuiteType.NavigationRail
    } else {
        NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(
            currentWindowAdaptiveInfo()
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val title = if (currentDestination?.hierarchy?.any { it.hasRoute<AdminRoute>() } == true) {
                        "Admin Panel"
                    } else {
                        "Bee Canteen"
                    }
                    Text(title)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,

                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,

                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,

                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    // 1. The Settings Icon Button
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Logout") },
                            onClick = {
                                showMenu = false
                                onLogout()
                            }
                        )
                    }
                }
            )
        }
    )

    {
        NavigationSuiteScaffold(
            layoutType = layoutType,
            modifier = Modifier.fillMaxSize().padding(it),
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
                        if (isAdmin) {
                            bottomNavController.navigate(AdminRoute) {
                                popUpTo(bottomNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        } else {
                            Toast.makeText(context, "Admin access required", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        ) {

            NavHost(
                navController = bottomNavController,
                startDestination = UserRoute,
                modifier = Modifier.fillMaxSize()
            ) {
                composable<UserRoute> {
                    VotingScreen(
                        name = name,
                    )
                }
                composable<AdminRoute> {
                    AdminScreen(
                        onClickFloat = {
                            bottomNavController.navigate(AddCategoryRoute)
                        }
                    )
                }
                composable<AddCategoryRoute> {
                    AddCategoryScreen(
                        onNavigateBack = {
                            bottomNavController.popBackStack()
                        },
                        modifier = Modifier
                    )
                }
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    BeeCanteenTheme {
        MainScreen(
            isAdmin = false,
            name = "John Doe",
            onLogout = {}
        )
    }
}