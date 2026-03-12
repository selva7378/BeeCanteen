package com.example.beecanteen.presentation.ui.screen.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beecanteen.domain.model.user.User
import com.example.beecanteen.domain.repository.AuthRepository
import com.example.beecanteen.domain.repository.AuthResult
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.actionCodeSettings
import com.google.firebase.auth.auth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _authState =
        MutableStateFlow<AuthResult<User>?>(null)

    val authState = _authState.asStateFlow()

    fun login(email: String, password: String) {

        viewModelScope.launch {

            _authState.value =
                repository.login(email, password)
        }
    }

    fun getCurrentUser() {
        viewModelScope.launch {
            _authState.value = repository.getCurrentUser()
        }
    }

    fun register(name: String, email: String, password: String) {

        viewModelScope.launch {

            _authState.value =
                repository.register(name , email, password)
        }
    }

    fun logout() {

        viewModelScope.launch {
            repository.logout()
        }
    }
}