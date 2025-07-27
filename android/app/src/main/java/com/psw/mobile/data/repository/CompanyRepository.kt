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
    
    suspend fun getNewCompanies(
        search: String? = null, 
        status: String? = null,
        broker: String? = null,
        country: String? = null
    ): Flow<Result<List<NewCompany>>> = flow {
        try {
            val token = tokenManager.getToken()
            if (token != null) {
                val response = apiService.getNewCompanies("Bearer $token", search, status, broker, country)
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
    
    suspend fun getBrokers(): Flow<Result<List<Broker>>> = flow {
        try {
            val token = tokenManager.getToken()
            if (token != null) {
                val response = apiService.getBrokers("Bearer $token")
                if (response.isSuccessful) {
                    response.body()?.let { brokersResponse ->
                        if (brokersResponse.success) {
                            emit(Result.success(brokersResponse.brokers))
                        } else {
                            emit(Result.failure(Exception("Failed to fetch brokers")))
                        }
                    } ?: emit(Result.failure(Exception("Invalid response")))
                } else {
                    emit(Result.failure(Exception("Failed to fetch brokers: ${response.code()}")))
                }
            } else {
                emit(Result.failure(Exception("No auth token")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    suspend fun getCountries(): Flow<Result<List<Country>>> = flow {
        try {
            val token = tokenManager.getToken()
            if (token != null) {
                val response = apiService.getCountries("Bearer $token")
                if (response.isSuccessful) {
                    response.body()?.let { countriesResponse ->
                        if (countriesResponse.success) {
                            emit(Result.success(countriesResponse.countries))
                        } else {
                            emit(Result.failure(Exception("Failed to fetch countries")))
                        }
                    } ?: emit(Result.failure(Exception("Invalid response")))
                } else {
                    emit(Result.failure(Exception("Failed to fetch countries: ${response.code()}")))
                }
            } else {
                emit(Result.failure(Exception("No auth token")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}