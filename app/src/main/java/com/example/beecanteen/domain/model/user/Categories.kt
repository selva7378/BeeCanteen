package com.example.beecanteen.domain.model.user

data class Categories(
    val id: String,
    val name: String,
    val icon: String,
    val startTime: Long,
    val endTime: Long,
    val totalVoteCount: Int,
)
