package com.example.beecanteen.presentation.ui.screen.voting

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beecanteen.data.repository.UserAlarmManager
import com.example.beecanteen.domain.model.CategoryPoll
import com.example.beecanteen.domain.repository.voting.VotingRepository
import com.example.beecanteen.domain.repository.authentication.Result
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
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
    private val firebaseAuth: FirebaseAuth,
    private val userAlarmManager: UserAlarmManager
) : ViewModel() {

    private val _pollsState = MutableStateFlow<Result<List<CategoryPoll>>?>(null)
    val pollsState: StateFlow<Result<List<CategoryPoll>>?> = _pollsState.asStateFlow()

    // ✨ NEW: State to track if the UI is currently refreshing
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    // ✨ NEW: Track the job so we can cancel and restart the listener
    private var pollJob: Job? = null

    val customAlarms: StateFlow<List<String>> = userAlarmManager.savedAlarmsFlow
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        fetchPolls()
    }

    // ✨ NEW: Extracted into its own function so Pull-to-Refresh can trigger it
    fun fetchPolls() {
        pollJob?.cancel() // Stop the old listener
        pollJob = viewModelScope.launch {
            _isRefreshing.value = true

            repository.getRealTimePolls().collect { result ->
                val currentMillis = getCurrentMillisSinceMidnight()
                val filteredResult = when (result) {
                    is Result.Success -> {
                        val filteredList = result.data.filter { poll ->
                            currentMillis in poll.category.startTime..poll.category.endTime
                        }
                        Result.Success(filteredList)
                    }
                    is Result.Error -> result
                    is Result.Loading -> result
                }

                _pollsState.value = filteredResult
                _isRefreshing.value = false // Turn off the loading spinner when data arrives!
            }
        }
    }

    fun addCustomAlarm(hour: Int, minute: Int) {
        viewModelScope.launch {
            userAlarmManager.scheduleAlarm(hour, minute)
        }
    }

    fun removeCustomAlarm(timeString: String) {
        viewModelScope.launch {
            userAlarmManager.cancelAlarm(timeString)
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

    private fun getCurrentMillisSinceMidnight(): Long {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).time
        return (now.hour * 3600000L) + (now.minute * 60000L) + (now.second * 1000L)
    }
}