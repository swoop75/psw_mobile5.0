package com.psw.mobile.data.api

import com.psw.mobile.data.model.ApiResponse
import com.psw.mobile.data.model.Company
import com.psw.mobile.data.model.DashboardData
import com.psw.mobile.data.model.LoginRequest
import com.psw.mobile.data.model.LoginResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
    
    @GET("auth/verify")
    suspend fun verifyToken(): Response<ApiResponse<Any>>
    
    @GET("dashboard/overview")
    suspend fun getDashboardData(): Response<ApiResponse<DashboardData>>
    
    @GET("companies")
    suspend fun getCompanies(
        @Query("status") status: Int? = null,
        @Query("country") country: String? = null,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): Response<ApiResponse<List<Company>>>
    
    @GET("companies/{id}")
    suspend fun getCompany(@Path("id") id: Int): Response<ApiResponse<Company>>
    
    @GET("companies/stats/summary")
    suspend fun getCompanyStats(): Response<ApiResponse<Any>>
}