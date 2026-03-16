package com.example.beecanteen.domain.repository.authentication

import com.example.beecanteen.domain.model.user.User

interface AuthRepository {

    suspend fun login(
        email: String,
        password: String
    ): Result<User>

    suspend fun register(
        name: String,
        email: String,
        password: String
    ): Result<User>

    suspend fun logout()

    suspend fun getCurrentUser(): Result<User>
}