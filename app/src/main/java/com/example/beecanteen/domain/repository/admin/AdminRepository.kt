package com.example.beecanteen.domain.repository.admin

import com.example.beecanteen.domain.model.CategoryPoll
import com.example.beecanteen.domain.model.admin.CategoryDto

interface AdminRepository {
    suspend fun createCategory(category: CategoryDto, options: List<String>)

    // Update the return type to our new wrapper class
    suspend fun getCategoriesWithOptions(): AdminResult<List<CategoryPoll>>

    suspend fun deleteCategory(categoryId: String): AdminResult<Unit>

    suspend fun resetAllVotes(): AdminResult<Unit>
}