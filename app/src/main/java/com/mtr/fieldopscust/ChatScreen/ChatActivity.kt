package com.mtr.fieldopscust.ChatScreen

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.mtr.fieldopscust.R
import com.mtr.fieldopscust.Utils.ApplicationHelper
import com.mtr.fieldopscust.Utils.Constants.Companion.DOMAIN_ID_KEY
import com.mtr.fieldopscust.Utils.Constants.Companion.INTENT_MESSAGE_SEND_TO_USER_ID
import com.mtr.fieldopscust.Utils.Constants.Companion.INTENT_MESSAGE_SEND_USER_FULL_NAME
import com.mtr.fieldopscust.Utils.Constants.Companion.LOGIN_PREFS
import com.mtr.fieldopscust.Utils.Constants.Companion.TOKEN_KEY
import com.mtr.fieldopscust.Utils.Constants.Companion.USER_ID_KEY
import com.mtr.fieldopscust.Utils.Constants.Companion.USER_PHONE_NUMBER
import com.mtr.fieldopscust.Utils.GlobalProgressBar
import com.mtr.fieldopscust.databinding.ActivityChatBinding
import com.mtr.fieldopscust.network.request.MessageItem
import com.mtr.fieldopscust.network.request.MessageResponse
import com.mtr.fieldopsemp.network.ApiClientProxy
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit


class ChatActivity : AppCompatActivity() {
    lateinit var binding: ActivityChatBinding
    private var token: String? = null
    private var currentUserId: Int? = null
    private var DOMAIN_ID: Int? = null
    private var phoneNumber: String? = null
    private var disposable: Disposable? = null
    private lateinit var chatAdapter: MessageAdapter
    private var messageList = mutableListOf<MessageItem>()
    private val pageSize = 20
    private var currentPage = 1
    private var isLoading = false
    private var otherUserId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR //  set status text dark

        window.statusBarColor =
            ContextCompat.getColor(
                this@ChatActivity,
                R.color.white
            ) // set status background white
        binding = ActivityChatBinding.inflate(layoutInflater)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.getRoot())

        val sharedPreferences = ApplicationHelper.getAppController()?.getSharedPreferences(
            LOGIN_PREFS, Context.MODE_PRIVATE
        )
        if (sharedPreferences != null) {
            token = sharedPreferences.getString(TOKEN_KEY, null)
            currentUserId = sharedPreferences.getInt(USER_ID_KEY, 0)
            DOMAIN_ID = sharedPreferences.getInt(DOMAIN_ID_KEY, 1)
            phoneNumber = sharedPreferences.getString(USER_PHONE_NUMBER, null)
            Log.d("TAG", "UserId for Chats: $token")
        }

         otherUserId = intent.getIntExtra(INTENT_MESSAGE_SEND_TO_USER_ID, 0)
        val sendToName = intent.getStringExtra(INTENT_MESSAGE_SEND_USER_FULL_NAME)
        val profileUrl = intent.getStringExtra("profile_url")

        Glide.with(this)
            .load(profileUrl)
            .placeholder(R.drawable.placeholder)
            .into(binding.imgProfilePicChatScreen)

        binding.txtProfileNameChats.text = sendToName

        binding.imageViewBackButtonChat.setOnClickListener {
            onClickBackButton()
        }

        binding.btnSendMessage.setOnClickListener {
        }

        binding.imgButtonCallPerson.setOnClickListener {

            if (phoneNumber.isNullOrEmpty()){
                Toast.makeText(this, "Phone Number Not Found", Toast.LENGTH_SHORT).show()
            } else{
                val phoneNumber = phoneNumber.toString()
                // Create an intent to open the dialer app with the phone number pre-filled
                val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:$phoneNumber") // Pre-fill the phone number
                }
                // Start the dialer activity
                startActivity(dialIntent)
            }



        }


        val layoutManager =
            LinearLayoutManager(this@ChatActivity, LinearLayoutManager.VERTICAL, false)
        binding.rvChatMessages.layoutManager = layoutManager

        chatAdapter = MessageAdapter(messageList, currentUserId!!)
        binding.rvChatMessages.adapter = chatAdapter
        binding.rvChatMessages.setHasFixedSize(true)

        fetchChatHistory(currentPage)

    }

    private fun onClickBackButton() {
        val fragmentManager: FragmentManager = supportFragmentManager

        if (fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStack()
        } else {
            this.onBackPressed()
        }
    }

    private fun fetchChatHistory(currentPage: Int) {
        GlobalProgressBar.show(this@ChatActivity)
        disposable = ApiClientProxy.getchathistorywithuser(
            otherUserId!!,
            currentPage,
            pageSize,
            "Bearer " + token,
            DOMAIN_ID!!
        )
            .retryWhen { error ->
                error.zipWith(Observable.range(1, 3)) { error, retryCount ->
                    if (error is SocketTimeoutException && retryCount < 3) {
                        Observable.timer(2, TimeUnit.SECONDS) // Wait for 2 seconds before retrying
                    } else {
                        Observable.error<Throwable>(error)
                    }
                }.flatMap { it }
            }.subscribe(this::onMessageSuccess, this::onErrorMessages)
    }

    private fun onErrorMessages(throwable: Throwable?) {
        GlobalProgressBar.dismiss()
        isLoading = false
        if (throwable != null) {
            Toast.makeText(this, "Error: " + throwable.message.toString(), Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun onMessageSuccess(messageResponse: MessageResponse?) {
        GlobalProgressBar.dismiss()
        if (messageResponse!!.isSuccess) {

            val newItems = messageResponse.result.items.toMutableList()
            messageList.addAll(newItems)  //= messageResponse.result.items.toMutableList()//getAllMessages(messageResponse, currentUserId!!)
            binding.rvChatMessages.scrollToPosition(messageList.size - 1)
        }
    }

}