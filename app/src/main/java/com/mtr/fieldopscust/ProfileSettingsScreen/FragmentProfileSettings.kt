package com.mtr.fieldopscust.ProfileSettingsScreen


import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.mtr.fieldopscust.LoginScreen.ActivityLogin
import com.mtr.fieldopscust.NetworkUtil
import com.mtr.fieldopscust.NotificationFragment.FragmentNotification
import com.mtr.fieldopscust.R
import com.mtr.fieldopscust.UpdateProfileScreen.FragmentProfileUpdate
import com.mtr.fieldopscust.Utils.ApplicationHelper
import com.mtr.fieldopscust.Utils.Constants.Companion.DOMAIN_ID_KEY
import com.mtr.fieldopscust.Utils.Constants.Companion.IS_LOGIN
import com.mtr.fieldopscust.Utils.Constants.Companion.LOGIN_PREFS
import com.mtr.fieldopscust.Utils.Constants.Companion.TOKEN_KEY
import com.mtr.fieldopscust.Utils.Constants.Companion.USER_EMAIL
import com.mtr.fieldopscust.Utils.Constants.Companion.USER_FIRST_NAME
import com.mtr.fieldopscust.Utils.Constants.Companion.USER_GET_PROFILE_PIC
import com.mtr.fieldopscust.Utils.Constants.Companion.USER_ID_KEY
import com.mtr.fieldopscust.Utils.Constants.Companion.USER_LAST_NAME
import com.mtr.fieldopscust.Utils.Constants.Companion.USER_MIDDLE_NAME
import com.mtr.fieldopscust.Utils.Constants.Companion.USER_PHONE_NUMBER
import com.mtr.fieldopscust.Utils.SessionPreferences
import com.mtr.fieldopscust.databinding.FragmentProfileSettingsBinding
import com.mtr.fieldopscust.network.request.UserResponse
import com.mtr.fieldopsemp.network.ApiClientProxy
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit


class FragmentProfileSettings : Fragment() {
    lateinit var binding: FragmentProfileSettingsBinding
    private var profileSettingsDisposable: Disposable? = null
    private var sharedSesssionPrefs: SessionPreferences? = null
    private var profileUrl: String? = null
    private var phoneNumber: String? = null
    private var userId: Int? = null
    private var token: String? = null
    private var firstName: String?= null
    private var lastName: String?=null
    private var email: String ?= null
    private var DOMAIN_ID: Int ?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//         Inflate the layout for this fragment
        binding = FragmentProfileSettingsBinding.inflate(inflater, container, false)
        sharedSesssionPrefs = ApplicationHelper.getAppController()?.loginPrefs(LOGIN_PREFS)

        activity?.let {
            token = sharedSesssionPrefs?.getString(TOKEN_KEY, null)
            profileUrl = sharedSesssionPrefs?.getString(USER_GET_PROFILE_PIC, null)
            firstName = sharedSesssionPrefs?.getString(USER_FIRST_NAME, null)
            lastName = sharedSesssionPrefs?.getString(USER_LAST_NAME, null)
            phoneNumber = sharedSesssionPrefs?.getString(USER_PHONE_NUMBER, null)
            email = sharedSesssionPrefs?.getString(USER_EMAIL, null)
            userId = sharedSesssionPrefs?.getInt(USER_ID_KEY, 0)
            DOMAIN_ID = sharedSesssionPrefs?.getInt(DOMAIN_ID_KEY, 1)
            Log.d("ProfileUrl", "onCreateView: $profileUrl")
            Log.d("UserId", "Value of UserID: $userId")

        }

        binding.imgButtonAlertReqSerPg.setOnClickListener {
            if (checkNetworkConnection()){
                notificationButton()
            } else {
                dialogNoInternet()
            }

        }
        binding.layoutSignoutButton.setOnClickListener{
            showSignOutPopup()
        }


        if (userId != null) {
            if (checkNetworkConnection()){
                if (firstName.isNullOrEmpty() && lastName.isNullOrEmpty()){
//                fetchUserDetails(userId!!)
                    clearGlideCache()
                    loadProfileImage(userId!!, token!!)
                } else {
                    clearGlideCache()
                    binding.textProfileNameSettings.text = "$firstName $lastName"
                    binding.michelleRi.text = email
                    if (profileUrl.isNullOrEmpty()){
                        Glide.with(requireContext())
                            .load(R.drawable.menface)
                            .into(binding.imgProfileImageProfilePage)
                    } else {
                        Glide.with(requireContext())
                            .load(profileUrl)
                            .placeholder(R.drawable.menface)
                            .into(binding.imgProfileImageProfilePage)
                    }

                }
                fetchUserDetails(userId!!)
            } else {
                dialogNoInternet()
            }

        } else {
            Glide.with(requireContext())
                .load(R.drawable.menface)
                .into(binding.imgProfileImageProfilePage)
        }


        return binding.getRoot()

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imgSettingsGearIcon.setOnClickListener(View.OnClickListener {
            val fragmentManager: FragmentManager = parentFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

            fragmentTransaction.replace(R.id.fragment_container, FragmentProfileUpdate())

            fragmentTransaction.addToBackStack(null)

            fragmentTransaction.commit()
        })

        binding.imgViewEditProfilePhoto.setOnClickListener {
            val fragmentManager: FragmentManager = parentFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

            fragmentTransaction.replace(R.id.fragment_container, FragmentProfileUpdate())

            fragmentTransaction.addToBackStack(null)

            fragmentTransaction.commit()
        }

    }

    private fun fetchUserDetails(userId: Int) {
        binding.progressBarProfileSettings.visibility = View.VISIBLE
        binding.txtLoadingDetails.visibility = View.VISIBLE
        val bearerToken = "bearer $token"
        profileSettingsDisposable = ApiClientProxy.getUserDetails(userId, bearerToken, DOMAIN_ID!!)
            .retryWhen { error ->
                error.zipWith(Observable.range(1, 3)) { error, retryCount ->
                    if (error is SocketTimeoutException && retryCount < 3) {
                        Observable.timer(2, TimeUnit.SECONDS) // Wait for 2 seconds before retrying
                    } else {
                        Observable.error<Throwable>(error)
                    }
                }.flatMap { it }
            }.subscribe(this::onUserDetailsSuccess, this::onUserDetailsError)
    }

    private fun onUserDetailsSuccess(userResponse: UserResponse) {
        binding.progressBarProfileSettings.visibility = View.GONE
        binding.txtLoadingDetails.visibility = View.GONE
        if (userResponse.isSuccess) {
            sharedSesssionPrefs?.edit()
                ?.putString(USER_GET_PROFILE_PIC, userResponse.result.profileUrl)?.apply()
            sharedSesssionPrefs?.edit()?.putString(USER_FIRST_NAME, userResponse.result.firstName)
                ?.apply()
            sharedSesssionPrefs?.edit()?.putString(USER_MIDDLE_NAME, userResponse.result.middleName)
                ?.apply()
            sharedSesssionPrefs?.edit()?.putString(USER_LAST_NAME, userResponse.result.lastName)
                ?.apply()
            sharedSesssionPrefs?.edit()?.putString(USER_MIDDLE_NAME, userResponse.result.middleName)
                ?.apply()
            sharedSesssionPrefs?.edit()?.putString(USER_EMAIL, userResponse.result.email)
                ?.apply()
            sharedSesssionPrefs?.edit()?.putString(USER_PHONE_NUMBER, userResponse.result.phoneNumber)
                ?.apply()
            getSharedPreferencesForProfileUpdate()

        } else {

            Toast.makeText(requireContext(), userResponse.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun getSharedPreferencesForProfileUpdate() {

        profileUrl = sharedSesssionPrefs?.getString(USER_GET_PROFILE_PIC, null)
        firstName = sharedSesssionPrefs?.getString(USER_FIRST_NAME, null)
        lastName = sharedSesssionPrefs?.getString(USER_LAST_NAME, null)
        email = sharedSesssionPrefs?.getString(USER_EMAIL, null)
        phoneNumber = sharedSesssionPrefs?.getString(USER_PHONE_NUMBER, null)


    }

    private fun onUserDetailsError(throwable: Throwable?) {
        binding.progressBarProfileSettings.visibility = View.GONE
        binding.txtLoadingDetails.visibility = View.GONE
        if (throwable != null) {
            Toast.makeText(requireContext(), "Error Fetching User Details", Toast.LENGTH_SHORT).show()

        }
    }

    private fun loadProfileImage(userId: Int, token: String) {
        getSharedPreferencesForProfileUpdate()
        binding.progressBarProfileSettings.visibility = View.VISIBLE
        binding.txtLoadingDetails.visibility = View.VISIBLE
        if (profileUrl!=null && profileUrl == sharedSesssionPrefs?.getString(USER_GET_PROFILE_PIC, null)){
            Glide.with(requireContext())
                .load(profileUrl)
                .placeholder(R.drawable.menface)
                .into(binding.imgProfileImageProfilePage)
            binding.textProfileNameSettings.text = "$firstName $lastName"
            binding.michelleRi.text = email


            binding.progressBarProfileSettings.visibility = View.GONE
            binding.txtLoadingDetails.visibility = View.GONE
        } else {
            binding.progressBarProfileSettings.visibility = View.GONE
            binding.txtLoadingDetails.visibility = View.GONE
            val bearerToken = "bearer $token"
            profileSettingsDisposable = ApiClientProxy.getUserDetails(userId, bearerToken, DOMAIN_ID!!)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onProfileImageSuccess, this::onProfileImageFailure)
        }
    }

    private fun onProfileImageSuccess(userResponse: UserResponse?) {
        if (userResponse != null) {
            // Show the ProgressBar before starting the image load
            binding.progressBarProfileSettings.visibility = View.VISIBLE
            binding.txtLoadingDetails.visibility = View.VISIBLE
            if (isAdded) {
                Glide.with(requireContext())
                    .load(userResponse.result.profileUrl)
                    .listener(object : RequestListener<Drawable> {

                        override fun onResourceReady(
                            resource: Drawable,
                            model: Any,
                            target: com.bumptech.glide.request.target.Target<Drawable>?,
                            dataSource: DataSource,
                            isFirstResource: Boolean
                        ): Boolean {
                            // Hide the ProgressBar when the image is ready
                            binding.progressBarProfileSettings.visibility = View.GONE
                            binding.txtLoadingDetails.visibility = View.GONE
                            return false
                        }


                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>,
                            isFirstResource: Boolean
                        ): Boolean {
                            // Hide the ProgressBar if the load fails
                            binding.progressBarProfileSettings.visibility = View.GONE
                            binding.txtLoadingDetails.visibility = View.GONE
                            binding.imgProfileImageProfilePage.setImageResource(R.drawable.menface)

                            return true
                        }


                    })
                    .into(binding.imgProfileImageProfilePage)
            } else {
                binding.imgProfileImageProfilePage.setImageResource(R.drawable.menface)
                Toast.makeText(requireContext(), "No Profile Image Found", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    }

    private fun onProfileImageFailure(throwable: Throwable?) {
        if (isAdded) {
            binding.progressBarProfileSettings.visibility = View.GONE
            binding.txtLoadingDetails.visibility = View.GONE
            Glide.with(requireContext())
                .load(R.drawable.menface)
                .into(binding.imgProfileImageProfilePage)
            Toast.makeText(requireContext(), "Failed to load image", Toast.LENGTH_SHORT).show()
        }

    }



    private fun clearGlideCache() {
        Glide.get(requireContext()).clearMemory()
        Thread {
            Glide.get(requireContext()).clearDiskCache()
        }.start()
    }


    private fun notificationButton(){
        val fragmentManager: FragmentManager = parentFragmentManager
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, FragmentNotification())
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun showSignOutPopup() {
        val dialog = Dialog(requireActivity(),android.R.style.Theme_Material_Dialog_Alert)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.log_out_dialog)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)

        dialog.findViewById<RelativeLayout>(R.id.btnOkay)?.setOnClickListener {
            logOutApp()
            dialog.dismiss()
        }

        dialog.findViewById<RelativeLayout>(R.id.btnCancel)?.setOnClickListener {
            dialog.dismiss()
        }
        // Show the dialog
        dialog.show()
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
//            if (checkNetworkConnection()) {
//                getUserDashboard()
//                fetchUserDetails(sharedViewModel.userID)
//                fetchUserReviews(sharedViewModel.userID)
//            } else {
//                dialogNoInternet()
//            }
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
    override fun onDestroy() {
        super.onDestroy()
        profileSettingsDisposable?.dispose()

    }


}