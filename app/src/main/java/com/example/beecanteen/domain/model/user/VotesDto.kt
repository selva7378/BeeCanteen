package com.example.beecanteen.domain.model.user

data class VoteDto(
    val userId: String = "",
    val userName: String = "",
    val optionId: String = "",
    val timestamp: Long = 0L
)
