package com.example.beecanteen.data.repository

import com.example.beecanteen.domain.model.user.User
import com.example.beecanteen.domain.repository.authentication.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.example.beecanteen.domain.repository.authentication.Result


import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    private val usersCollection = firestore.collection("users")

    override suspend fun login(
        email: String,
        password: String
    ): Result<User> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()

            if (result.user == null) {
                return Result.Error("Login failed")
            }

            // Immediately fetch the full User object (with role and name) from Firestore
            getCurrentUser()

        } catch (e: Exception) {
            Result.Error(e.message ?: "Login failed")
        }
    }

    override suspend fun register(
        name: String,
        email: String,
        password: String
    ): Result<User> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: return Result.Error("Registration failed")


            val newUser = User(
                id = firebaseUser.uid,
                email = email,
                name = name,
                role = "user" // Default role for new sign-ups
            )

            // 3. Save the User object directly to Firestore using the UID
            usersCollection.document(firebaseUser.uid).set(newUser).await()

            // 4. Return the new domain user
            Result.Success(newUser)

        } catch (e: Exception) {
            Result.Error(e.message ?: "Registration failed")
        }
    }

    override suspend fun logout() {
        auth.signOut()
    }

    override suspend fun getCurrentUser(): Result<User> {
        return try {
            val currentUser = auth.currentUser
                ?: return Result.Error("User is not authenticated")

            // Fetch the document matching the UID
            val documentSnapshot = usersCollection.document(currentUser.uid).get().await()

            if (documentSnapshot.exists()) {
                // Firestore automatically maps the fields to your User data class!
                val user = documentSnapshot.toObject(User::class.java)

                if (user != null) {
                    Result.Success(user)
                } else {
                    Result.Error("Failed to parse user data from Firestore")
                }
            } else {
                Result.Error("User data not found in database")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to fetch user data")
        }
    }
}