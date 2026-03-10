package com.example.beecanteen.data.repository

import com.example.beecanteen.domain.model.admin.CategoryDto
import com.example.beecanteen.domain.model.admin.OptionDto
import com.example.beecanteen.domain.repository.BeeCanteenRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class BeeCanteenRepositoryImpl(
    private val firestore: FirebaseFirestore
): BeeCanteenRepository {
    override suspend fun createCategory(
        title: String,
        options: List<String>
    ) {

        val categoryRef = firestore
            .collection("categories")
            .document()

        val category = CategoryDto(
            id = categoryRef.id,
            title = title
        )

        categoryRef.set(category).await()

        options.forEach { optionName ->

            val optionRef = categoryRef
                .collection("options")
                .document()

            val option = OptionDto(
                id = optionRef.id,
                name = optionName
            )

            optionRef.set(option).await()
        }
    }

    override suspend fun getCategories(): List<CategoryDto> {

        val snapshot = firestore
            .collection("categories")
            .get()
            .await()

        return snapshot.documents.mapNotNull { doc ->

            doc.toObject(CategoryDto::class.java)?.let {
                CategoryDto(
                    id = it.id,
                    title = it.title
                )
            }
        }
    }

}