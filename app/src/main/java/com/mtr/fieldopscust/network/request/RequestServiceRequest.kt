package com.mtr.fieldopscust.network.request

import com.google.gson.annotations.SerializedName

class RequestServiceRequest{
    @SerializedName("name")
    var serviceTitle: String? = null
    @SerializedName("description")
    var serviceDescription: String? = null
    @SerializedName("price")
    var price: Int? = 0
    @SerializedName("categoryId")
    var categoryId: Int? = 0
    @SerializedName("address")
    var address: String?=null
    @SerializedName("documents")
    var documents: List<String>? = null
}
