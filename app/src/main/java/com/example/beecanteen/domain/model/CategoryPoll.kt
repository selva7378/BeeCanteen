package com.example.beecanteen.domain.model

import com.example.beecanteen.domain.model.admin.CategoryDto
import com.example.beecanteen.domain.model.admin.OptionDto
import com.example.beecanteen.domain.model.user.VoteDto

data class CategoryPoll(
    val category: CategoryDto,
    val options: List<OptionDto>,
    val currentVotedOptionId: String? = null,
    val allVotes: List<VoteDto> = emptyList()
)
