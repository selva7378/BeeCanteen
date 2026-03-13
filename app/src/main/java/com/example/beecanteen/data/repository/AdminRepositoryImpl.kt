package com.example.beecanteen.data.repository

import com.example.beecanteen.domain.model.CategoryPoll
import com.example.beecanteen.domain.model.admin.CategoryDto
import com.example.beecanteen.domain.model.admin.OptionDto
import com.example.beecanteen.domain.repository.admin.AdminRepository
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
    override suspend fun getCategoriesWithOptions(): List<CategoryPoll> {

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

        return polls // Return the fully assembled list
    }
}