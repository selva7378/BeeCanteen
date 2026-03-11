package com.example.beecanteen.data.mapper

import com.example.beecanteen.domain.model.user.User
import com.google.firebase.auth.FirebaseUser

fun FirebaseUser.toUser(): User {
    return User(
        id = uid,
        email = email
    )
}