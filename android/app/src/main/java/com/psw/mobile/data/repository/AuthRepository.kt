package com.psw.mobile.data.repository

import android.content.Context
import com.psw.mobile.data.api.ApiClient
import com.psw.mobile.data.model.LoginRequest
import com.psw.mobile.data.model.LoginResponse
import com.psw.mobile.data.model.DashboardStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AuthRepository(private val context: Context) {
    private val apiService = ApiClient.apiService
    private val tokenManager = TokenManager(context)
    
    suspend fun login(username: String, password: String): Flow<Result<LoginResponse>> = flow {
        try {
            val response = apiService.login(LoginRequest(username, password))
            if (response.isSuccessful) {
                response.body()?.let { loginResponse ->
                    if (loginResponse.success && loginResponse.token != null) {
                        tokenManager.saveToken(loginResponse.token)
                        emit(Result.success(loginResponse))
                    } else {
                        emit(Result.failure(Exception(loginResponse.message ?: "Login failed")))
                    }
                } ?: emit(Result.failure(Exception("Invalid response")))
            } else {
                emit(Result.failure(Exception("Login failed: ${response.code()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    suspend fun getDashboardStats(): Flow<Result<DashboardStats>> = flow {
        try {
            val token = tokenManager.getToken()
            if (token != null) {
                val response = apiService.getDashboardStats("Bearer $token")
                if (response.isSuccessful) {
                    response.body()?.let { stats ->
                        emit(Result.success(stats))
                    } ?: emit(Result.failure(Exception("No stats data")))
                } else {
                    emit(Result.failure(Exception("Failed to fetch stats: ${response.code()}")))
                }
            } else {
                emit(Result.failure(Exception("No auth token")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    suspend fun verifyToken(): Flow<Result<Boolean>> = flow {
        try {
            val token = tokenManager.getToken()
            if (token != null) {
                val response = apiService.verifyToken("Bearer $token")
                if (response.isSuccessful) {
                    response.body()?.let { loginResponse ->
                        emit(Result.success(loginResponse.success))
                    } ?: emit(Result.success(false))
                } else {
                    emit(Result.success(false))
                }
            } else {
                emit(Result.success(false))
            }
        } catch (e: Exception) {
            emit(Result.success(false))
        }
    }
    
    fun isLoggedIn(): Boolean {
        return tokenManager.getToken() != null
    }
    
    fun logout() {
        tokenManager.clearToken()
    }
}

class TokenManager(private val context: Context) {
    companion object {
        private const val PREFS_NAME = "psw_auth_prefs"
        private const val TOKEN_KEY = "auth_token"
    }

    private val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        sharedPreferences.edit().putString(TOKEN_KEY, token).apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString(TOKEN_KEY, null)
    }

    fun clearToken() {
        sharedPreferences.edit().remove(TOKEN_KEY).apply()
    }
}