import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.beecanteen.presentation.ui.screen.MainScreen
import com.example.beecanteen.presentation.ui.screen.authentication.AuthScreen

import kotlinx.serialization.Serializable

// Root Graph Routes
@Serializable object LoginRoute
@Serializable object MainFlowRoute

// Bottom Nav Routes (Inside Main Flow)
@Serializable object UserRoute
@Serializable object AdminRoute
@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier
) {
    val rootNavController = rememberNavController()

    NavHost(
        navController = rootNavController,
        startDestination = LoginRoute
    ) {
        composable<LoginRoute> {
            AuthScreen(
                onLoginSuccess = {
                    // Navigate to Main flow and clear the backstack so
                    // the user can't press 'Back' to return to Login
                    rootNavController.navigate(MainFlowRoute) {
                        popUpTo(LoginRoute) { inclusive = true }
                    }
                }
            )
        }

        composable<MainFlowRoute> {
            // This screen hosts the Bottom Navigation and its own inner NavHost
            MainScreen()
        }
    }
}