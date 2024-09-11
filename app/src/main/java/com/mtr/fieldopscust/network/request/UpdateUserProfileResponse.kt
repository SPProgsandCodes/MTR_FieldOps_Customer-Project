package com.mtr.fieldopscust.network.request

data class UpdateUserProfileResponse(
    val isSuccess: Boolean,
    val message: String,
    val result: Any
)