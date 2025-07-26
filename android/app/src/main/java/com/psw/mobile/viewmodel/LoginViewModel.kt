package com.psw.mobile.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.psw.mobile.data.model.LoginResponse
import com.psw.mobile.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepository = AuthRepository(application)

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Initial)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        checkIfLoggedIn()
    }

    private fun checkIfLoggedIn() {
        if (authRepository.isLoggedIn()) {
            viewModelScope.launch {
                authRepository.verifyToken().collect { result ->
                    if (result.getOrNull() == true) {
                        _uiState.value = LoginUiState.Success(null)
                    }
                }
            }
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            authRepository.login(username, password).collect { result ->
                _uiState.value = result.fold(
                    onSuccess = { loginResponse -> LoginUiState.Success(loginResponse) },
                    onFailure = { error -> LoginUiState.Error(error.message ?: "Login failed") }
                )
            }
        }
    }
    
    fun logout() {
        authRepository.logout()
        _uiState.value = LoginUiState.Initial
    }
}

sealed class LoginUiState {
    object Initial : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val loginResponse: LoginResponse?) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}