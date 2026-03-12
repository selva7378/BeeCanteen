import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.beecanteen.presentation.ui.screen.MainScreen
import com.example.beecanteen.presentation.ui.screen.authentication.LoginScreen
import com.example.beecanteen.presentation.ui.screen.authentication.RegisterScreen
import kotlinx.serialization.Serializable

// Root Graph Routes
@Serializable object LoginRoute
@Serializable object RegisterRoute
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

        // 1. Login Destination
        composable<LoginRoute> {
            LoginScreen(
                onLoginSuccess = {
                    rootNavController.navigate(MainFlowRoute) {
                        popUpTo(LoginRoute) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    rootNavController.navigate(RegisterRoute)
                }
            )
        }

        composable<RegisterRoute> {
            RegisterScreen(
                onRegisterSuccess = {
                    rootNavController.navigate(MainFlowRoute) {
                        popUpTo(LoginRoute) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    rootNavController.popBackStack()
                }
            )
        }

        composable<MainFlowRoute> {
            MainScreen()
        }
    }
}