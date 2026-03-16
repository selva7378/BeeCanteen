package com.example.beecanteen.data.repository

import com.example.beecanteen.domain.model.CategoryPoll
import com.example.beecanteen.domain.model.admin.CategoryDto
import com.example.beecanteen.domain.model.admin.OptionDto
import com.example.beecanteen.domain.model.user.VoteDto
import com.example.beecanteen.domain.repository.voting.VotingRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class VotingRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : VotingRepository {

    override fun getRealTimePolls(): Flow<Result<List<CategoryPoll>>> = callbackFlow {
        val userId = auth.currentUser?.uid ?: ""

        // Listen for real-time updates on the categories collection
        val listener = firestore.collection("categories")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    // Launch a coroutine to fetch the subcollections for the updated categories
                    launch {
                        try {
                            val polls = snapshot.documents.mapNotNull { doc ->
                                val category = doc.toObject(CategoryDto::class.java) ?: return@mapNotNull null

                                // Fetch options
                                val optionsSnapshot = doc.reference.collection("options").get().await()
                                val options = optionsSnapshot.documents.mapNotNull { it.toObject(OptionDto::class.java) }

                                // Check if the CURRENT USER has voted
                                var currentVotedOptionId: String? = null
                                if (userId.isNotEmpty()) {
                                    val voteDoc = doc.reference.collection("votes").document(userId).get().await()
                                    if (voteDoc.exists()) {
                                        currentVotedOptionId = voteDoc.getString("optionId")
                                    }
                                }

                                CategoryPoll(
                                    category = category,
                                    options = options,
                                    currentVotedOptionId = currentVotedOptionId,
                                    allVotes = emptyList() // Not needed for the user facing screen
                                )
                            }
                            trySend(Result.success(polls))
                        } catch (e: Exception) {
                            trySend(Result.failure(e))
                        }
                    }
                }
            }

        // Clean up the listener when the flow is closed/cancelled
        awaitClose { listener.remove() }
    }

    override suspend fun castVote(
        categoryId: String,
        optionId: String,
        userId: String
    ): Result<Unit> {
        return try {

            val categoryRef = firestore.collection("categories").document(categoryId)
            val voteRef = categoryRef.collection("votes").document(userId)

            // 🔹 Get username from Firestore
            val userSnapshot = firestore.collection("users")
                .document(userId)
                .get()
                .await()

            val userName = userSnapshot.getString("name") ?: ""

            firestore.runTransaction { transaction ->

                val categorySnapshot = transaction.get(categoryRef)
                val voteSnapshot = transaction.get(voteRef)

                val currentTotal = categorySnapshot.getLong("totalVotes") ?: 0L

                // Increment only if first vote
                if (!voteSnapshot.exists()) {
                    transaction.update(categoryRef, "totalVotes", currentTotal + 1)
                }

                val voteDto = VoteDto(
                    userId = userId,
                    userName = userName,
                    optionId = optionId,
                    timestamp = System.currentTimeMillis()
                )

                transaction.set(voteRef, voteDto)

            }.await()

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun revokeVote(categoryId: String, userId: String): Result<Unit> {
        return try {
            val categoryRef = firestore.collection("categories").document(categoryId)
            val voteRef = categoryRef.collection("votes").document(userId)

            firestore.runTransaction { transaction ->
                val categorySnapshot = transaction.get(categoryRef)
                val voteSnapshot = transaction.get(voteRef)

                // Only decrement if a vote actually exists
                if (voteSnapshot.exists()) {
                    val currentTotal = categorySnapshot.getLong("totalVotes") ?: 0L
                    val newTotal = if (currentTotal > 0) currentTotal - 1 else 0

                    transaction.update(categoryRef, "totalVotes", newTotal)
                    transaction.delete(voteRef)
                }
            }.await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}