package com.mtr.fieldopscust.MessagesScreen

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.mtr.fieldopscust.ActivityDashboard
import com.mtr.fieldopscust.ChatScreen.ChatActivity
import com.mtr.fieldopscust.LoginScreen.ActivityLogin
import com.mtr.fieldopscust.NetworkUtil
import com.mtr.fieldopscust.R
import com.mtr.fieldopscust.Utils.ApplicationHelper
import com.mtr.fieldopscust.Utils.Constants.Companion.DOMAIN_ID_KEY
import com.mtr.fieldopscust.Utils.Constants.Companion.INTENT_MESSAGE_SEND_TO_USER_ID
import com.mtr.fieldopscust.Utils.Constants.Companion.INTENT_MESSAGE_SEND_USER_FULL_NAME
import com.mtr.fieldopscust.Utils.Constants.Companion.IS_LOGIN
import com.mtr.fieldopscust.Utils.Constants.Companion.LOGIN_PREFS
import com.mtr.fieldopscust.Utils.Constants.Companion.NO_NEW_MESSAGES
import com.mtr.fieldopscust.Utils.Constants.Companion.TOKEN_KEY
import com.mtr.fieldopscust.Utils.Constants.Companion.USER_ID_KEY
import com.mtr.fieldopscust.Utils.SessionPreferences
import com.mtr.fieldopscust.databinding.FragmentMessagesBinding
import com.mtr.fieldopscust.network.request.ChatUser
import com.mtr.fieldopscust.network.request.GetMessageResponse
import com.mtr.fieldopsemp.network.ApiClientProxy
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.exceptions.CompositeException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

class FragmentMessages : Fragment(), AdapterUserMessages.OnMessageClickListener {
    lateinit var binding: FragmentMessagesBinding
    private lateinit var messagesAdapter: AdapterUserMessages
    private var token: String? = null
    private var messages: ArrayList<ChatUser>? = null
    private var userID: Int? = null
    private var DOMAIN_ID: Int? = null
    private var messagesDisposable: Disposable? = null
    private var sharedSesssionPrefs: SessionPreferences? = null
    private val compositeDisposable = CompositeDisposable()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentMessagesBinding.inflate(inflater, container, false)
        sharedSesssionPrefs = ApplicationHelper.getAppController()?.loginPrefs(LOGIN_PREFS)
        activity?.let {
            val sharedPreferences = it.getSharedPreferences(TOKEN_KEY, Context.MODE_PRIVATE)
            token = sharedPreferences.getString(TOKEN_KEY, null)
            userID = sharedPreferences.getInt(USER_ID_KEY, 0)
            DOMAIN_ID = sharedPreferences.getInt(DOMAIN_ID_KEY, 1)
        }

        getUserMessages()

        return binding.getRoot()
    }


    // Method to check Internet Connection
    private fun checkNetworkConnection(): Boolean {
        return NetworkUtil().isNetworkAvailable(context)
    }

    private fun dialogNoInternet() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.no_internet_dialog)
        val btnRetry = dialog.findViewById<Button>(R.id.btnRetry)
        btnRetry.setOnClickListener {
            logOutApp()
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun logOutApp() {

        sharedSesssionPrefs?.edit()?.putBoolean(IS_LOGIN, false)?.apply()
        val intent = Intent(activity, ActivityLogin::class.java)
        startActivity(intent)
        if (isAdded) {
            Toast.makeText(requireContext(), "Logout Successfully", Toast.LENGTH_SHORT).show()
        }


    }

    private fun getUserMessages() {
        val bearerToken = "bearer $token"
        binding.progressBarMessagesScreen.visibility = View.VISIBLE
        binding.loadingDetailsTxtMessagesScreen.visibility = View.VISIBLE
        try {
            messagesDisposable = ApiClientProxy.getMessagesByUserId(bearerToken, DOMAIN_ID!!)
                .retryWhen { errors ->
                    errors.zipWith(Observable.range(1, 3)) { error, retryCount ->
                        when {
                            error is SocketTimeoutException && retryCount < 3 -> {
                                Log.e("Retry", "SocketTimeoutException - retrying... ($retryCount)")
                                Observable.timer(2, TimeUnit.SECONDS)
                            }

                            retryCount < 3 -> {
                                Log.e("Retry", "Exception - retrying... ($retryCount)")
                                Observable.timer(2, TimeUnit.SECONDS)
                            }

                            else -> {
                                Log.e("Retry", "Exception - giving up after 3 attempts", error)
                                Observable.error<Throwable>(error)
                            }
                        }
                    }.flatMap { it }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response -> onUserMessagesSuccess(response) },
                    { error -> onUserMessagesError(error) }
                )

        } catch (e: CompositeException) {
            e.printStackTrace()
        }


    }

    private fun onUserMessagesError(throwable: Throwable?) {
        if (throwable != null) {
            binding.progressBarMessagesScreen.visibility = View.GONE
            binding.loadingDetailsTxtMessagesScreen.text = NO_NEW_MESSAGES
            binding.loadingDetailsTxtMessagesScreen.visibility = View.VISIBLE
            Toast.makeText(requireContext(), "Error Fetching Messages", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onUserMessagesSuccess(getMessageResponse: GetMessageResponse?) {
        if (getMessageResponse != null) {
            if (getMessageResponse.isSuccess) {
                if (isAdded) {
                   // Toast.makeText(requireContext(), getMessageResponse.message, Toast.LENGTH_SHORT).show()
                }
                binding.progressBarMessagesScreen.visibility = View.GONE
                binding.loadingDetailsTxtMessagesScreen.visibility = View.GONE

                binding.rvMessages.layoutManager = LinearLayoutManager(requireContext())
                if (messages == null) {
                    if (getMessageResponse.result.isEmpty()) {
                        binding.progressBarMessagesScreen.visibility = View.GONE
                        binding.loadingDetailsTxtMessagesScreen.text = NO_NEW_MESSAGES
                        binding.loadingDetailsTxtMessagesScreen.visibility = View.VISIBLE
                        return
                    } else {
                        messages = ArrayList(getMessageResponse.result)
                        messagesAdapter = AdapterUserMessages(
                            messages!!,
                            this,
                            binding.loadingDetailsTxtMessagesScreen
                        )
                        binding.rvMessages.adapter = messagesAdapter
                    }


                } else {
                    // Determine the start position for new items
                    val previousSize = messages!!.size
                    // Add new messages to the existing list
                    messages!!.addAll(getMessageResponse.result)
                    // Notify the adapter of the new items inserted
                    messagesAdapter.notifyItemRangeInserted(
                        previousSize,
                        getMessageResponse.result.size
                    )

                }
            } else {
                if (isAdded) {
                    binding.progressBarMessagesScreen.visibility = View.GONE
                    binding.loadingDetailsTxtMessagesScreen.text = NO_NEW_MESSAGES
                    binding.loadingDetailsTxtMessagesScreen.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), getMessageResponse.message, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    override fun onMessageClick(message: ChatUser) {
        if (checkNetworkConnection()) {
            val intent = Intent(requireContext(), ChatActivity::class.java).apply {
                putExtra(INTENT_MESSAGE_SEND_TO_USER_ID, message.userId)
                putExtra("profile_url", message.profilePicture)
                putExtra(INTENT_MESSAGE_SEND_USER_FULL_NAME, message.userName)
                Log.d("TAG", "Message Send To ${message.userName}")
            }
            startActivity(intent)
        } else {
            dialogNoInternet()
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
        if (messagesDisposable != null) {
            messagesDisposable!!.dispose()
        }

        if (messages != null) {
            messages!!.clear()
        }


    }


}

