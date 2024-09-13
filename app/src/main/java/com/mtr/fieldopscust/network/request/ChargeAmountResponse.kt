package com.mtr.fieldopscust.network.request

data class ChargeAmountResponse(
    val isSuccess: Boolean,
    val message: String,
    val result: Int
)