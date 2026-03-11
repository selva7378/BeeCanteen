package com.example.beecanteen.data.repository

import com.example.beecanteen.data.mapper.toUser
import com.example.beecanteen.domain.model.user.User
import com.example.beecanteen.domain.repository.AuthRepository
import com.example.beecanteen.domain.repository.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth
) : AuthRepository {

    override suspend fun login(
        email: String,
        password: String
    ): AuthResult<User> {

        return try {

            val result = auth
                .signInWithEmailAndPassword(email, password)
                .await()

            val firebaseUser = result.user
                ?: return AuthResult.Error("Login failed")

            AuthResult.Success(firebaseUser.toUser())

        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Login failed")
        }
    }

    override suspend fun register(
        email: String,
        password: String
    ): AuthResult<User> {

        return try {

            val result = auth
                .createUserWithEmailAndPassword(email, password)
                .await()

            val firebaseUser = result.user
                ?: return AuthResult.Error("Registration failed")

            AuthResult.Success(firebaseUser.toUser())

        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Registration failed")
        }
    }

    override suspend fun logout() {
        auth.signOut()
    }

    override fun getCurrentUser(): User? {
        return auth.currentUser?.toUser()
    }
}