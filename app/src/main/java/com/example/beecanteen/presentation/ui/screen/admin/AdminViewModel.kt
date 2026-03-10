package com.example.beecanteen.presentation.ui.screen.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beecanteen.domain.repository.BeeCanteenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val repository: BeeCanteenRepository
) : ViewModel() {

    fun createCategory(
        title: String,
        options: List<String>
    ) {

        viewModelScope.launch {
            repository.createCategory(title, options)
        }
    }
}