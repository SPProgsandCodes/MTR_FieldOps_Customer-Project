package com.mtr.fieldopscust.network.request

data class UploadFileResponse(
    val result: UploadFileResult,
    val isSuccess: Boolean,
    val message: String
)

data class UploadFileResult(
    val key: String,
    val fileUrl: String
)
