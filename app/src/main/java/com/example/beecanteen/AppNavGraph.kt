import android.R.attr.name
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.beecanteen.domain.repository.AuthResult
import com.example.beecanteen.presentation.ui.screen.MainScreen
import com.example.beecanteen.presentation.ui.screen.authentication.AuthViewModel
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
    authViewModel: AuthViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val rootNavController = rememberNavController()
    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(Unit) {
        authViewModel.getCurrentUser()
    }

    if (authState is AuthResult.Loading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator() // Or a custom splash screen logo
        }
        return // Do not render the NavHost yet
    }

    NavHost(
        navController = rootNavController,
        startDestination = if (authState is AuthResult.Success) MainFlowRoute else LoginRoute,
//        startDestination = LoginRoute,
        modifier = Modifier
    ) {

        // 1. Login Destination
        composable<LoginRoute> {
            LoginScreen(
                authViewModel = authViewModel,
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
                authViewModel = authViewModel,
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
            val currentUser = (authState as? AuthResult.Success)?.data
            val isAdmin = currentUser?.role?.trim() == "admin"

            Log.i("AppNavGraph", "role ${currentUser?.role}")

            MainScreen(
                name = currentUser?.name ?: "user",
                isAdmin = isAdmin,
                onLogout = {
                    authViewModel.logout()

                    rootNavController.navigate(LoginRoute) {
                        popUpTo(MainFlowRoute) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}