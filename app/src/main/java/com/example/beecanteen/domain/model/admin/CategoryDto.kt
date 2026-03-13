package com.example.beecanteen.domain.model.admin

data class CategoryDto(
    val id: String = "",
    val title: String = "",
    val startTime: Long = 0L,
    val endTime: Long = 0L
)
