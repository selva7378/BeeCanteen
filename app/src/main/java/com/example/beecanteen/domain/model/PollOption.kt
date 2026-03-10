package com.example.beecanteen.domain.model

data class PollOption(
    val id: String,          // "tea", "coffee"
    val label: String,       // "Tea", "Coffee"
    val voteCount: Int,
    val votePercentage: Float // computed from totalVotes
)
