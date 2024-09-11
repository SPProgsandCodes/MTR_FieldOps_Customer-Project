package com.mtr.fieldopscust.network.request

import com.mtr.fieldopsemp.network.request.Result
import com.mtr.fieldopsemp.network.request.User
import kotlinx.serialization.Serializable

@Serializable
data class ForgetPassLinkResponse(
    val result: Result,
    val isSuccess: Boolean,
    val message: String
)

@Serializable
data class Result(
    val token: String,
    val user: User
)