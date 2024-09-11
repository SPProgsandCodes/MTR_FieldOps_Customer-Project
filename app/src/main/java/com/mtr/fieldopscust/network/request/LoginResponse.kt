package com.mtr.fieldopsemp.network.request

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("result") val result: Result,
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("message") val message: String
)

data class Result(
    @SerializedName("token") val token: String,
    @SerializedName("user") val user: User
)

data class User(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("firstName") val firstName: String,
    @SerializedName("middleName") val middleName: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("phoneNumber") val phoneNumber: String,
    @SerializedName("email") val email: String,
    @SerializedName("role") val role: String,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("lastUpdatedAt") val lastUpdatedAt: String,
    @SerializedName("yearOfExperience") val yearOfExperience: Int,
    @SerializedName("rating") val rating: Int,
    @SerializedName("profileUrl") val profileUrl: String,
    @SerializedName("totalMoneySpent") val totalMoneySpent: Int,
    @SerializedName("servicesRequested") val servicesRequested: Int,
    @SerializedName("totalServiceDelivered") val totalServiceDelivered: Int,
    @SerializedName("domainId") val domainId: Int,
    @SerializedName("paymentMethod") val paymentMethod: String?,
    @SerializedName("isOnline") val isOnline: Boolean,
    @SerializedName("reviews") val reviews: List<Any> // Assuming reviews is a list of objects or empty list
)

