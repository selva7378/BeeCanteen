package com.example.beecanteen.domain.repository.voting

import com.example.beecanteen.domain.model.CategoryPoll
import kotlinx.coroutines.flow.Flow
import com.example.beecanteen.domain.repository.authentication.Result
interface VotingRepository {

    fun getRealTimePolls(): Flow<Result<List<CategoryPoll>>>

    suspend fun castVote(
        categoryId: String,
        optionId: String,
        userId: String
    ): Result<Unit>

    suspend fun revokeVote(
        categoryId: String,
        userId: String
    ): Result<Unit>
}
