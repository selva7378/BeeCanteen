package com.example.beecanteen.presentation.ui.screen.voting

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beecanteen.domain.model.CategoryPoll
import com.example.beecanteen.domain.repository.voting.VotingRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

@HiltViewModel
class VotingViewModel @Inject constructor(
    private val repository: VotingRepository,
    private val firebaseAuth: FirebaseAuth
): ViewModel() {

    private val _pollsState = MutableStateFlow<Result<List<CategoryPoll>>?>(null)
    val pollsState: StateFlow<Result<List<CategoryPoll>>?> = _pollsState.asStateFlow()

    init {
        // Start listening to the real-time flow immediately
        viewModelScope.launch {
            repository.getRealTimePolls().collect { result ->
                _pollsState.value = result
            }
        }
    }

    fun castVote(categoryId: String, optionId: String) {
        val userId = firebaseAuth.currentUser?.uid ?: return
        viewModelScope.launch {
            Log.i("VotingViewModel", "castVote: $categoryId, $optionId, $userId")
            repository.castVote(categoryId, optionId, userId)
        }
    }

    fun revokeVote(categoryId: String) {
        val userId = firebaseAuth.currentUser?.uid ?: return
        viewModelScope.launch {
            repository.revokeVote(categoryId, userId)
        }
    }

    fun todayFormatted(): String {
        val fmt = LocalDate.Format {
            monthName(MonthNames.ENGLISH_FULL)
            char(' ')
            dayOfMonth()
        }
        return Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date
            .format(fmt)
    }
}