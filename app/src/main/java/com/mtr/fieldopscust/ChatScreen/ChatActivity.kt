package com.mtr.fieldopscust.ChatScreen

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide

import com.mtr.fieldopscust.R
import com.mtr.fieldopscust.Utils.ApplicationHelper
import com.mtr.fieldopscust.Utils.ApplicationHelper.hideSystemUI
import com.mtr.fieldopscust.Utils.Constants.Companion.DOMAIN_ID_KEY
import com.mtr.fieldopscust.Utils.Constants.Companion.INTENT_MESSAGE_SEND_TO_USER_ID
import com.mtr.fieldopscust.Utils.Constants.Companion.INTENT_MESSAGE_SEND_USER_FULL_NAME
import com.mtr.fieldopscust.Utils.Constants.Companion.LOGIN_PREFS
import com.mtr.fieldopscust.Utils.Constants.Companion.TOKEN_KEY
import com.mtr.fieldopscust.Utils.Constants.Companion.USER_ID_KEY
import com.mtr.fieldopscust.databinding.ActivityChatBinding
import com.mtr.fieldopscust.network.request.ResultMessageSended
import com.mtr.fieldopscust.network.request.SendMessageRequest
import com.mtr.fieldopscust.network.request.SendMessageResponse
import com.mtr.fieldopsemp.network.ApiClient
import com.mtr.fieldopsemp.network.ApiClientProxy
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

class ChatActivity : AppCompatActivity() {
    lateinit var binding: ActivityChatBinding
    var disposable: Disposable? = null
    private var token: String? = null
    private val messageList = mutableListOf<ResultMessageSended>()
    private lateinit var chatAdapter: ChatAdapter
    private var currentUserId: Int?= null
    private var DOMAIN_ID: Int?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(getLayoutInflater())
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.getRoot())
        hideSystemUI(this@ChatActivity)
        this.getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        coroutineScope.launch {
            delay(2000)
            hideSystemUI(this@ChatActivity)
        }

        val sharedPreferences = ApplicationHelper.getAppController()?.getSharedPreferences(
            LOGIN_PREFS, Context.MODE_PRIVATE)
        if (sharedPreferences != null) {
            token = sharedPreferences.getString(TOKEN_KEY, null)
            currentUserId = sharedPreferences.getInt(USER_ID_KEY, 0)
            DOMAIN_ID = sharedPreferences.getInt(DOMAIN_ID_KEY, 1)
            Log.d("TAG", "UserId for Chats: $token")
        }

        // Initialize the adapter
        chatAdapter = ChatAdapter(messageList, currentUserId!!)
        binding.rvChatMessages.layoutManager = LinearLayoutManager(this)
        binding.rvChatMessages.adapter = chatAdapter


        val sendTo = intent.getIntExtra(INTENT_MESSAGE_SEND_TO_USER_ID, 0)
        val sendToName = intent.getStringExtra(INTENT_MESSAGE_SEND_USER_FULL_NAME)
        val profileUrl = intent.getStringExtra("profile_url")
        Glide.with(this)
            .load(profileUrl)
            .placeholder(R.drawable.placeholder)
            .into(binding.imgProfilePicChatScreen)
        binding.txtProfileNameChats.text = sendToName
        binding.imageViewBackButtonChat.setOnClickListener{
            onClickBackButton()
        }
        binding.btnSendMessage.setOnClickListener{
            sendMessage(sendTo, DOMAIN_ID!!)
        }
//        sendMessage()

    }
    private fun onClickBackButton() {
        val fragmentManager: FragmentManager = supportFragmentManager

        if (fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStack()
        } else {
            this.onBackPressed()
        }
    }

    private fun sendMessage(sendTo: Int, domainId: Int) {

        val sampleMessage = binding.editTextInputMsg.text.toString()
//        val sendTo = 124 // Replace with the user ID you want to send the message to
        // Send the message to the server
        val bearerToken = "bearer $token"
        disposable = ApiClientProxy.sendMessage(sendTo, sampleMessage, bearerToken, domainId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { response ->
                    Log.d("API_RESPONSE", "Response: $response")
                    onSendMessageSuccess(response) },
                { error ->
                    Log.e("API_ERROR", "Error: ${error.message}", error)
                    onSendMessageError(error) }
            )

    }

    private fun onSendMessageSuccess(sendMessageResponse: SendMessageResponse?) {
        if (sendMessageResponse != null){
            if (sendMessageResponse.isSuccess){
                Toast.makeText(this, sendMessageResponse.message, Toast.LENGTH_SHORT).show()

                binding.rvChatMessages.layoutManager = LinearLayoutManager(this)

                    val messages : ResultMessageSended = sendMessageResponse.result
                    val updatedMessages = arrayListOf(messages)
                    chatAdapter.addMessage(messages) // Add the new message to the adapter

            }else{
                Toast.makeText(this, sendMessageResponse.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onSendMessageError(throwable: Throwable?) {
        if (throwable != null){
            Toast.makeText(this, "Error Sending Message", Toast.LENGTH_SHORT).show()
        }
    }
}