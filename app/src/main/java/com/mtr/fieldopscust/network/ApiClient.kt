package com.mtr.fieldopsemp.network

import android.telecom.Call
import android.util.Log
import com.google.gson.JsonObject
import com.mtr.fieldopscust.DashboardScreen.ModelCategories
import com.mtr.fieldopscust.DashboardScreen.ModelUserDashboard
import com.mtr.fieldopscust.network.request.ForgetPassLinkRequest
import com.mtr.fieldopscust.network.request.ForgetPassLinkResponse
import com.mtr.fieldopscust.network.request.GetFileResponse
import com.mtr.fieldopscust.network.request.GetMessageResponse
import com.mtr.fieldopscust.network.request.GetTransactionHistoryResponse
import com.mtr.fieldopscust.network.request.NotificationResponse
import com.mtr.fieldopscust.network.request.PaymentIntentRequest
import com.mtr.fieldopscust.network.request.PaymentIntentResponse
import com.mtr.fieldopscust.network.request.PaymentStatusUpdateRequest
import com.mtr.fieldopscust.network.request.PaymentUpdateResponse
import com.mtr.fieldopscust.network.request.RequestHistoryResponse
import com.mtr.fieldopscust.network.request.RequestServiceRequest
import com.mtr.fieldopscust.network.request.RequestServiceResponse
import com.mtr.fieldopscust.network.request.SendMessageRequest
import com.mtr.fieldopscust.network.request.SendMessageResponse
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
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

object ApiClient {

    private const val BASE_URL_PROD = "https://api2-fieldo.gettaxiusa.com/api/"

    private var mInterface: ApiService? = null

    fun getServiceClient(): ApiService {
        if (mInterface == null) {
            val baseUrl = BASE_URL_PROD
            val loggingInterceptor = HttpLoggingInterceptor(
                HttpLoggingInterceptor.Logger { message -> Log.i("ApiClient", message) })

            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            val restAdapter = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(OkHttpClient.Builder().addInterceptor(loggingInterceptor).build())
                .build()

            mInterface = restAdapter.create(ApiService::class.java)
        }

        return mInterface!!
    }

    interface ApiService {
        @Headers("Content-type: application/json")
        @POST("Authenticate/login")
        fun login(@Body loginRequest: LoginRequest): Observable<LoginResponse>


        @FormUrlEncoded
        @POST("Authenticate/register")
        fun register(
            @Field("Email") email: String,
            @Field("FirstName") firstName: String,
            @Field("MiddleName") middleName: String,
            @Field("LastName") lastName: String,
            @Field("PhoneNumber") phoneNumber: String,
            @Field("DomainId") domainId: Int,
            @Field("Password") password: String,
            @Field("ConfirmPassword") confirmPassword: String,
            @Field("RoleId") roleId: Int,
            @Field("YearOfExperience") yearOfExperience: Int
        ): Observable<SignupResponse>


//        @FormUrlEncoded()
//        @Headers("Content-type: application/json")
//        @POST("Authenticate/forgetpasslink")
//        fun forgetPassLink(@Body forgetPassLinkRequest: ForgetPassLinkRequest): Observable<ForgetPassLinkResponse>

        @POST("Authenticate/forgetpasslink")
        fun forgetPassLink(
            @Query("email") email: String
        ): Observable<ForgetPassLinkResponse>


        @GET("Message/getchatuserlist")
        fun getMessagesByUserId(
            @Query("domainId") domainId: Int,
            @Header("Authorization") token: String
        ): Observable<GetMessageResponse>

        @GET("User/getuserdashboard")
        fun getUserDashboard(
            @Query("domainId") domainId: Int,
            @Header("Authorization") token: String
        ): Observable<ModelUserDashboard>

        @GET("User/GetUserByUserId/{userId}")
        fun getUserDetails(
            @Path("userId") userId: Int,
            @Query("domainId") domainId: Int,

            @Header("Authorization") token: String
        ): Observable<UserResponse>

        @GET("User/GetUserReviewsByUserId/{userId}")
        fun getUserReviews(
            @Path("userId") userId: Int,
            @Query("domainId") domainId: Int,
            @Header("Authorization") token: String
        ): Observable<UserReviewsResponse>


        @POST("Message/SendMessage")
        fun sendMessage(
            @Query("sendTo") sendTo: Int,
            @Query("message") message: String,
            @Query("domainId") domainId: Int,
            @Header("Authorization") token: String
        ) : Observable<SendMessageResponse>

        @Multipart
        @POST("File/uploadfile")
        fun uploadFile(
            @Part file: MultipartBody.Part,
            @Query("filename") filename: RequestBody,
            @Query("domainId") domainId: Int,
            @Header("Authorization") token: String
        ): Observable<UploadFileResponse>


        @FormUrlEncoded
        @POST("File/getfileurl")
        fun getFileUrl(
            @Query("key") key: String,
            @Query("domainId") domainId: Int,
            @Field("temp") trans: String,
            @Header("Authorization") token: String
        ): Observable<GetFileResponse>


        @FormUrlEncoded
        @POST("User/updateuserprofilepic")
        fun updateUserProfilePic(
            @Query("profilePicKey") key: String,
            @Query("domainId") domainId: Int,
            @Field("temp") trans: String,
            @Header("Authorization") token: String
        ): Observable<UpdateUserProfilePicResponse>

        @GET("Task/GetUserTasksByUserIdAndByStatus")
        fun getRequestHistory(
            @Query("userId") userId: Int,
            @Query("taskStatus") taskStatus: Int,
            @Query("domainId") domainId: Int,
            @Header("Authorization") token: String
        ): Observable<RequestHistoryResponse>

        @GET("Payment/getmytransactionhostory")
        fun getTransactionHistory(
            @Query("domainId") domainId: Int,
            @Header("Authorization") token: String
        ): Observable<GetTransactionHistoryResponse>

        @Headers("Content-type: application/json")
        @POST("User/UpdateUserProfile")
        fun updateUserProfile(
            @Query("domainId") domainId: Int,
            @Body updateUserProfileRequest: UpdateUserProfileRequest,
            @Header("Authorization") token: String
        ): Observable<UpdateUserProfileResponse>

        @Headers("Content-type: application/json")
        @POST("Task/CreateTask")
        fun requestService(
            @Query("domainId") domainId: Int,
            @Body requestServiceRequest: RequestServiceRequest,
            @Header("Authorization") token: String
        ) : Observable<RequestServiceResponse>

        @GET("User/getallnotifications")
        fun getAllNotifications(
            @Query("domainId") domainId: Int,
            @Header("Authorization") token: String
        ): Observable<NotificationResponse>

        @POST("Payment/createpaymentintent")
        fun createPaymentIntent(
            @Query("domainId") domainId: Int,
            @Header("Authorization") token: String,
            @Body paymentIntentRequest: PaymentIntentRequest
        ): Observable<PaymentIntentResponse>

        @POST("Payment/updatepaymentstatus")
        fun updatePaymentStatus(
            @Query("domainId") domainId: Int,
            @Header("Authorization") token: String,
            @Body paymentStatusUpdateRequest: PaymentStatusUpdateRequest
        ): Observable<PaymentUpdateResponse>

        @GET("Wallet/getwalletbalance")
        fun getWalletBalance(
            @Query("domainId") domainId: Int,
            @Query("currency") currency: String,
            @Header("Authorization") token: String
        ): Observable<WalletBalanceResponse>
    }
}