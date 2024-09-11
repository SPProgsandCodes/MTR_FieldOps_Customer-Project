package com.mtr.fieldopscust.network.request

data class NotificationResponse (
    val result: List<Notifications>,
    val isSuccess: Boolean,
    val message: String
)

data class Notifications(
    val id: Int,
    val userId: Int,
    val taskId: Int,
    val subject: String,
    val description: String,
    val notificationImage: String,
    val domainId: Int,
    val isRead: Boolean,
    val createdAt: String,
    val task: String,
    val user: SendByUser
)

data class SendByUser(
    val id: Int,
    val firstName: String,
    val middleName: String?,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val languageId: Int?,
    val serviceCategoryId: Int?,
    val stripeCustomerId: String?,
    val squareCustomerId: String?,
    val yearOfExperience: Int,
    val domainId: Int,
    val roleId: Int,
    val role: String?,
    val category: String?,
    val createdAt: String,
    val lastUpdatedAt: String,
    val password: String,
    val isOnline: Boolean,
    val isActive: Boolean,
    val profileUrl: String?,
    val resetToken: String?,
    val resetTokenExpiry: String?
)