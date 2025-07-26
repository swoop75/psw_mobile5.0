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
    val status: String = "Pending" // "Pending", "Approved", "Rejected"
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

data class CompanyActionRequest(
    val companyId: String,
    val action: String // "approve" or "reject"
)

data class CompanyActionResponse(
    val success: Boolean,
    val message: String
)