package com.mtr.fieldopscust.network.request

import com.google.gson.annotations.SerializedName
import retrofit2.http.Field

class SendMessageRequest (
    @SerializedName("sendTo")
    var sendTo: Int,
    @SerializedName("message")
    var message: String
)