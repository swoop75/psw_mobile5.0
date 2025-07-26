package com.psw.mobile.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.psw.mobile.data.model.DashboardStats
import com.psw.mobile.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepository = AuthRepository(application)

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardStats()
    }

    fun loadDashboardStats() {
        viewModelScope.launch {
            _uiState.value = DashboardUiState.Loading
            authRepository.getDashboardStats().collect { result ->
                _uiState.value = result.fold(
                    onSuccess = { stats -> DashboardUiState.Success(stats) },
                    onFailure = { error -> DashboardUiState.Error(error.message ?: "Failed to load stats") }
                )
            }
        }
    }
    
    fun logout() {
        authRepository.logout()
    }
}

sealed class DashboardUiState {
    object Loading : DashboardUiState()
    data class Success(val stats: DashboardStats) : DashboardUiState()
    data class Error(val message: String) : DashboardUiState()
}