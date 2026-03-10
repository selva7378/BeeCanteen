package com.example.beecanteen.domain.model

import java.util.Date

data class Poll(
    val id: String,
    val title: String,
    val category: String,
    val startTime: Date,
    val endTime: Date,
    val totalVotes: Int,
    val options: List<PollOption>,
    val status: PollStatus
)
