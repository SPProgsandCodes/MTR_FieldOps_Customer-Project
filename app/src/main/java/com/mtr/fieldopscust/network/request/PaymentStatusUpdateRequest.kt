package com.mtr.fieldopscust.network.request

import com.google.gson.annotations.SerializedName

class PaymentStatusUpdateRequest {
    @SerializedName("paymentMethod")
    var paymentMethod: Int? = null
    @SerializedName("intent")
    var intent: String? = null
}