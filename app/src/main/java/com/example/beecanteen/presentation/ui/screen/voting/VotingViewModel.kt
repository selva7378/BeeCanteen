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
    private val userAlarmManager: UserAlarmManager // <-- INJECTED HERE
) : ViewModel() {

    private val _pollsState = MutableStateFlow<Result<List<CategoryPoll>>?>(null)
    val pollsState: StateFlow<Result<List<CategoryPoll>>?> = _pollsState.asStateFlow()

    // ✨ Convert the DataStore Flow instantly into a StateFlow for the UI!
    val customAlarms: StateFlow<List<String>> = userAlarmManager.savedAlarmsFlow
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        viewModelScope.launch {
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
            }
        }
    }

    // New Alarm Methods
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

    // Voting methods remain exactly the same
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