package com.psw.mobile.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val error: String? = null,
    val count: Int? = null
)

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val success: Boolean,
    val token: String,
    val user: User
)

data class User(
    val id: Int,
    val username: String,
    val role: String
)

@Parcelize
data class Company(
    val new_company_id: Int,
    val company_name: String,
    val ticker: String?,
    val isin: String?,
    val country_name: String?,
    val yield: Double?,
    val inspiration: String?,
    val comments: String?,
    val borsdata_available: Boolean?,
    val company_status: String?,
    val country_full_name: String?
) : Parcelable

data class DashboardData(
    val recent_companies: List<RecentCompany>,
    val summary: Summary,
    val top_countries: List<CountryStats>,
    val weekly_additions: Int
)

data class RecentCompany(
    val new_company_id: Int,
    val company_name: String,
    val ticker: String?,
    val country_name: String?,
    val yield: Double?,
    val company_status: String?,
    val date_added: String?
)

data class Summary(
    val total_companies: Int,
    val active_companies: Int,
    val companies_with_yield: Int,
    val avg_yield: Double?,
    val max_yield: Double?
)

data class CountryStats(
    val country_name: String,
    val company_count: Int
)