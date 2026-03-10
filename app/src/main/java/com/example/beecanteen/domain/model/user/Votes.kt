package com.example.beecanteen.domain.model.user

data class Vote(
    val userId: String,
    val categoryId: String,
    val beverageId: String,
)
