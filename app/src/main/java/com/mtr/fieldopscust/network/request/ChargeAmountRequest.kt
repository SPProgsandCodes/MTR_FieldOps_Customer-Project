package com.mtr.fieldopscust.network.request

import com.google.gson.annotations.SerializedName

class ChargeAmountRequest {
    @SerializedName("taskId")
    var taskId: Int? = null
    @SerializedName("amount")
    var amountCharged: Int? = null
    @SerializedName("currency")
    var currency: String? = null

}