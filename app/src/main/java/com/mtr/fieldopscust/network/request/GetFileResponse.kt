package com.mtr.fieldopscust.network.request

data class GetFileResponse(
    val result: String,
    val isSuccess: Boolean,
    val message: String
)