package com.mtr.fieldopscust.network.request

import com.mtr.fieldopsemp.network.request.Result
import com.mtr.fieldopsemp.network.request.User
import kotlinx.serialization.Serializable

@Serializable
data class SignupResponse(
    val result: SignupUser,
    val isSuccess: Boolean,
    val message: String
)

@Serializable
data class Result1(
    val token: String,
    val user: User
)

@Serializable
data class SignupUser(
    val id: Int,
    val name: String,
    val firstName: String,
    val middleName: String,
    val lastName: String,
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
    val reviews: List<Any> // Replace 'Any' with the actual type of review if available
)