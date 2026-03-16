package com.example.beecanteen.data.repository

import com.example.beecanteen.domain.model.CategoryPoll
import com.example.beecanteen.domain.model.admin.CategoryDto
import com.example.beecanteen.domain.model.admin.OptionDto
import com.example.beecanteen.domain.repository.admin.AdminRepository
import com.example.beecanteen.domain.repository.admin.AdminResult
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
    override suspend fun getCategoriesWithOptions(): AdminResult<List<CategoryPoll>> {

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

                // Step 5: Bundle them together into our wrapper class!
                CategoryPoll(
                    category = category,
                    options = optionsList
                )
            }

            AdminResult.Success(polls)
        } catch (e: Exception) {
            AdminResult.Error(e.message ?: "failed to fetch catergory poll")
        }
    }

    override suspend fun deleteCategory(categoryId: String): AdminResult<Unit> {
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

            AdminResult.Success(Unit)
        } catch (e: Exception) {
            AdminResult.Error(e.message ?: "Failed to delete category")
        }
    }
}