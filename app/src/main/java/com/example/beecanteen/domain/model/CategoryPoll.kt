package com.example.beecanteen.domain.model

import com.example.beecanteen.domain.model.admin.CategoryDto
import com.example.beecanteen.domain.model.admin.OptionDto

data class CategoryPoll(
    val category: CategoryDto,
    val options: List<OptionDto>
)
