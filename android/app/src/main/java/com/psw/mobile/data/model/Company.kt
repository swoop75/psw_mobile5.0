package com.psw.mobile.data.model

data class Company(
    val id: String,
    val name: String,
    val industry: String,
    val location: String,
    val status: String, // "Active", "Pending", "Inactive"
    val description: String? = null,
    val contactEmail: String? = null,
    val contactPhone: String? = null,
    val website: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

data class NewCompany(
    val id: String,
    val name: String,
    val industry: String,
    val location: String,
    val description: String,
    val contactEmail: String? = null,
    val contactPhone: String? = null,
    val website: String? = null,
    val submittedBy: String,
    val submittedDate: String,
    val status: String = "Pending", // "Pending", "Active", "Inactive"
    val brokerName: String? = null,
    val countryName: String? = null,
    val ticker: String? = null,
    val yield_percent: Double = 0.0
)

data class CompanyListResponse(
    val success: Boolean,
    val companies: List<Company>,
    val totalCount: Int,
    val message: String? = null
)

data class NewCompanyListResponse(
    val success: Boolean,
    val companies: List<NewCompany>,
    val totalCount: Int,
    val message: String? = null
)

data class Broker(
    val id: String,
    val name: String
)

data class Country(
    val id: String,
    val name: String
)

data class BrokersResponse(
    val success: Boolean,
    val brokers: List<Broker>
)

data class CountriesResponse(
    val success: Boolean,
    val countries: List<Country>
)