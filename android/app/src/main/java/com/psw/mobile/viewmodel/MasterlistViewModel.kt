package com.psw.mobile.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.psw.mobile.data.model.Company
import com.psw.mobile.data.repository.CompanyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MasterlistViewModel(application: Application) : AndroidViewModel(application) {
    private val companyRepository = CompanyRepository(application)

    private val _uiState = MutableStateFlow<MasterlistUiState>(MasterlistUiState.Loading)
    val uiState: StateFlow<MasterlistUiState> = _uiState.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadCompanies()
    }

    fun loadCompanies(search: String? = null) {
        viewModelScope.launch {
            _uiState.value = MasterlistUiState.Loading
            companyRepository.getMasterlist(search).collect { result ->
                _uiState.value = result.fold(
                    onSuccess = { companies -> MasterlistUiState.Success(companies) },
                    onFailure = { error -> MasterlistUiState.Error(error.message ?: "Failed to load companies") }
                )
            }
        }
    }
    
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        loadCompanies(if (query.isBlank()) null else query)
    }
}

sealed class MasterlistUiState {
    object Loading : MasterlistUiState()
    data class Success(val companies: List<Company>) : MasterlistUiState()
    data class Error(val message: String) : MasterlistUiState()
}