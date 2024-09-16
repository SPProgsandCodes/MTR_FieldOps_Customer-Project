package com.mtr.fieldopsemp.network

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import android.webkit.MimeTypeMap
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.google.gson.JsonObject
import com.mtr.fieldopscust.DashboardScreen.ModelUserDashboard
import com.mtr.fieldopscust.Utils.ApplicationHelper.compressBitmap
import com.mtr.fieldopscust.Utils.ApplicationHelper.resizeBitmap
import com.mtr.fieldopscust.network.request.ChargeAmountRequest
import com.mtr.fieldopscust.network.request.ChargeAmountResponse
import com.mtr.fieldopscust.network.request.ForgetPassLinkRequest
import com.mtr.fieldopscust.network.request.ForgetPassLinkResponse
import com.mtr.fieldopscust.network.request.GetFileResponse
import com.mtr.fieldopscust.network.request.GetMessageResponse
import com.mtr.fieldopscust.network.request.GetTransactionHistoryResponse
import com.mtr.fieldopscust.network.request.MessageResponse
import com.mtr.fieldopscust.network.request.NotificationResponse
import com.mtr.fieldopscust.network.request.PaymentIntentRequest
import com.mtr.fieldopscust.network.request.PaymentIntentResponse
import com.mtr.fieldopscust.network.request.PaymentStatusUpdateRequest
import com.mtr.fieldopscust.network.request.PaymentUpdateResponse
import com.mtr.fieldopscust.network.request.RequestHistoryResponse
import com.mtr.fieldopscust.network.request.RequestServiceRequest
import com.mtr.fieldopscust.network.request.RequestServiceResponse
import com.mtr.fieldopscust.network.request.SendMessageRequest
import com.mtr.fieldopscust.network.request.SignupRequest
import com.mtr.fieldopscust.network.request.SignupResponse
import com.mtr.fieldopscust.network.request.UpdateUserProfilePicResponse
import com.mtr.fieldopscust.network.request.UpdateUserProfileRequest
import com.mtr.fieldopscust.network.request.UpdateUserProfileResponse
import com.mtr.fieldopscust.network.request.UploadFileResponse
import com.mtr.fieldopscust.network.request.UserResponse
import com.mtr.fieldopscust.network.request.UserReviewsResponse
import com.mtr.fieldopscust.network.request.WalletBalanceResponse
import com.mtr.fieldopsemp.network.request.LoginResponse
import com.mtr.smartpos.network.request.LoginRequest
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File


object ApiClientProxy {

    private fun getEncodedImage(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream)
        return "data:image/jpeg;base64," + Base64.encodeToString(
            stream.toByteArray(),
            Base64.DEFAULT
        )
    }

    fun login(email: String, password: String): Observable<LoginResponse> {
        val loginRequest = LoginRequest()
        loginRequest.userName = email
        loginRequest.password = password
        return ApiClient.getServiceClient().login(loginRequest)
            .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
    }

    fun signup(
        email: String,
        firstName: String,
        middleName: String,
        lastName: String,
        phoneNumber: String,
        domainId: Int,
        password: String,
        confirmPassword: String
    ): Observable<SignupResponse> {
        val signupRequest = SignupRequest()
        signupRequest.email = email
        signupRequest.firstName = firstName
        signupRequest.lastName = lastName
        signupRequest.phone_number = phoneNumber
        signupRequest.password = password
        signupRequest.confirm_password = confirmPassword
        signupRequest.role_id = 2
        signupRequest.years_of_exp = 0
        return ApiClient.getServiceClient().register(email, firstName, middleName,lastName, phoneNumber, domainId, password, confirmPassword, 2, 0)
            .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
    }

    fun forgetpasslink(forgetPassLinkRequestVar: String): Observable<ForgetPassLinkResponse> {
        val forgetPassLinkRequest = ForgetPassLinkRequest()
        forgetPassLinkRequest.link = forgetPassLinkRequestVar
        return ApiClient.getServiceClient().forgetPassLink(forgetPassLinkRequestVar)
            .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
    }

    fun getUserDashboard(token: String, domainId: Int): Observable<ModelUserDashboard> {
        return ApiClient.getServiceClient().getUserDashboard(domainId,token)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getUserDetails(userId: Int, token: String, domainId: Int): Observable<UserResponse> {
        return ApiClient.getServiceClient().getUserDetails( userId,domainId, token)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getUserReviews(userId: Int, token: String, domainId: Int): Observable<UserReviewsResponse> {
        return ApiClient.getServiceClient().getUserReviews(userId, domainId,  token)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getMessagesByUserId(token: String, domainId: Int): Observable<GetMessageResponse> {
        return ApiClient.getServiceClient().getMessagesByUserId(domainId,  token)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun uploadFile(file: Any, filename: String, token: String, domainId: Int): Observable<UploadFileResponse> {
        val multipartBody: MultipartBody.Part

        when (file) {
            is Bitmap -> {
                // Handle Bitmap: Resize and compress
                val resizedBitmap = resizeBitmap(file, 800, 800)
                val compressedBitmapData = compressBitmap(resizedBitmap, 70)
                val requestBody = compressedBitmapData.toRequestBody("image/jpeg".toMediaTypeOrNull())
                multipartBody = MultipartBody.Part.createFormData("file", "$filename.jpg", requestBody)
            }

            is File -> {
                // Handle File: Prepare RequestBody with the appropriate MIME type
                val mimeType = getMimeType(file) ?: "*/*"
                val requestBody = file.asRequestBody(mimeType.toMediaTypeOrNull())
                multipartBody = MultipartBody.Part.createFormData("file", file.name, requestBody)
            }

            is MultipartBody.Part -> {
                // Handle MultipartBody.Part directly
                multipartBody = file
            }

            else -> {
                Log.e("UploadFile", "Unsupported file type: ${file::class.java.name}")
                throw IllegalArgumentException("Unsupported file type: ${file::class.java.name}")
            }
        }

        // Prepare the filename part as text/plain
        val filenamePart = filename.toRequestBody("text/plain".toMediaTypeOrNull())
        val tokenBody = token.toRequestBody("text/plain".toMediaTypeOrNull())

        // Call the API to upload the file
        return ApiClient.getServiceClient().uploadFile(multipartBody, filenamePart, domainId, token)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { Log.d("UploadFile", "Uploading file: $filename") }
            .doOnTerminate { Log.d("UploadFile", "Upload completed for file: $filename") }
            .doOnError { e -> Log.e("UploadFile", "Upload failed for file: $filename", e) }
    }

    // Helper function to determine MIME type of a file
    private fun getMimeType(file: File): String {
        val extension = file.extension.toLowerCase()
        return when (extension) {
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "pdf" -> "application/pdf"
            "doc", "docx" -> "application/msword"
            else -> "*/*"  // Default to a generic MIME type
        }
    }

    // Helper function to convert Bitmap to ByteArray
    private fun compressBitmap(bitmap: Bitmap, quality: Int): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }

    // Helper function to resize Bitmap
    private fun resizeBitmap(bitmap: Bitmap, width: Int, height: Int): Bitmap {
        return Bitmap.createScaledBitmap(bitmap, width, height, true)
    }

    fun getFileUrl(keyRequest: String, token: String, domainId: Int): Observable<GetFileResponse>{
        return ApiClient.getServiceClient().getFileUrl(keyRequest,domainId,"d", token)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun updateuserprofilepic(key: String, token: String, domainId: Int): Observable<UpdateUserProfilePicResponse>{
        return ApiClient.getServiceClient().updateUserProfilePic(key,domainId, "d", token)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getRequestHistory(userId: Int, taskStatus: Int, token: String, domainId: Int): Observable<RequestHistoryResponse>{
        return ApiClient.getServiceClient().getRequestHistory(userId, taskStatus, domainId, token)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    }

    fun getBookingHistory(userId: Int, taskStatus: Int, token: String, domainId: Int): Observable<RequestHistoryResponse>{
        return ApiClient.getServiceClient().getRequestHistory(userId, taskStatus,domainId, token)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    }

    fun getTransactionHistory(domainId: Int, token: String): Observable<GetTransactionHistoryResponse>{
        return ApiClient.getServiceClient().getTransactionHistory(domainId, token)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun updateUserProfile(domainId: Int, firstName: String, middleName: String, lastName: String, email: String, password: String, phoneNumber: String, token: String): Observable<UpdateUserProfileResponse>{
        var updateUserProfileRequest: UpdateUserProfileRequest = UpdateUserProfileRequest()
        updateUserProfileRequest.firstName = firstName
        updateUserProfileRequest.middleName = middleName
        updateUserProfileRequest.lastName = lastName
        updateUserProfileRequest.email = email
        updateUserProfileRequest.password = password
        updateUserProfileRequest.phoneNumber = phoneNumber


        return ApiClient.getServiceClient().updateUserProfile(domainId, updateUserProfileRequest, token)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun requestService(domainId: Int, name: String, description: String, price: Int, categoryId: Int, documents: List<String>, token: String): Observable<RequestServiceResponse>{
        val requestServiceRequest = RequestServiceRequest()
        requestServiceRequest.serviceTitle = name
        requestServiceRequest.serviceDescription = description
        requestServiceRequest.price = price
        requestServiceRequest.categoryId = categoryId
        requestServiceRequest.documents = documents
        requestServiceRequest.address = ""

        return ApiClient.getServiceClient().requestService(domainId, requestServiceRequest, token)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    }

    fun getAllNotifications(domainId: Int, token: String): Observable<NotificationResponse>{
        return ApiClient.getServiceClient().getAllNotifications(domainId, token)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun createPaymentIntent(domainId: Int, token: String, amount: Int, currency: String): Observable<PaymentIntentResponse> {
        val paymentIntentRequest = PaymentIntentRequest()
        paymentIntentRequest.amount = amount
        paymentIntentRequest.currency = currency

        return ApiClient.getServiceClient().createPaymentIntent(domainId, token, paymentIntentRequest)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun updatePaymentStatus(domainId: Int, token: String, paymentMethod: Int,intent: String): Observable<PaymentUpdateResponse>{
        val paymentStatusUpdateRequest = PaymentStatusUpdateRequest()
        paymentStatusUpdateRequest.paymentMethod = paymentMethod
        paymentStatusUpdateRequest.intent = intent
        return ApiClient.getServiceClient().updatePaymentStatus(domainId, token, paymentStatusUpdateRequest)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getWalletBalance(domainId: Int, currency: String, token: String): Observable<WalletBalanceResponse>{
        return ApiClient.getServiceClient().getWalletBalance(domainId, currency, token)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun chargeAmount(taskId: Int, amountCharged: Int, currency: String, token: String, domainId: Int): Observable<ChargeAmountResponse>{
        val chargeAmountRequest = ChargeAmountRequest()
        chargeAmountRequest.taskId = taskId
        chargeAmountRequest.amountCharged = amountCharged
        chargeAmountRequest.currency = currency
        return ApiClient.getServiceClient().chargeAmount(domainId, token, chargeAmountRequest)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getchathistorywithuser(
        userId: Int,
        pageNumber: Int,
        pageSize: Int,
        token: String,
        domainId: Int
    ): Observable<MessageResponse> {
        return ApiClient.getServiceClient()
            .getchathistorywithuser(userId, pageNumber, pageSize, domainId, token)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
    }
}


