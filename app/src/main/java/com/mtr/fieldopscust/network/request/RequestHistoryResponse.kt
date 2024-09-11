package com.mtr.fieldopscust.network.request

data class RequestHistoryResponse(
    val result: List<Job>,
    val isSuccess: Boolean,
    val message: String
)

data class Job(
    val id: Int,
    val name: String,
    val description: String,
    val status: String,
    val viewStatus: String,
    val price: Int,
    val documents: String?,
    val createdAt: String,
    val updatedAt: String,
    val isViewed: Boolean?,
    val address: String,
    val viewedTime: String?,
    val assignedTime: String?,
    val workStartTime: String?,
    val workCompleteTime: String?,
    val createdBy: User,
    val viewedBy: User,
    val updatedBy: User,
    val assignedTo: User,
    val assignedBy: User,
    val attachmentUrls: List<String>?,
    val categoryName: String
)

data class User(
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
    val profileUrl: String?,
    val totalMoneySpent: Int,
    val servicesRequested: Int,
    val totalServiceDelivered: Int,
    val paymentMethod: String,
    val isOnline: Boolean,
    val reviews: List<Reviews1> // Adjust the type if reviews are more complex
)

data class Reviews1(
    val userId: Int,
    val reviews: List<Any>,
    val reviewerName: String,
    val reviewerPicture: String,
    val review: String,
    val rating: Int,
)
