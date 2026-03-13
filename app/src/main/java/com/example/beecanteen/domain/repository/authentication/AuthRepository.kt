package com.example.beecanteen.domain.repository.authentication

import com.example.beecanteen.domain.model.user.User
import com.example.beecanteen.domain.repository.authentication.AuthResult

interface AuthRepository {

    suspend fun login(
        email: String,
        password: String
    ): AuthResult<User>

    suspend fun register(
        name: String,
        email: String,
        password: String
    ): AuthResult<User>

    suspend fun logout()

    suspend fun getCurrentUser(): AuthResult<User>
}