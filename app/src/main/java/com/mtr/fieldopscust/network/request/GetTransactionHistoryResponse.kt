package com.mtr.fieldopscust.network.request

data class GetTransactionHistoryResponse(
    val result: List<History>,
    val isSuccess: Boolean,
    val message: String
)

data class History(
    val id: Int,
    val serviceType: String,
    val serviceProvider: String,
    val amount: Int,
    val createdAt: String,
    val status: String
)
