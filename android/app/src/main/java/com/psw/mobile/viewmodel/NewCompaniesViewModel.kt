package com.psw.mobile.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.psw.mobile.data.model.NewCompany
import com.psw.mobile.data.model.Broker
import com.psw.mobile.data.model.Country
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
    
    private val _brokers = MutableStateFlow<List<Broker>>(emptyList())
    val brokers: StateFlow<List<Broker>> = _brokers.asStateFlow()
    
    private val _countries = MutableStateFlow<List<Country>>(emptyList())
    val countries: StateFlow<List<Country>> = _countries.asStateFlow()

    init {
        loadNewCompanies()
        loadBrokers()
        loadCountries()
    }

    fun loadNewCompanies(
        search: String? = null, 
        status: String? = null,
        broker: String? = null,
        country: String? = null
    ) {
        viewModelScope.launch {
            _uiState.value = NewCompaniesUiState.Loading
            companyRepository.getNewCompanies(search, status, broker, country).collect { result ->
                _uiState.value = result.fold(
                    onSuccess = { companies -> NewCompaniesUiState.Success(companies) },
                    onFailure = { error -> NewCompaniesUiState.Error(error.message ?: "Failed to load new companies") }
                )
            }
        }
    }
    
    fun loadBrokers() {
        viewModelScope.launch {
            companyRepository.getBrokers().collect { result ->
                result.fold(
                    onSuccess = { brokerList -> _brokers.value = brokerList },
                    onFailure = { /* Handle error silently for now */ }
                )
            }
        }
    }
    
    fun loadCountries() {
        viewModelScope.launch {
            companyRepository.getCountries().collect { result ->
                result.fold(
                    onSuccess = { countryList -> _countries.value = countryList },
                    onFailure = { /* Handle error silently for now */ }
                )
            }
        }
    }
    
    fun clearActionState() {
        _actionState.value = ActionState.Idle
    }
    
    fun searchCompanies(
        query: String, 
        status: String? = null,
        broker: String? = null,
        country: String? = null
    ) {
        val searchQuery = if (query.isBlank()) null else query
        loadNewCompanies(searchQuery, status, broker, country)
    }
    
    fun applyFilters(
        search: String? = null,
        status: String? = null,
        broker: String? = null,
        country: String? = null
    ) {
        loadNewCompanies(search, status, broker, country)
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