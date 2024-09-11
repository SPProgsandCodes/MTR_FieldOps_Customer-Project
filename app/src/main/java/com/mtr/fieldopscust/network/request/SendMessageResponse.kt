package com.mtr.fieldopscust.network.request

data class SendMessageResponse (
    val isSuccess: Boolean,
    val message: String,
    val result: ResultMessageSended
)

data class ResultMessageSended(
    val id: Int,
    val sendBy: Int,
    val sendTo: Int,
    val message: String,
    val sendTime: String,
    val isReceived: Boolean,
    val updatedAt: String
)