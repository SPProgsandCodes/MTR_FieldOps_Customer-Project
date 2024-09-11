package com.mtr.fieldopscust.network.request

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val result: Result2,
    val isSuccess: Boolean,
    val message: String
)

@Serializable
data class Result2(
    val id: Int,
    val name: String,
    val firstName: String,
    val middleName: String,
    val lastName: String,
    val phoneNumber: String,
    val email: String,
    val role: String,
    val createdAt: String,
    val lastUpdatedAt: String,
    val yearOfExperience: Int,
    val rating: Int,
    val profileUrl: String,
    val totalMoneySpent: Int,
    val servicesRequested: Int,
    val totalServiceDelivered: Int,
    val paymentMethod: String,
    val isOnline: Boolean,
    val reviews: List<String>
)
