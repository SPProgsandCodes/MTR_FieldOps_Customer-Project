package com.mtr.fieldopscust.network.request

data class GetMessageResponse(
    val result: List<ChatUser>,
    val isSuccess: Boolean,
    val message: String
)

data class ChatUser(
    val userId: Int,
    val userName: String,
    val profilePicture: String?,
    val unreadMessageCount: Int,
    val lastMessage: LastMessage
)

data class LastMessage(
    val id: Int,
    val message: String,
    val sendTime: String,
    val sendBy: Int
)
