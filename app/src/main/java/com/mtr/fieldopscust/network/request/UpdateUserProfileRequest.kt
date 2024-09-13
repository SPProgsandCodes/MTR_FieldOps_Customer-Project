package com.mtr.fieldopscust.network.request

import com.google.gson.annotations.SerializedName

class UpdateUserProfileRequest {
    @SerializedName("firstName")
    var firstName: String? = null
    @SerializedName("middleName")
    var middleName: String? = null
    @SerializedName("lastName")
    var lastName: String? = null
    @SerializedName("email")
    var email: String? = null
    @SerializedName("password")
    var password: String? = null
    @SerializedName("phoneNumber")
    var phoneNumber: String? = null
}