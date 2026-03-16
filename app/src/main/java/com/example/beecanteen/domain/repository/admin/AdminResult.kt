package com.example.beecanteen.domain.repository.admin

sealed class AdminResult<out T> {
    data class Success<out T>(
        val data: T
    ) : AdminResult<T>()

    data class Error(
        val message: String
    ) : AdminResult<Nothing>()

    object Loading : AdminResult<Nothing>()
}