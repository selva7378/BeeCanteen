package com.example.beecanteen.presentation.ui.screen.admin.addcatagory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beecanteen.domain.model.admin.CategoryDto
import com.example.beecanteen.domain.repository.admin.AdminRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddCatagoryViewModel @Inject constructor(
    private val repository: AdminRepository
) : ViewModel() {

    fun createCategory(
        category: CategoryDto,
        options: List<String>
    ) {

        viewModelScope.launch {
            repository.createCategory(category, options)
        }
    }
}