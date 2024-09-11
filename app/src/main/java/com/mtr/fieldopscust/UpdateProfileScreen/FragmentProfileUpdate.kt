package com.mtr.fieldopscust.UpdateProfileScreen

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.mtr.fieldopscust.NotificationFragment.FragmentNotification
import com.mtr.fieldopscust.R
import com.mtr.fieldopscust.RequestServiceScreen.FragmentRequestServicePage.Companion.PICK_FILE_REQUEST
import com.mtr.fieldopscust.Utils.ApplicationHelper
import com.mtr.fieldopscust.Utils.ApplicationHelper.getBitmapFromUri
import com.mtr.fieldopscust.Utils.ApplicationHelper.getFileFromUri
import com.mtr.fieldopscust.Utils.Constants.Companion.DOMAIN_ID_KEY
import com.mtr.fieldopscust.Utils.Constants.Companion.FILE_REQUEST_KEY
import com.mtr.fieldopscust.Utils.Constants.Companion.LOGIN_PREFS
import com.mtr.fieldopscust.Utils.Constants.Companion.TOKEN_KEY
import com.mtr.fieldopscust.Utils.Constants.Companion.USER_EMAIL
import com.mtr.fieldopscust.Utils.Constants.Companion.USER_FIRST_NAME
import com.mtr.fieldopscust.Utils.Constants.Companion.USER_GET_PROFILE_PIC
import com.mtr.fieldopscust.Utils.Constants.Companion.USER_ID_KEY
import com.mtr.fieldopscust.Utils.Constants.Companion.USER_LAST_NAME
import com.mtr.fieldopscust.Utils.Constants.Companion.USER_MIDDLE_NAME
import com.mtr.fieldopscust.Utils.Constants.Companion.USER_PASSWORD
import com.mtr.fieldopscust.Utils.Constants.Companion.USER_PHONE_NUMBER
import com.mtr.fieldopscust.Utils.SessionPreferences
import com.mtr.fieldopscust.databinding.FragmentProfileUpdateBinding
import com.mtr.fieldopscust.network.request.UpdateUserProfilePicResponse
import com.mtr.fieldopscust.network.request.UpdateUserProfileResponse
import com.mtr.fieldopscust.network.request.UploadFileResponse
import com.mtr.fieldopscust.network.request.UserResponse
import com.mtr.fieldopsemp.network.ApiClientProxy
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit


class FragmentProfileUpdate : Fragment() {
    lateinit var binding: FragmentProfileUpdateBinding
    private var sharedSesssionPrefs: SessionPreferences? = null
    private var profileUrl: String? = null
    private var userId: Int? = null
    private var profileUpdateDisposable: Disposable? = null
    private var token: String? = null
    private var fileKey: String? = null
    private lateinit var phoneNumber: String
    private lateinit var email: String
    private var firstNameUser: String? = null
    private var middleNameUser: String? = null
    private var lastNameUser: String? = null
    private var DOMAIN_ID: Int? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProfileUpdateBinding.inflate(inflater, container, false)

        activity?.getWindow()?.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )


        sharedSesssionPrefs = ApplicationHelper.getAppController()?.loginPrefs(LOGIN_PREFS)

        activity?.let {
            token = sharedSesssionPrefs?.getString(TOKEN_KEY, null)
            profileUrl = sharedSesssionPrefs?.getString(USER_GET_PROFILE_PIC, null)
            userId = sharedSesssionPrefs?.getInt(USER_ID_KEY, 0)

            phoneNumber = sharedSesssionPrefs?.getString(USER_PHONE_NUMBER, null).toString()
            email = sharedSesssionPrefs?.getString(USER_EMAIL, null).toString()

            DOMAIN_ID = sharedSesssionPrefs?.getInt(DOMAIN_ID_KEY, 1)
        }

        if (userId != null) {
            loadProfileImage(userId!!, token!!)
            fetchUserDetails(userId!!)
        } else {
            Glide.with(this)
                .load(R.drawable.menface)
                .into(binding.profileImageSettingsPage)

            Toast.makeText(requireContext(), "User ID Null", Toast.LENGTH_SHORT).show()
        }

        binding.imgViewEditProfilePhotoUpdatePage.setOnClickListener {
            openGalleryForFile()
        }

        binding.imgButtonAlertProfileUpdatePage.setOnClickListener{
            notificationButton()
        }

        if (email.isNotEmpty()){
            val emailEditable: Editable = Editable.Factory.getInstance().newEditable(email)
            binding.editTextUpdateProfileEmail.text = emailEditable
        }

        if (phoneNumber.isNotEmpty()){
            val phoneNumberEditable: Editable = Editable.Factory.getInstance().newEditable(phoneNumber)
            binding.editTextUpdateProfilePhoneNumber.text = phoneNumberEditable
        }

        getSharedPreferencesForProfileUpdate()
        if (firstNameUser?.isNotEmpty() == true){
            val firstNameEditable: Editable = Editable.Factory.getInstance().newEditable(firstNameUser)
            binding.editTextUpdateProfileFirstName.text = firstNameEditable
        }

        if (middleNameUser?.isNotEmpty() == true){
            val middleNameEditable: Editable = Editable.Factory.getInstance().newEditable(middleNameUser)
            binding.editTextUpdateProfileMiddleName.text = middleNameEditable
        }

        if (lastNameUser?.isNotEmpty() == true){
            val lastNameEditable: Editable = Editable.Factory.getInstance().newEditable(lastNameUser)
            binding.editTextUpdateProfileLastName.text = lastNameEditable
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageViewBackButton.setOnClickListener{

                val fragmentManager: FragmentManager = parentFragmentManager;

                if (fragmentManager.backStackEntryCount > 0) {
                    // Navigate to the previous fragment
                    fragmentManager.popBackStack();
                } else {
                    // If no fragments in the back stack, finish the activity or handle as needed
                    activity?.onBackPressed();
                }

        }
        binding.btnSaveChanges.setOnClickListener {
//            getSharedPreferencesForProfileUpdate()
            var firstName = binding.editTextUpdateProfileFirstName.text.toString()
            if (firstName.isEmpty()) {
                firstName = firstNameUser!!
            }
            var middleName = binding.editTextUpdateProfileMiddleName.text.toString()
            if (middleName.isEmpty()) {
                middleName = middleNameUser!!
            }
            var lastName = binding.editTextUpdateProfileLastName.text.toString()
            if (lastName.isEmpty()) {
                lastName = lastNameUser!!
            }
            val email = sharedSesssionPrefs?.getString(USER_EMAIL, null)

            val passwd = sharedSesssionPrefs?.getString(USER_PASSWORD, null)

            if (!fileKey.isNullOrEmpty()) {
                updateUserProfilePic(fileKey!!, token!!, DOMAIN_ID!!)
            }

            if (passwd != null && email != null) {

                if (firstName.isEmpty() && lastName.isEmpty() && middleName.isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        "Atleast one Field is Required",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {

                    Log.d("TAG", "First Name: $firstName")
                    Log.d("TAG", "Middle Name: $middleName")
                    Log.d("TAG", "Last Name: $lastName")
                    updateUserDetails(
                        DOMAIN_ID!!,
                        token!!,
                        firstName,
                        middleName,
                        lastName,
                        email,
                        passwd
                    )
                }
            }

        }


    }

    // For Fetching User Details
    private fun fetchUserDetails(userId: Int) {
        binding.progressBarProfileUpdate.visibility = View.VISIBLE
        val bearerToken = "bearer $token"
        profileUpdateDisposable = ApiClientProxy.getUserDetails(userId, bearerToken, DOMAIN_ID!!)
            .retryWhen { error ->
                error.zipWith(Observable.range(1, 3)) { error, retryCount ->
                    if (error is SocketTimeoutException && retryCount < 3) {
                        Observable.timer(
                            2,
                            TimeUnit.SECONDS
                        ) // Wait for 2 seconds before retrying
                    } else {
                        Observable.error<Throwable>(error)
                    }
                }.flatMap { it }
            }.subscribe(this::onUserDetailsSuccess, this::onUserDetailsError)
    }

    private fun onUserDetailsSuccess(userResponse: UserResponse) {
        binding.progressBarProfileUpdate.visibility = View.GONE
        if (userResponse.isSuccess) {
            sharedSesssionPrefs?.edit()
                ?.putString(USER_GET_PROFILE_PIC, userResponse.result.profileUrl)?.apply()
            sharedSesssionPrefs?.edit()
                ?.putString(USER_FIRST_NAME, userResponse.result.firstName)
                ?.apply()
            sharedSesssionPrefs?.edit()
                ?.putString(USER_MIDDLE_NAME, userResponse.result.middleName)
                ?.apply()
            sharedSesssionPrefs?.edit()
                ?.putString(USER_LAST_NAME, userResponse.result.lastName)
                ?.apply()
            getSharedPreferencesForProfileUpdate()

        } else {
            binding.progressBarProfileUpdate.visibility = View.GONE
            Toast.makeText(requireContext(), userResponse.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun getSharedPreferencesForProfileUpdate() {

        profileUrl = sharedSesssionPrefs?.getString(USER_GET_PROFILE_PIC, null)
        firstNameUser = sharedSesssionPrefs?.getString(USER_FIRST_NAME, null)
        middleNameUser = sharedSesssionPrefs?.getString(USER_MIDDLE_NAME, null)
        lastNameUser = sharedSesssionPrefs?.getString(USER_LAST_NAME, null)
    }

    private fun onUserDetailsError(throwable: Throwable?) {
        binding.progressBarProfileUpdate.visibility = View.GONE
        if (throwable != null) {
            Toast.makeText(requireContext(), "Error Fetching User Details", Toast.LENGTH_SHORT)
                .show()

        }
    }

    private fun openGalleryForFile() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "*/*"  // Use "image/*" if you want only images
        startActivityForResult(intent, PICK_FILE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_FILE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val fileUri = data.data
            fileUri?.let {
                val file = getFileFromUri(it, requireContext())
                file?.let { file ->
                    val filename = file.name
                    val bitmap = getBitmapFromUri(it, requireContext())
                    val token = "bearer $token"  // Replace with your token

                    uploadProfilePicture(bitmap, filename, token)

                }
            }
        }
    }

    private fun clearGlideCache() {
        Glide.get(requireContext()).clearMemory()
        Thread {
            Glide.get(requireContext()).clearDiskCache()
        }.start()
    }

    private fun loadProfileImage(userId: Int, token: String) {
        getSharedPreferencesForProfileUpdate()
        if (profileUrl != null && profileUrl == sharedSesssionPrefs?.getString(
                USER_GET_PROFILE_PIC,
                null
            )
        ) {
            binding.progressBarProfileUpdate.visibility = View.VISIBLE
            Glide.with(this)
                .load(profileUrl)
                .placeholder(R.drawable.placeholder)
                .into(binding.profileImageSettingsPage)
            binding.progressBarProfileUpdate.visibility = View.GONE
        } else {
            binding.progressBarProfileUpdate.visibility = View.VISIBLE
            val bearerToken = "bearer $token"
            profileUpdateDisposable = ApiClientProxy.getUserDetails(userId, bearerToken, DOMAIN_ID!!)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onProfileImageSuccess, this::onProfileImageFailure)
        }
    }

    private fun onProfileImageSuccess(userResponse: UserResponse?) {
        if (userResponse != null) {
            // Show the ProgressBar before starting the image load
            binding.progressBarProfileUpdate.visibility = View.GONE

            if (userResponse.result.profileUrl.isNullOrEmpty()){
                Glide.with(requireContext())
                    .load(R.drawable.menface)
                    .into(binding.profileImageSettingsPage)
            } else {
                Glide.with(this)
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
                            binding.progressBarProfileUpdate.visibility = View.GONE
                            return false
                        }


                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>,
                            isFirstResource: Boolean
                        ): Boolean {
                            // Hide the ProgressBar if the load fails
                            binding.progressBarProfileUpdate.visibility = View.GONE
                            binding.profileImageSettingsPage.setImageResource(R.drawable.menface)

                            return true
                        }


                    })
                    .into(binding.profileImageSettingsPage)

            }

        } else {
            binding.progressBarProfileUpdate.visibility = View.GONE
            Glide.with(this)
                .load(R.drawable.menface)
                .into(binding.profileImageSettingsPage)
            Toast.makeText(requireContext(), "No Profile Image Found", Toast.LENGTH_SHORT)
                .show()
        }

    }

    private fun onProfileImageFailure(throwable: Throwable?) {
        binding.progressBarProfileUpdate.visibility = View.GONE
        Glide.with(this)
            .load(R.drawable.menface)
            .into(binding.profileImageSettingsPage)
        Toast.makeText(requireContext(), "Failed to load image", Toast.LENGTH_SHORT).show()
    }


    private fun updateUserDetails(
        domainId: Int,
        token: String,
        firstName: String,
        middleName: String,
        lastName: String,
        email: String,
        passwd: String
    ) {
        val bearerToken = "bearer $token"
        binding.profileSettingsProgressBar.visibility = View.VISIBLE
        firstNameUser = firstName
        middleNameUser = middleName
        lastNameUser = lastName
        profileUpdateDisposable = ApiClientProxy.updateUserProfile(
            domainId,
            firstName,
            middleName,
            lastName,
            email,
            passwd,
            bearerToken
        )
            .observeOn(AndroidSchedulers.mainThread())
            .retryWhen { error ->
                error.zipWith(Observable.range(1, 5)) { error, retryCount ->
                    if ((error is SocketTimeoutException && retryCount < 3) || (error is UnknownHostException && retryCount < 3)) {
                        Observable.timer(
                            2,
                            TimeUnit.SECONDS
                        ) // Wait for 2 seconds before retrying
                    } else {
                        Observable.error<Throwable>(error)
                    }
                }.flatMap { it }
            }
            .subscribe(this::onUpdateProfileSuccess, this::onUpdateProfileFailure)

    }

    private fun onUpdateProfileSuccess(updateUserProfileResponse: UpdateUserProfileResponse) {
        if (updateUserProfileResponse.isSuccess) {
            binding.profileSettingsProgressBar.visibility = View.GONE
            Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT)
                .show()
            updateSharedPreferences(firstNameUser, middleNameUser, lastNameUser)
            val fragmentManager: FragmentManager = parentFragmentManager;

            if (fragmentManager.getBackStackEntryCount() > 0) {
                // Navigate to the previous fragment
                fragmentManager.popBackStack();
            } else {
                // If no fragments in the back stack, finish the activity or handle as needed
                activity?.onBackPressed();
            }


        } else {
            binding.profileSettingsProgressBar.visibility = View.GONE
            Toast.makeText(requireContext(), "Failed to update profile", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun updateSharedPreferences(
        firstNameUser: String?,
        middleNameUser: String?,
        lastNameUser: String?
    ) {
            sharedSesssionPrefs?.edit()?.putString(USER_FIRST_NAME, firstNameUser)?.apply()
            sharedSesssionPrefs?.edit()?.putString(USER_MIDDLE_NAME, middleNameUser)?.apply()
            sharedSesssionPrefs?.edit()?.putString(USER_LAST_NAME, lastNameUser)?.apply()
    }

    private fun onUpdateProfileFailure(throwable: Throwable?) {
        binding.profileSettingsProgressBar.visibility = View.GONE
        Toast.makeText(requireContext(), "Failed to update profile", Toast.LENGTH_SHORT).show()
    }

    private fun uploadProfilePicture(bitmap: Bitmap, filename: String, token: String) {
        clearGlideCache()
        getSharedPreferencesForProfileUpdate()
        binding.progressBarProfileUpdate.visibility = View.VISIBLE
        profileUpdateDisposable = ApiClientProxy.uploadFile(bitmap, filename, token, DOMAIN_ID!!)
            .observeOn(AndroidSchedulers.mainThread())
            .retryWhen { error ->
                error.zipWith(Observable.range(1, 5)) { error, retryCount ->
                    if ((error is SocketTimeoutException && retryCount < 3) || (error is UnknownHostException && retryCount < 3)) {
                        Observable.timer(
                            2,
                            TimeUnit.SECONDS
                        ) // Wait for 2 seconds before retrying
                    } else {
                        Observable.error<Throwable>(error)
                    }
                }.flatMap { it }
            }
            .subscribe(this::onImageUploadSuccess, this::onImageUploadFailure)
    }

    private fun onImageUploadSuccess(uploadFileResponse: UploadFileResponse?) {
        if (uploadFileResponse != null && uploadFileResponse.isSuccess) {
            binding.progressBarProfileUpdate.visibility = View.GONE
            Toast.makeText(requireContext(), "File Uploaded Successfully", Toast.LENGTH_SHORT)
                .show()
            fileKey = uploadFileResponse.result.key
            sharedSesssionPrefs?.edit()?.putString(FILE_REQUEST_KEY, fileKey)?.apply()
            val bearerToken = "bearer $token"
            sharedSesssionPrefs?.edit()
                ?.putString(USER_GET_PROFILE_PIC, uploadFileResponse.result.fileUrl)?.apply()
            if (userId != null) {
                loadProfileImage(userId!!, token!!)
            }


        } else {
            binding.progressBarProfileUpdate.visibility = View.GONE
            Toast.makeText(requireContext(), "Failed to Upload File", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onImageUploadFailure(throwable: Throwable?) {
        binding.progressBarProfileUpdate.visibility = View.GONE
        Toast.makeText(requireContext(), "Failed to Upload Image", Toast.LENGTH_SHORT).show()
    }

    private fun updateUserProfilePic(key: String, token: String, domainId: Int) {

        val bearerToken = "bearer $token"

        profileUpdateDisposable = ApiClientProxy.updateuserprofilepic(key, bearerToken, domainId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::onImageUpdateSuccess, this::onImageUpdateFailure)
    }

    private fun onImageUpdateSuccess(updateUserProfilePicResponse: UpdateUserProfilePicResponse) {
        if (updateUserProfilePicResponse.isSuccess) {
            Toast.makeText(
                requireContext(),
                updateUserProfilePicResponse.message,
                Toast.LENGTH_SHORT
            ).show()


            if (userId != null) {
                loadProfileImage(userId!!, token!!)
            }
        } else {
            Toast.makeText(
                requireContext(),
                updateUserProfilePicResponse.message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun onImageUpdateFailure(throwable: Throwable?) {
        Toast.makeText(requireContext(), "Failure in updating profile", Toast.LENGTH_SHORT)
            .show()
    }

    private fun notificationButton(){
        val fragmentManager: FragmentManager = parentFragmentManager
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, FragmentNotification())
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        profileUpdateDisposable?.dispose()

    }


}