package com.mtr.fieldopscust.network.request

import com.google.gson.annotations.SerializedName

class SignupRequest {
    @SerializedName("Email")
    var email: String? = null
    @SerializedName("FirstName")
    var firstName: String? = null
    @SerializedName("LastName")
    var lastName: String? = null
    @SerializedName("PhoneNumber")
    var phone_number: String? = null
    @SerializedName("Password")
    var password: String? = null
    @SerializedName("ConfirmPassword")
    var confirm_password: String? = null
    @SerializedName("RoleId")
    var role_id: Int? = null
    @SerializedName("YearOfExperience")
    var years_of_exp: Int? = null
}