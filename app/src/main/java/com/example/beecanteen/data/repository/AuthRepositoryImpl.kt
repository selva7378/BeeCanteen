package com.example.beecanteen.data.repository

import com.example.beecanteen.domain.model.user.User
import com.example.beecanteen.domain.repository.authentication.AuthRepository
import com.example.beecanteen.domain.repository.authentication.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    private val usersCollection = firestore.collection("users")

    override suspend fun login(
        email: String,
        password: String
    ): AuthResult<User> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()

            if (result.user == null) {
                return AuthResult.Error("Login failed")
            }

            // Immediately fetch the full User object (with role and name) from Firestore
            getCurrentUser()

        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Login failed")
        }
    }

    override suspend fun register(
        name: String,
        email: String,
        password: String
    ): AuthResult<User> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: return AuthResult.Error("Registration failed")


            val newUser = User(
                id = firebaseUser.uid,
                email = email,
                name = name,
                role = "user" // Default role for new sign-ups
            )

            // 3. Save the User object directly to Firestore using the UID
            usersCollection.document(firebaseUser.uid).set(newUser).await()

            // 4. Return the new domain user
            AuthResult.Success(newUser)

        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Registration failed")
        }
    }

    override suspend fun logout() {
        auth.signOut()
    }

    override suspend fun getCurrentUser(): AuthResult<User> {
        return try {
            val currentUser = auth.currentUser
                ?: return AuthResult.Error("User is not authenticated")

            // Fetch the document matching the UID
            val documentSnapshot = usersCollection.document(currentUser.uid).get().await()

            if (documentSnapshot.exists()) {
                // Firestore automatically maps the fields to your User data class!
                val user = documentSnapshot.toObject(User::class.java)

                if (user != null) {
                    AuthResult.Success(user)
                } else {
                    AuthResult.Error("Failed to parse user data from Firestore")
                }
            } else {
                AuthResult.Error("User data not found in database")
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Failed to fetch user data")
        }
    }
}