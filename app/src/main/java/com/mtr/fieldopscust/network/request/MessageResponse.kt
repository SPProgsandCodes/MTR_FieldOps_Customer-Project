package com.mtr.fieldopscust.network.request

import com.google.gson.annotations.SerializedName

data class MessageResponse(
    @SerializedName("result") val result: MessagesResult,
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("message") val message: String
)

data class MessagesResult(
    @SerializedName("items") var items: List<MessageItem>,
    @SerializedName("totalCount") val totalCount: Int,
    @SerializedName("pageNumber") val pageNumber: Int,
    @SerializedName("pageSize") val pageSize: Int
)

data class MessageItem(
    @SerializedName("id") val id: Int,
    @SerializedName("sendBy") val sendBy: Int,
    @SerializedName("senderName") val senderName: String,
    @SerializedName("senderPicture") val senderPicture: String?,
    @SerializedName("sendTo") val sendTo: Int,
    @SerializedName("message") val message: String,
    @SerializedName("sendTime") val sendTime: String,
    @SerializedName("isReceived") val isReceived: Boolean
)

