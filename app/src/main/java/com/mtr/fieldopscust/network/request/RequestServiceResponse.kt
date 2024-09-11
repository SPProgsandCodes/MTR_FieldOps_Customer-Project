package com.mtr.fieldopscust.network.request

data class RequestServiceResponse(
    val isSuccess: Boolean,
    val message: String,
    val result: ServiceResponse
)

data class ServiceResponse(
    val address: String,
    val amount: Any,
    val assignedAt: Any,
    val assignedBy: Any,
    val assignedTime: Any,
    val assignedTo: Any,
    val categoryId: Int,
    val createdAt: String,
    val createdBy: Int,
    val currency: Any,
    val description: String,
    val documents: String,
    val domainId: Int,
    val id: Int,
    val isViewed: Any,
    val name: String,
    val paymentStatus: Any,
    val price: Int,
    val status: String,
    val taskCategory: Any,
    val updatedAt: String,
    val updatedBy: Any,
    val userDetailsAssignedTo: Any,
    val userDetailsAssingedBy: Any,
    val userDetailsCreatedBy: Any,
    val userDetailsUpdatedBy: Any,
    val userDetailsViewedBy: Any,
    val viewStatus: Int,
    val viewedBy: Any,
    val viewedTime: Any,
    val workCompleteTime: Any,
    val workStartTime: Any
)