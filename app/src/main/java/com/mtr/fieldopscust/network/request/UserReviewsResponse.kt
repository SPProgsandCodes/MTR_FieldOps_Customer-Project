package com.mtr.fieldopscust.network.request

import kotlinx.serialization.Serializable

@Serializable
data class UserReviewsResponse(
    val result: List<Reviews>,
    val isSuccess: Boolean,
    val message: String
)


data class Reviews(
    val userId: Int,
    val reviews: List<Any>,
    val reviewerName: String,
    val reviewerPicture: String,
    val review: String,
    val rating: Int,
)