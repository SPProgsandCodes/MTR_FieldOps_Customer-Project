package com.mtr.fieldopscust.DashboardScreen

data class ModelUserDashboard(
    val result: Result,
    val isSuccess: Boolean,
    val message: String
)

data class Result(
    val numberOfServicesRequested: Int,
    val totalMoneySpent: Int,
    val reviews: List<Any>,
    val categories: List<Category>,
    val averageRating: Int
)

data class Category(
    val id: Int,
    val name: String,
    val icon: String,
    val createdBy: Int,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String?,
    val requestCategoryCreatedBy: Any?
)