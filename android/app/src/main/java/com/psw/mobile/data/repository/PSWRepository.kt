package com.psw.mobile.data.repository

import com.psw.mobile.data.api.ApiService
import com.psw.mobile.data.local.TokenManager
import com.psw.mobile.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PSWRepository @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) {
    
    suspend fun login(username: String, password: String): Flow<Result<LoginResponse>> = flow {
        try {
            val response = apiService.login(LoginRequest(username, password))
            if (response.isSuccessful && response.body() != null) {
                val loginResponse = response.body()!!
                tokenManager.saveToken(loginResponse.token)
                emit(Result.success(loginResponse))
            } else {
                emit(Result.failure(Exception("Login failed: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    suspend fun getDashboardData(): Flow<Result<DashboardData>> = flow {
        try {
            val response = apiService.getDashboardData()
            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()!!.data!!
                emit(Result.success(data))
            } else {
                emit(Result.failure(Exception("Failed to fetch dashboard data")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    suspend fun getCompanies(
        status: Int? = null,
        country: String? = null,
        limit: Int = 20,
        offset: Int = 0
    ): Flow<Result<List<Company>>> = flow {
        try {
            val response = apiService.getCompanies(status, country, limit, offset)
            if (response.isSuccessful && response.body()?.success == true) {
                val companies = response.body()!!.data ?: emptyList()
                emit(Result.success(companies))
            } else {
                emit(Result.failure(Exception("Failed to fetch companies")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    suspend fun getCompany(id: Int): Flow<Result<Company>> = flow {
        try {
            val response = apiService.getCompany(id)
            if (response.isSuccessful && response.body()?.success == true) {
                val company = response.body()!!.data!!
                emit(Result.success(company))
            } else {
                emit(Result.failure(Exception("Company not found")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    suspend fun verifyToken(): Flow<Result<Boolean>> = flow {
        try {
            val response = apiService.verifyToken()
            emit(Result.success(response.isSuccessful))
        } catch (e: Exception) {
            emit(Result.success(false))
        }
    }
    
    fun isLoggedIn(): Boolean = tokenManager.getToken() != null
    
    fun logout() = tokenManager.clearToken()
}