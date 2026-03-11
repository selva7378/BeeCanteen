package com.example.beecanteen.domain.model.user

data class Category(
    val id: String,
    val name: String,
    val startTime: Long,
    val endTime: Long,
    val totalVoteCount: Int,
)
