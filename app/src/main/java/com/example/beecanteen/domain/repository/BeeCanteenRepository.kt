package com.example.beecanteen.domain.repository

import com.example.beecanteen.domain.model.admin.CategoryDto

interface BeeCanteenRepository {
    suspend fun createCategory(
        title: String,
        options: List<String>
    )

    suspend fun getCategories(): List<CategoryDto>
}