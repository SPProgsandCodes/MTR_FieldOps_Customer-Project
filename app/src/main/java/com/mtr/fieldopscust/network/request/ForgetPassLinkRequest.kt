package com.mtr.fieldopscust.network.request

import com.google.gson.annotations.SerializedName

class ForgetPassLinkRequest {
    @SerializedName("email")
    var link: String? = null
}