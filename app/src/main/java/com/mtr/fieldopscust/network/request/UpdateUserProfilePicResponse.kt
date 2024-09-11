package com.mtr.fieldopscust.network.request

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserProfilePicResponse(
    val result: SignupUser,
    val isSuccess: Boolean,
    val message: String
)