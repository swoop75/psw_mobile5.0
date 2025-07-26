package com.psw.mobile.data.model

data class User(
    val id: String,
    val username: String,
    val email: String? = null,
    val role: String? = null,
    val createdAt: String? = null
)

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val success: Boolean,
    val message: String? = null,
    val user: User? = null,
    val token: String? = null
)

data class DashboardStats(
    val totalCompanies: Int,
    val newThisWeek: Int,
    val pending: Int,
    val active: Int,
    val inactive: Int
)