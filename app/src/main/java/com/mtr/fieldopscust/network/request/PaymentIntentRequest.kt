package com.mtr.fieldopscust.network.request

import com.google.gson.annotations.SerializedName

class PaymentIntentRequest {
    @SerializedName("amount")
    var amount: Int? = null
    @SerializedName("currency")
    var currency: String? = null

}