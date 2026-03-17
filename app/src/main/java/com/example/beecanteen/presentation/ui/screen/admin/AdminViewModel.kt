package com.example.beecanteen.presentation.ui.screen.admin

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beecanteen.domain.model.CategoryPoll
import com.example.beecanteen.domain.model.admin.CategoryDto
import com.example.beecanteen.domain.repository.admin.AdminRepository
import com.example.beecanteen.domain.repository.admin.AdminResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


@HiltViewModel
class AdminViewModel @Inject constructor(
    private val repository: AdminRepository
) : ViewModel() {

    private val _polls = MutableStateFlow<AdminResult<List<CategoryPoll>>>(AdminResult.Loading)
    val polls: StateFlow<AdminResult<List<CategoryPoll>>> = _polls.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        fetchPolls()
    }

    fun fetchPolls() {
        viewModelScope.launch {
            _isRefreshing.value = true

            val result = repository.getCategoriesWithOptions()
            if (result is AdminResult.Success) {
                result.data.forEach { poll ->
                    Log.d("VoteDebug", "Category: ${poll.category.title}, Total Votes Loaded: ${poll.allVotes.size}")
                }
            }
            _polls.value = result

            _isRefreshing.value = false

        }
    }

    fun resetAllVotes() {
        viewModelScope.launch {
            _isRefreshing.value = true // Show the loading spinner

            val result = repository.resetAllVotes()
            if (result is AdminResult.Success) {
                Log.i("AdminViewModel", "Successfully reset all votes.")
            } else {
                Log.e("AdminViewModel", "Failed to reset votes: ${(result as? AdminResult.Error)?.message}")
            }

            // Refresh the list to reflect the cleared votes
            fetchPolls()
        }
    }

    fun deleteCategory(categoryId: String) {
        viewModelScope.launch {
             repository.deleteCategory(categoryId)

            fetchPolls()
        }
    }
}