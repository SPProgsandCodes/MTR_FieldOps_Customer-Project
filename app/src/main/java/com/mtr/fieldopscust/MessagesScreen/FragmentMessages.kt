package com.mtr.fieldopscust.MessagesScreen

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.mtr.fieldopscust.ActivityDashboard
import com.mtr.fieldopscust.ChatScreen.ChatActivity
import com.mtr.fieldopscust.R
import com.mtr.fieldopscust.Utils.Constants.Companion.DOMAIN_ID_KEY
import com.mtr.fieldopscust.Utils.Constants.Companion.INTENT_MESSAGE_SEND_TO_USER_ID
import com.mtr.fieldopscust.Utils.Constants.Companion.INTENT_MESSAGE_SEND_USER_FULL_NAME
import com.mtr.fieldopscust.Utils.Constants.Companion.NO_NEW_MESSAGES
import com.mtr.fieldopscust.Utils.Constants.Companion.TOKEN_KEY
import com.mtr.fieldopscust.Utils.Constants.Companion.USER_ID_KEY
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
    private var messages : ArrayList<ChatUser>?=null
    private var userID: Int? = null
    private var DOMAIN_ID: Int? = null
    var disposable: Disposable? = null
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

        activity?.let {
            val sharedPreferences = it.getSharedPreferences(TOKEN_KEY, Context.MODE_PRIVATE)
            token = sharedPreferences.getString(TOKEN_KEY, null)
            userID = sharedPreferences.getInt(USER_ID_KEY, 0)
            DOMAIN_ID = sharedPreferences.getInt(DOMAIN_ID_KEY, 1)
        }
        getUserMessages()
//        messagesAdapter = AdapterMessages(messages, userID!!)

        return binding.getRoot()
    }

    private fun updateBottomNavigation() {
        activity?.let {
            val currentFragment = parentFragmentManager.findFragmentById(R.id.fragment_container)
            if (currentFragment is FragmentMessages) {
                // Update the bottom navigation here for the Messages fragment
                // Assuming you have an Activity method to handle this
                (it as? ActivityDashboard)?.updateBottomNavigation(currentFragment)
            }
        }
    }

    private fun getUserMessages() {
        val bearerToken = "bearer $token"
        binding.progressBarMessagesScreen.visibility = View.VISIBLE
        binding.loadingDetailsTxtMessagesScreen.visibility = View.VISIBLE
        try {
            disposable = ApiClientProxy.getMessagesByUserId(bearerToken, DOMAIN_ID!!)
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
                if (isAdded){
                    Toast.makeText(requireContext(), getMessageResponse.message, Toast.LENGTH_SHORT).show()
                }
                binding.progressBarMessagesScreen.visibility = View.GONE
                binding.loadingDetailsTxtMessagesScreen.visibility = View.GONE

                binding.rvMessages.layoutManager = LinearLayoutManager(requireContext())
                if (messages == null) {
                    if (getMessageResponse.result.isEmpty()){
                        binding.progressBarMessagesScreen.visibility = View.GONE
                        binding.loadingDetailsTxtMessagesScreen.text = NO_NEW_MESSAGES
                        binding.loadingDetailsTxtMessagesScreen.visibility = View.VISIBLE
                        return
                    } else {
                        messages = ArrayList(getMessageResponse.result)
                        messagesAdapter = AdapterUserMessages(messages!!, userID!!, this, binding.loadingDetailsTxtMessagesScreen)
                        binding.rvMessages.adapter = messagesAdapter
                    }


                } else {
                    // Determine the start position for new items
                    val previousSize = messages!!.size
                    // Add new messages to the existing list
                    messages!!.addAll(getMessageResponse.result)
                    // Notify the adapter of the new items inserted
                    messagesAdapter.notifyItemRangeInserted(previousSize, getMessageResponse.result.size)

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
        val intent = Intent(requireContext(), ChatActivity::class.java).apply{
            putExtra(INTENT_MESSAGE_SEND_TO_USER_ID, message.userName)
            putExtra("profile_url", message.profilePicture)
            Log.d("TAG", "Message Send To ${message.userName}")
            Log.d("TAG", "Message Send By ${message.userName}")
//            Log.d("TAG", "Message Send To User Name ${message.userDetailsSendTo.firstName}")
//            Log.d("TAG", "Message Send By User Name ${message.userDetailsSendBy.firstName}")
            putExtra(INTENT_MESSAGE_SEND_USER_FULL_NAME, message.userName)
        }

        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
        if (disposable != null) {
            disposable!!.dispose()
        }

        if (messages != null) {
            messages!!.clear()
        }


    }


}

