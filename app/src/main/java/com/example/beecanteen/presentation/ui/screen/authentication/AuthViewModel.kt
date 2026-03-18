package com.example.beecanteen.presentation.ui.screen.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beecanteen.domain.model.user.User
import com.example.beecanteen.domain.repository.authentication.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.beecanteen.domain.repository.authentication.Result

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _authState =
        MutableStateFlow<Result<User>?>(null)

    val authState = _authState.asStateFlow()

    init {
        checkCurrentUser()
    }


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

    fun checkCurrentUser() {
        viewModelScope.launch {
            _authState.value = Result.Loading // Set loading immediately
            _authState.value = repository.getCurrentUser()
        }
    }

    fun logout() {

        viewModelScope.launch {
            repository.logout()
            _authState.value = Result.Error("LOGED OUT")
        }
    }
}