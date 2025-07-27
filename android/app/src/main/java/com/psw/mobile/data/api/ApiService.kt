package com.psw.mobile.data.api

import com.psw.mobile.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    // Authentication
    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>
    
    @POST("auth/verify")
    suspend fun verifyToken(@Header("Authorization") token: String): Response<LoginResponse>
    
    // Dashboard
    @GET("dashboard/stats")
    suspend fun getDashboardStats(@Header("Authorization") token: String): Response<DashboardStats>
    
    // Masterlist - All approved companies
    @GET("companies/masterlist")
    suspend fun getMasterlist(
        @Header("Authorization") token: String,
        @Query("search") search: String? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50
    ): Response<CompanyListResponse>
    
    // New Companies - Pending approval
    @GET("companies/new")
    suspend fun getNewCompanies(
        @Header("Authorization") token: String,
        @Query("search") search: String? = null,
        @Query("status") status: String? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50
    ): Response<NewCompanyListResponse>
    
    // Company Actions
    @POST("companies/action")
    suspend fun performCompanyAction(
        @Header("Authorization") token: String,
        @Body request: CompanyActionRequest
    ): Response<CompanyActionResponse>
}