package com.mtr.fieldopscust.SignupScreen

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.hbb20.CountryCodePicker
import com.mtr.fieldopscust.LoginScreen.ActivityLogin
import com.mtr.fieldopscust.NetworkUtil
import com.mtr.fieldopscust.R
import com.mtr.fieldopscust.Utils.ApplicationHelper
import com.mtr.fieldopscust.Utils.ApplicationHelper.hideSystemUI
import com.mtr.fieldopscust.Utils.Constants.Companion.DOMAIN_ID_KEY
import com.mtr.fieldopscust.Utils.Constants.Companion.LOGIN_PREFS
import com.mtr.fieldopscust.Utils.Constants.Companion.USER_EMAIL
import com.mtr.fieldopscust.Utils.Constants.Companion.USER_FIRST_NAME
import com.mtr.fieldopscust.Utils.Constants.Companion.USER_LAST_NAME
import com.mtr.fieldopscust.Utils.Constants.Companion.USER_MIDDLE_NAME
import com.mtr.fieldopscust.Utils.Constants.Companion.USER_PASSWORD
import com.mtr.fieldopscust.Utils.SessionPreferences
import com.mtr.fieldopscust.databinding.ActivitySignupScreenBinding
import com.mtr.fieldopscust.network.request.SignupResponse
import com.mtr.fieldopsemp.network.ApiClientProxy
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

class ActivitySignupScreen : AppCompatActivity() {
    private lateinit var binding: ActivitySignupScreenBinding
    private var dialogProgressBar: ProgressBar? = null
    private var signupDisposable: Disposable? = null
    private var visibilityFlag = false
    private var countryCodePicker: CountryCodePicker? = null
    private var sharedSesssionPrefs: SessionPreferences? = null
    private var confirmPassword: String? = null
    private var DOMAIN_ID: Int? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupScreenBinding.inflate(getLayoutInflater())
        this.enableEdgeToEdge()
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(binding.getRoot())
        hideSystemUI(this@ActivitySignupScreen)
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        coroutineScope.launch {
            delay(2000)
            hideSystemUI(this@ActivitySignupScreen)
        }
        sharedSesssionPrefs = ApplicationHelper.getAppController()?.loginPrefs(LOGIN_PREFS)
        DOMAIN_ID = sharedSesssionPrefs?.getInt(DOMAIN_ID_KEY, 1)
        dialogProgressBar = findViewById(R.id.signupProgressBar)
        countryCodePicker = findViewById(R.id.country_code)

        countryCodePicker?.registerCarrierNumberEditText(binding.editTextSignupPhoneNumber)
        countryCodePicker?.setNumberAutoFormattingEnabled(true)

        binding.txtCountryCode.text = countryCodePicker?.selectedCountryCodeWithPlus

        countryCodePicker?.setOnCountryChangeListener {
            binding.txtCountryCode.text = countryCodePicker?.selectedCountryCodeWithPlus
        }


        binding.txtLogin.setOnClickListener{
            val intent: Intent = Intent(this, ActivityLogin::class.java)
            startActivity(intent)
        }

        binding.btnSignUp.setOnClickListener(View.OnClickListener {

            val email: String = binding.editTextSignupEmail.text.toString()
            val firstName: String = binding.editTextSignupFirstName.text.toString()
            val middleName: String = binding.editTextSignupMiddleName.text.toString()
            val lastName: String = binding.editTextSignupLastName.text.toString()
            val phoneNumber: String = countryCodePicker?.fullNumberWithPlus.toString()
            val password: String = binding.editTextSignupPasswd.text.toString()
            confirmPassword = binding.editTextSignupConfirmPassword.text.toString()

            if (email.isEmpty()) {
                binding.editTextSignupEmail.error = "This field is required"
            } else if (firstName.isEmpty()) {
                binding.editTextSignupFirstName.error = "This field is required"
            } else if (lastName.isEmpty()) {
                binding.editTextSignupLastName.error = "This field is required"
            } else if (phoneNumber.isEmpty()) {
                binding.editTextSignupPhoneNumber.error = "This field is required"
            } else if (password.isEmpty()) {
                binding.editTextSignupPasswd.error = "This field is required"
            } else if (confirmPassword!!.isEmpty()) {
                binding.editTextSignupConfirmPassword.error = "This field is required"
            } else {
                if (password != confirmPassword) {
                    Toast.makeText(this, "Password Mismatch", Toast.LENGTH_SHORT).show()
                } else{
                    checkNetworkConnection(
                        email,
                        firstName,
                        middleName,
                        lastName,
                        phoneNumber,
                        DOMAIN_ID!!,
                        password,
                        confirmPassword!!
                    )
                }

            }
        })

        binding.visibleEyeIconVectorSignup.setOnClickListener {
            if (visibilityFlag) {
                binding.visibleEyeIconVectorSignup.setImageResource(R.drawable.vector_passwd_eye)
                binding.editTextSignupPasswd.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                visibilityFlag = false
            } else {
                binding.visibleEyeIconVectorSignup.setImageResource(R.drawable.vector_eye_visible)
                binding.editTextSignupPasswd.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                visibilityFlag = true
            }
            binding.editTextSignupPasswd.setSelection(binding.editTextSignupPasswd.text.length)
        }

        binding.visibleEyeIconVectorSignup1.setOnClickListener {
            if (visibilityFlag) {
                binding.visibleEyeIconVectorSignup1.setImageResource(R.drawable.vector_passwd_eye)
                binding.editTextSignupConfirmPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                visibilityFlag = false
            } else {
                binding.visibleEyeIconVectorSignup1.setImageResource(R.drawable.vector_eye_visible)
                binding.editTextSignupConfirmPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                visibilityFlag = true
            }
            binding.editTextSignupPasswd.setSelection(binding.editTextSignupPasswd.text.length)
        }

    }

    // Method to check Internet Connection
    private fun checkNetworkConnection(
        email: String,
        firstName: String,
        middleName: String,
        lastName: String,
        phoneNumber: String,
        domainId: Int,
        password: String,
        confirmPassword: String
    ) {
        if (NetworkUtil().isNetworkAvailable(this)) {
            authenticateCredentials(
                email,
                firstName,
                middleName,
                lastName,
                phoneNumber,
                domainId,
                password,
                confirmPassword
            )
            showProgressBar()
        } else {
            dialogNoInternet()
        }
    }


    // Method that authenticates the user Credentials
    private fun authenticateCredentials(
        email: String,
        firstName: String,
        middleName: String = "",
        lastName: String,
        phoneNumber: String,
        domainId: Int,
        password: String,
        confirmPassword: String
    ) {
        signupDisposable = ApiClientProxy.signup(
            email,
            firstName,
            middleName,
            lastName,
            phoneNumber,
            domainId,
            password,
            confirmPassword
        ).retryWhen { error ->
            error.zipWith(Observable.range(1, 3)) { error, retryCount ->
                if (error is SocketTimeoutException && retryCount < 3) {
                    Observable.timer(2, TimeUnit.SECONDS) // Wait for 2 seconds before retrying
                } else {
                    Observable.error<Throwable>(error)
                }
            }.flatMap { it }
        }.subscribe(this::onSignupSuccess, this::onSignupError)
    }

    private fun onSignupSuccess(signupResponse: SignupResponse?) {
        if (signupResponse != null) {
            if (signupResponse.isSuccess) {
                dialogProgressBar?.visibility = View.GONE
                setSharedPreferences(signupResponse.result.firstName, signupResponse.result.middleName, signupResponse.result.lastName, signupResponse.result.email, confirmPassword!!)
                Toast.makeText(this, "Signup Successfully", Toast.LENGTH_SHORT).show()
                showPopupWindow()
            } else {
                dialogProgressBar?.visibility = View.GONE
                Toast.makeText(this, signupResponse.message, Toast.LENGTH_SHORT).show()
            }
        } else {
            // Handle the case where the login response is null
            Toast.makeText(this, "Login Failed: Null response from server", Toast.LENGTH_SHORT)
                .show()
        }
    }


    private fun onSignupError(throwable: Throwable?) {
        dialogProgressBar?.visibility = View.GONE
        Toast.makeText(this, "Register Failed", Toast.LENGTH_SHORT).show()

    }

    private fun setSharedPreferences(firstName: String,middleName: String , lastName: String, email: String, confirmPassword: String){
        sharedSesssionPrefs?.edit()?.putString(USER_FIRST_NAME, firstName)?.apply()
        sharedSesssionPrefs?.edit()?.putString(USER_MIDDLE_NAME, middleName)?.apply()
        sharedSesssionPrefs?.edit()?.putString(USER_LAST_NAME, lastName)?.apply()
        sharedSesssionPrefs?.edit()?.putString(USER_EMAIL, email)?.apply()
        sharedSesssionPrefs?.edit()?.putString(USER_PASSWORD, confirmPassword)?.apply()


    }
    private fun showProgressBar() {
//        val progressBar = dialogProgressBar?.findViewById<ProgressBar>(R.id.progressBar)

            dialogProgressBar?.visibility = View.VISIBLE


//        dialogProgressBar?.visibility = View.VISIBLE
    }

    private fun showPopupWindow() {
        val dialog = Dialog(this@ActivitySignupScreen)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.request_sent_popup)


        // Convert dp to pixels
//        val widthInPx = TypedValue.applyDimension(
//            TypedValue.COMPLEX_UNIT_DIP,
//            190f,
//            getResources().getDisplayMetrics()
//        ).toInt()
//        val heightInPx = TypedValue.applyDimension(
//            TypedValue.COMPLEX_UNIT_DIP,
//            188f,
//            getResources().getDisplayMetrics()
//        ).toInt()

        var textView = dialog.findViewById<TextView>(R.id.txt_login_success)
        textView.visibility = View.VISIBLE
        textView.setOnClickListener {
            val intent: Intent = Intent(this, ActivityLogin::class.java)
            startActivity(intent)
        }
        textView = dialog.findViewById<TextView>(R.id.request_sen)
        textView.setText(getString(R.string.signup_success))

        dialog.show()
    }

    private fun dialogNoInternet() {
        val dialog = Dialog(this@ActivitySignupScreen)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(R.layout.no_internet_dialog)


        // Convert dp to pixels
//        val widthInPx = TypedValue.applyDimension(
//            TypedValue.COMPLEX_UNIT_DIP,
//            190f,
//            getResources().getDisplayMetrics()
//        ).toInt()
//        val heightInPx = TypedValue.applyDimension(
//            TypedValue.COMPLEX_UNIT_DIP,
//            188f,
//            getResources().getDisplayMetrics()
//        ).toInt()

        dialog.findViewById<Button>(R.id.btnRetry).setOnClickListener(View.OnClickListener {
            if (NetworkUtil().isNetworkAvailable(this)) {
                dialog.dismiss()
                Toast.makeText(this, "Internet Connection Retrieved", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please Check your Internet Connection", Toast.LENGTH_SHORT)
                    .show()
            }
        })
        dialog.show()
    }


}