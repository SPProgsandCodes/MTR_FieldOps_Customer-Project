package com.mtr.fieldopscust.NotificationFragment

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.mtr.fieldopscust.LoginScreen.ActivityLogin
import com.mtr.fieldopscust.NetworkUtil
import com.mtr.fieldopscust.R
import com.mtr.fieldopscust.Utils.Constants.Companion.DOMAIN_ID_KEY
import com.mtr.fieldopscust.Utils.Constants.Companion.IS_LOGIN
import com.mtr.fieldopscust.Utils.Constants.Companion.NO_NEW_MESSAGES
import com.mtr.fieldopscust.Utils.Constants.Companion.NO_NEW_NOTIFICATIONS
import com.mtr.fieldopscust.Utils.Constants.Companion.TOKEN_KEY
import com.mtr.fieldopscust.Utils.Constants.Companion.USER_ID_KEY
import com.mtr.fieldopscust.databinding.FragmentNotificationBinding
import com.mtr.fieldopscust.network.request.NotificationResponse
import com.mtr.fieldopscust.network.request.Notifications
import com.mtr.fieldopsemp.network.ApiClientProxy
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

class FragmentNotification : Fragment() {
    private lateinit var fragmentNotificationBinding: FragmentNotificationBinding
    private var token: String? = null
    private var userID: Int? = null
    private var DOMAIN_ID: Int? = null
    private var notificationsList: ArrayList<Notifications>? = null
    private var notificationDisposable: Disposable? = null
    private var notificationAdapter: NotificationAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentNotificationBinding =
            FragmentNotificationBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        activity?.let {
            val sharedPreferences = it.getSharedPreferences(TOKEN_KEY, Context.MODE_PRIVATE)
            token = sharedPreferences.getString(TOKEN_KEY, null)
            userID = sharedPreferences.getInt(USER_ID_KEY, 0)
            DOMAIN_ID = sharedPreferences?.getInt(DOMAIN_ID_KEY, 1)
        }
        fetchNotifications()

        return fragmentNotificationBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentNotificationBinding.imageViewBackButtonNotificationScreen.setOnClickListener {
            val fragmentManager: FragmentManager = parentFragmentManager;

            if (fragmentManager.backStackEntryCount > 0) {
                // Navigate to the previous fragment
                fragmentManager.popBackStack();
            } else {
                // If no fragments in the back stack, finish the activity or handle as needed
                activity?.onBackPressed();
            }
        }


    }



    private fun fetchNotifications() {
        val bearerToken = "bearer $token"
        fragmentNotificationBinding.progressBarNotificationScreen.visibility = View.VISIBLE
        fragmentNotificationBinding.loadingDetailsTxtNotificationScreen.visibility = View.VISIBLE
        notificationDisposable = ApiClientProxy.getAllNotifications(DOMAIN_ID!!, bearerToken)
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
            }.observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::onNotificationSuccess, this::onNotificationError)
    }

    private fun onNotificationSuccess(notificationResponse: NotificationResponse?) {
        if (notificationResponse != null) {

            fragmentNotificationBinding.progressBarNotificationScreen.visibility = View.GONE
            fragmentNotificationBinding.loadingDetailsTxtNotificationScreen.visibility = View.GONE
            fragmentNotificationBinding.rvNotifications.layoutManager =
                LinearLayoutManager(requireContext())
            if (notificationsList == null) {
                if (notificationResponse.result.isEmpty()) {
                    fragmentNotificationBinding.progressBarNotificationScreen.visibility =
                        View.GONE
                    fragmentNotificationBinding.loadingDetailsTxtNotificationScreen.text =
                        NO_NEW_MESSAGES
                    fragmentNotificationBinding.loadingDetailsTxtNotificationScreen.visibility =
                        View.VISIBLE
                    Toast.makeText(requireContext(), "Notification List Empty", Toast.LENGTH_SHORT)
                        .show()
                    return
                } else {
                    notificationsList = ArrayList(notificationResponse.result)
                    notificationAdapter = NotificationAdapter(notificationsList!!)
                    fragmentNotificationBinding.rvNotifications.adapter = notificationAdapter
                }
            } else {
                // Determine the start position for new items
                val previousSize = notificationsList!!.size
                // Add new messages to the existing list
                notificationsList!!.addAll(notificationResponse.result)
                // Notify the adapter of the new items inserted
                notificationAdapter?.notifyItemRangeInserted(
                    previousSize,
                    notificationResponse.result.size
                )

            }


            Toast.makeText(
                requireContext(),
                "Notification Fetched Successfully",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            if (isAdded) {
                fragmentNotificationBinding.progressBarNotificationScreen.visibility = View.GONE
                fragmentNotificationBinding.loadingDetailsTxtNotificationScreen.text =
                    NO_NEW_NOTIFICATIONS
                fragmentNotificationBinding.loadingDetailsTxtNotificationScreen.visibility =
                    View.VISIBLE
            }

        }
    }

    private fun onNotificationError(throwable: Throwable?) {
        Toast.makeText(requireContext(), "Error Fetching Notifications", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (notificationDisposable != null) {
            notificationDisposable!!.dispose()
        }

        if (notificationsList != null) {
            notificationsList!!.clear()
        }


    }


}