package com.example.beecanteen.domain.model.user

import androidx.annotation.Keep

@Keep
data class User(
    val id: String = "",
    val email: String = "",
    val name: String = "",
    val role: String = "user"
)
