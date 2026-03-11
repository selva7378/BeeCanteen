package com.example.beecanteen.presentation.ui.screen.authentication

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.beecanteen.domain.repository.AuthResult

@Composable
fun AuthScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
    onLoginSuccess: () -> Unit
) {

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    val context = LocalContext.current
    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(authState) {

        when (authState) {

            is AuthResult.Success -> {
                Toast.makeText(context, "Login success", Toast.LENGTH_SHORT).show()
                onLoginSuccess()
            }

            is AuthResult.Error -> {
                Toast.makeText(
                    context,
                    (authState as AuthResult.Error).message,
                    Toast.LENGTH_LONG
                ).show()
            }

            else -> {}
        }
    }

    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {

                if (email.isBlank() || password.isBlank()) {
                    Toast.makeText(
                        context,
                        "Email and password required",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@Button
                }

                authViewModel.login(email, password)

            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }

        Button(
            onClick = {

                if (email.isBlank() || password.isBlank()) {
                    Toast.makeText(
                        context,
                        "Email and password required",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@Button
                }

                authViewModel.register(email, password)

            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }

        if (authState is AuthResult.Loading) {
            CircularProgressIndicator()
        }
    }
}