package com.psw.mobile.data.repository

import android.content.Context
import com.psw.mobile.data.api.ApiClient
import com.psw.mobile.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CompanyRepository(private val context: Context) {
    private val apiService = ApiClient.apiService
    private val tokenManager = TokenManager(context)
    
    suspend fun getMasterlist(search: String? = null): Flow<Result<List<Company>>> = flow {
        try {
            val token = tokenManager.getToken()
            if (token != null) {
                val response = apiService.getMasterlist("Bearer $token", search)
                if (response.isSuccessful) {
                    response.body()?.let { companyListResponse ->
                        if (companyListResponse.success) {
                            emit(Result.success(companyListResponse.companies))
                        } else {
                            emit(Result.failure(Exception(companyListResponse.message ?: "Failed to fetch companies")))
                        }
                    } ?: emit(Result.failure(Exception("Invalid response")))
                } else {
                    emit(Result.failure(Exception("Failed to fetch companies: ${response.code()}")))
                }
            } else {
                emit(Result.failure(Exception("No auth token")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    suspend fun getNewCompanies(search: String? = null): Flow<Result<List<NewCompany>>> = flow {
        try {
            val token = tokenManager.getToken()
            if (token != null) {
                val response = apiService.getNewCompanies("Bearer $token", search)
                if (response.isSuccessful) {
                    response.body()?.let { newCompanyListResponse ->
                        if (newCompanyListResponse.success) {
                            emit(Result.success(newCompanyListResponse.companies))
                        } else {
                            emit(Result.failure(Exception(newCompanyListResponse.message ?: "Failed to fetch new companies")))
                        }
                    } ?: emit(Result.failure(Exception("Invalid response")))
                } else {
                    emit(Result.failure(Exception("Failed to fetch new companies: ${response.code()}")))
                }
            } else {
                emit(Result.failure(Exception("No auth token")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    suspend fun approveCompany(companyId: String): Flow<Result<String>> = flow {
        try {
            val token = tokenManager.getToken()
            if (token != null) {
                val request = CompanyActionRequest(companyId, "approve")
                val response = apiService.performCompanyAction("Bearer $token", request)
                if (response.isSuccessful) {
                    response.body()?.let { actionResponse ->
                        if (actionResponse.success) {
                            emit(Result.success(actionResponse.message))
                        } else {
                            emit(Result.failure(Exception(actionResponse.message)))
                        }
                    } ?: emit(Result.failure(Exception("Invalid response")))
                } else {
                    emit(Result.failure(Exception("Failed to approve company: ${response.code()}")))
                }
            } else {
                emit(Result.failure(Exception("No auth token")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    suspend fun rejectCompany(companyId: String): Flow<Result<String>> = flow {
        try {
            val token = tokenManager.getToken()
            if (token != null) {
                val request = CompanyActionRequest(companyId, "reject")
                val response = apiService.performCompanyAction("Bearer $token", request)
                if (response.isSuccessful) {
                    response.body()?.let { actionResponse ->
                        if (actionResponse.success) {
                            emit(Result.success(actionResponse.message))
                        } else {
                            emit(Result.failure(Exception(actionResponse.message)))
                        }
                    } ?: emit(Result.failure(Exception("Invalid response")))
                } else {
                    emit(Result.failure(Exception("Failed to reject company: ${response.code()}")))
                }
            } else {
                emit(Result.failure(Exception("No auth token")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}