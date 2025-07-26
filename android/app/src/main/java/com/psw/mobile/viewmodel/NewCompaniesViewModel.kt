package com.psw.mobile.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.psw.mobile.data.model.NewCompany
import com.psw.mobile.data.repository.CompanyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NewCompaniesViewModel(application: Application) : AndroidViewModel(application) {
    private val companyRepository = CompanyRepository(application)

    private val _uiState = MutableStateFlow<NewCompaniesUiState>(NewCompaniesUiState.Loading)
    val uiState: StateFlow<NewCompaniesUiState> = _uiState.asStateFlow()
    
    private val _actionState = MutableStateFlow<ActionState>(ActionState.Idle)
    val actionState: StateFlow<ActionState> = _actionState.asStateFlow()

    init {
        loadNewCompanies()
    }

    fun loadNewCompanies() {
        viewModelScope.launch {
            _uiState.value = NewCompaniesUiState.Loading
            companyRepository.getNewCompanies().collect { result ->
                _uiState.value = result.fold(
                    onSuccess = { companies -> NewCompaniesUiState.Success(companies) },
                    onFailure = { error -> NewCompaniesUiState.Error(error.message ?: "Failed to load new companies") }
                )
            }
        }
    }
    
    fun approveCompany(companyId: String) {
        viewModelScope.launch {
            _actionState.value = ActionState.Loading(companyId, "approve")
            companyRepository.approveCompany(companyId).collect { result ->
                result.fold(
                    onSuccess = { message ->
                        _actionState.value = ActionState.Success(message)
                        // Reload the list to reflect changes
                        loadNewCompanies()
                    },
                    onFailure = { error ->
                        _actionState.value = ActionState.Error(error.message ?: "Failed to approve company")
                    }
                )
            }
        }
    }
    
    fun rejectCompany(companyId: String) {
        viewModelScope.launch {
            _actionState.value = ActionState.Loading(companyId, "reject")
            companyRepository.rejectCompany(companyId).collect { result ->
                result.fold(
                    onSuccess = { message ->
                        _actionState.value = ActionState.Success(message)
                        // Reload the list to reflect changes
                        loadNewCompanies()
                    },
                    onFailure = { error ->
                        _actionState.value = ActionState.Error(error.message ?: "Failed to reject company")
                    }
                )
            }
        }
    }
    
    fun clearActionState() {
        _actionState.value = ActionState.Idle
    }
}

sealed class NewCompaniesUiState {
    object Loading : NewCompaniesUiState()
    data class Success(val companies: List<NewCompany>) : NewCompaniesUiState()
    data class Error(val message: String) : NewCompaniesUiState()
}

sealed class ActionState {
    object Idle : ActionState()
    data class Loading(val companyId: String, val action: String) : ActionState()
    data class Success(val message: String) : ActionState()
    data class Error(val message: String) : ActionState()
}