package com.example.beecanteen.data.repository

import com.example.beecanteen.domain.model.CategoryPoll
import com.example.beecanteen.domain.model.admin.CategoryDto
import com.example.beecanteen.domain.model.admin.OptionDto
import com.example.beecanteen.domain.model.user.VoteDto
import com.example.beecanteen.domain.repository.admin.AdminRepository
chanimport com.example.beecanteen.domain.repository.authentication.Result
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AdminRepositoryImpl(
    private val firestore: FirebaseFirestore
): AdminRepository {

    override suspend fun createCategory(
        category: CategoryDto,
        options: List<String>
    ) {
        val categoryRef = firestore.collection("categories").document()

        val newCategory = CategoryDto(
            id = categoryRef.id,
            title = category.title,
            startTime = category.startTime,
            endTime = category.endTime
        )

        categoryRef.set(newCategory).await()

        options.forEach { optionName ->
            val optionRef = categoryRef.collection("options").document()
            val option = OptionDto(
                id = optionRef.id,
                name = optionName
            )
            optionRef.set(option).await()
        }
    }

    // The new fetching logic
    override suspend fun getCategoriesWithOptions(): Result<List<CategoryPoll>> {

        return try {
            // Step 1: Fetch ALL parent categories
            val categoriesSnapshot = firestore
                .collection("categories")
                .get()
                .await()

            // Step 2: Loop through the categories using mapNotNull (this safely ignores empty/broken docs)
            val polls = categoriesSnapshot.documents.mapNotNull { categoryDoc ->

                // Parse the category document
                val category = categoryDoc.toObject(CategoryDto::class.java)
                    ?: return@mapNotNull null // Skip if parsing fails

                // Step 3: Fetch the options sub-collection specifically for THIS category's ID
                val optionsSnapshot = firestore
                    .collection("categories")
                    .document(category.id)
                    .collection("options")
                    .get()
                    .await()

                // Step 4: Parse the options documents into a list of OptionDto
                val optionsList = optionsSnapshot.documents.mapNotNull { optionDoc ->
                    optionDoc.toObject(OptionDto::class.java)
                }

                // Step 5: Fetch the votes sub-collection for THIS category's ID
                val votesSnapshot = firestore
                    .collection("categories")
                    .document(category.id)
                    .collection("votes") // Assuming your sub-collection is named "votes"
                    .get()
                    .await()

                // Step 6: Parse the votes documents into a list of VoteDto
                val votesList = votesSnapshot.documents.mapNotNull { voteDoc ->
                    voteDoc.toObject(VoteDto::class.java)
                }

                // Step 7: Bundle them together into our wrapper class!
                CategoryPoll(
                    category = category,
                    options = optionsList,
                    allVotes = votesList
                )
            }

            Result.Success(polls)
        } catch (e: Exception) {
            Result.Error(e.message ?: "failed to fetch catergory poll")
        }
    }

    override suspend fun resetAllVotes(): Result<Unit> {
        return try {
            // Step 1: Get all categories
            val categoriesSnapshot = firestore.collection("categories").get().await()

            // Step 2: Create a batch to group our delete operations
            val batch = firestore.batch()

            for (categoryDoc in categoriesSnapshot.documents) {
                // Optional: If you maintain a totalVotes counter on the category document, reset it to 0
                if (categoryDoc.contains("totalVotes")) {
                    batch.update(categoryDoc.reference, "totalVotes", 0)
                }

                // Step 3: Fetch the votes subcollection for this specific category
                val votesSnapshot = categoryDoc.reference.collection("votes").get().await()

                // Step 4: Queue each vote document for deletion
                for (voteDoc in votesSnapshot.documents) {
                    batch.delete(voteDoc.reference)
                }
            }

            // Step 5: Execute the batch delete all at once
            batch.commit().await()

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to reset all votes")
        }
    }

    override suspend fun deleteCategory(categoryId: String): Result<Unit> {
        return try {
            val categoryRef = firestore.collection("categories").document(categoryId)

            // Step 1: Fetch the options sub-collection
            val optionsSnapshot = categoryRef.collection("options").get().await()

            // Step 2: Delete every option document individually
            optionsSnapshot.documents.forEach { optionDoc ->
                optionDoc.reference.delete().await()
            }

            // Step 3: Now it is safe to delete the parent category document
            categoryRef.delete().await()

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to delete category")
        }
    }
}