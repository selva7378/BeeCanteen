package com.example.beecanteen.domain.model

data class Vote(
    val userId: String,
    val categoryId: String,
    val beverageId: String,
)
