package com.psw.mobile.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.psw.mobile.data.model.LoginResponse
import com.psw.mobile.data.repository.PSWRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: PSWRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Initial)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        checkIfLoggedIn()
    }

    private fun checkIfLoggedIn() {
        if (repository.isLoggedIn()) {
            viewModelScope.launch {
                repository.verifyToken().collect { result ->
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
            repository.login(username, password).collect { result ->
                _uiState.value = result.fold(
                    onSuccess = { loginResponse -> LoginUiState.Success(loginResponse) },
                    onFailure = { error -> LoginUiState.Error(error.message ?: "Login failed") }
                )
            }
        }
    }
}

sealed class LoginUiState {
    object Initial : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val loginResponse: LoginResponse?) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}