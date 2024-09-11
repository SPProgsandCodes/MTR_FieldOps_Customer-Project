package com.mtr.fieldopscust.network.request

import com.google.gson.annotations.SerializedName

data class WalletBalanceResponse(
    @SerializedName("result")
    val result: Int,       // Represents the balance amount

    @SerializedName("isSuccess")
    val isSuccess: Boolean, // Indicates if the request was successful

    @SerializedName("message")
    val message: String     // Any additional message
)
