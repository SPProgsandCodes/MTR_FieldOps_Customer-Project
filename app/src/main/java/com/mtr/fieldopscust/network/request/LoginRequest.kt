package com.mtr.smartpos.network.request

import com.google.gson.annotations.SerializedName

class LoginRequest {
    @SerializedName("userName")
    var userName: String? = null
    @SerializedName("password")
    var password: String? = null
}