package com.mtr.fieldopscust.LoginScreen

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.mtr.fieldopscust.ActivityDashboard
import com.mtr.fieldopscust.ForgetPassLink.ActivityRecoverPasswd
import com.mtr.fieldopscust.NetworkUtil
import com.mtr.fieldopscust.R
import com.mtr.fieldopscust.SignupScreen.ActivitySignupScreen
import com.mtr.fieldopscust.Utils.ApplicationHelper
import com.mtr.fieldopscust.Utils.ApplicationHelper.hideSystemUI
import com.mtr.fieldopscust.Utils.Constants.Companion.DOMAIN_ID_KEY
import com.mtr.fieldopscust.Utils.Constants.Companion.IS_LOGIN
import com.mtr.fieldopscust.Utils.Constants.Companion.LOGIN_PREFS
import com.mtr.fieldopscust.Utils.Constants.Companion.PASSWORD
import com.mtr.fieldopscust.Utils.Constants.Companion.TOKEN_KEY
import com.mtr.fieldopscust.Utils.Constants.Companion.USERNAME
import com.mtr.fieldopscust.Utils.Constants.Companion.USER_EMAIL
import com.mtr.fieldopscust.Utils.Constants.Companion.USER_FIRST_NAME
import com.mtr.fieldopscust.Utils.Constants.Companion.USER_GET_PROFILE_PIC
import com.mtr.fieldopscust.Utils.Constants.Companion.USER_ID_KEY
import com.mtr.fieldopscust.Utils.Constants.Companion.USER_LAST_NAME
import com.mtr.fieldopscust.Utils.Constants.Companion.USER_MIDDLE_NAME
import com.mtr.fieldopscust.Utils.Constants.Companion.USER_PASSWORD
import com.mtr.fieldopscust.Utils.Constants.Companion.USER_PHONE_NUMBER
import com.mtr.fieldopscust.Utils.SessionPreferences
import com.mtr.fieldopscust.databinding.ActivityLoginBinding
import com.mtr.fieldopsemp.network.ApiClientProxy
import com.mtr.fieldopsemp.network.request.LoginResponse
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

class ActivityLogin : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private lateinit var loginDisposable: Disposable
    private var dialogProgressBar: ProgressBar? = null
    private var VISIBILITY_FLAG = false
    private var sharedSesssionPrefs: SessionPreferences? = null
    private var passwd: String?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        hideSystemUI(this@ActivityLogin)
        coroutineScope.launch {
            delay(2000)
            hideSystemUI(this@ActivityLogin)
        }
        dialogProgressBar = findViewById(R.id.loginProgressBar)
        sharedSesssionPrefs = ApplicationHelper.getAppController()?.loginPrefs(LOGIN_PREFS)

        if (sharedSesssionPrefs?.getString("email", "")!!.isNotEmpty()) {
            binding.editTextLoginEmail.setText(sharedSesssionPrefs?.getString("email", ""))
            binding.editTextResetPassword.setText(sharedSesssionPrefs?.getString("password", ""))
        }

        binding.visibleEyeIconVector.setOnClickListener {
            if (VISIBILITY_FLAG) {
                binding.visibleEyeIconVector.setImageResource(R.drawable.vector_passwd_eye)
                binding.editTextResetPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                VISIBILITY_FLAG = false
            } else {
                binding.visibleEyeIconVector.setImageResource(R.drawable.vector_eye_visible)
                binding.editTextResetPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                VISIBILITY_FLAG = true
            }
            binding.editTextResetPassword.setSelection(binding.editTextResetPassword.text.length)
        }

        binding.btnLogin.setOnClickListener {

            val email: String = binding.editTextLoginEmail.text.toString()
            val password: String = binding.editTextResetPassword.text.toString()

            if (email.isEmpty()) {
                binding.editTextLoginEmail.error = "This field is required"
            } else if (password.isEmpty()) {
                binding.editTextResetPassword.error = "This field is required"
            } else {
                checkNetworkConnection(email, password)

            }
        }

        binding.txtSignup.setOnClickListener {
            val intent: Intent = Intent(this@ActivityLogin, ActivitySignupScreen::class.java)
            startActivity(intent)
        }

        binding.forgotPass.setOnClickListener {
            val intent: Intent =
                Intent(this@ActivityLogin, ActivityRecoverPasswd::class.java)
            startActivity(intent)
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    // Method to check Internet Connection
    private fun checkNetworkConnection(email: String, password: String) {
        if (NetworkUtil().isNetworkAvailable(this)) {
            authenticateCredentials(email, password)
            showProgressBar()
        } else {
            dialogNoInternet()
        }
    }

    // Method that authenticates the user Credentials
    private fun authenticateCredentials(email: String, password: String) {
        passwd = password
        loginDisposable = ApiClientProxy.login(email, password)
            .retryWhen { error ->
                error.zipWith(Observable.range(1, 3)) { error, retryCount ->
                    if (error is SocketTimeoutException && retryCount < 3) {
                        Observable.timer(2, TimeUnit.SECONDS) // Wait for 2 seconds before retrying
                    } else {
                        Observable.error<Throwable>(error)
                    }
                }.flatMap { it }
            }
            .subscribe(this::onLoginSuccess, this::onLoginError)
    }

    private fun onLoginSuccess(loginResponse: LoginResponse?) {
        if (loginResponse != null) {
            if (loginResponse.isSuccess) {
                dialogProgressBar?.visibility = View.GONE

                Toast.makeText(this, "Login Successfully", Toast.LENGTH_SHORT).show()

                Log.d("TAG", "UserId: ${loginResponse.result.user.id}")

                if (binding.checkboxCh.isChecked){
                    Log.d("TAG121212", "isChecked: "+binding.checkboxCh.isChecked)

                    sharedSesssionPrefs!!.edit().putString("email", binding.editTextLoginEmail.text.toString()).apply()
                    sharedSesssionPrefs!!.edit().putString("password", binding.editTextResetPassword.text.toString()).apply()
                }else{
                    Log.d("TAG121212", "isChecked: "+binding.checkboxCh.isChecked)
                    sharedSesssionPrefs?.edit()?.putString("email", "")?.apply()
                    sharedSesssionPrefs?.edit()?.putString("password", "")?.apply()
                }

                setSharedPreferences(
                    loginResponse.result.token,
                    loginResponse.result.user.id,
                    loginResponse.result.user.profileUrl,
                    loginResponse.result.user.firstName,
                    loginResponse.result.user.middleName,
                    loginResponse.result.user.lastName,
                    loginResponse.result.user.email, passwd!!,
                    loginResponse.result.user.phoneNumber,
                    loginResponse.result.user.domainId)

                dialogProgressBar?.visibility = View.GONE
                val intent = Intent(this@ActivityLogin, ActivityDashboard::class.java)
                startActivity(intent)

            } else {
                dialogProgressBar?.visibility = View.GONE
                Toast.makeText(this, "Incorrect Email or Password", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Handle the case where the login response is null
            Toast.makeText(this, "Login Failed: Null response from server", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun setSharedPreferences(token: String, userId: Int, userProfileUrl: String, userFirstName: String,
                                     userMiddleName: String, userLastName: String,
                                     userEmail: String, userPasswd: String, userPhone: String,
                                     domainId: Int) {
        sharedSesssionPrefs?.edit()?.putBoolean(IS_LOGIN, true)?.apply()
        sharedSesssionPrefs?.edit()?.putString(TOKEN_KEY, token)?.apply()
        sharedSesssionPrefs?.edit()?.putInt(USER_ID_KEY, userId)?.apply()
        sharedSesssionPrefs?.edit()?.putString(USER_GET_PROFILE_PIC, userProfileUrl)?.apply()
        sharedSesssionPrefs?.edit()?.putString(USER_FIRST_NAME, userFirstName)?.apply()
        sharedSesssionPrefs?.edit()?.putString(USER_MIDDLE_NAME, userMiddleName)?.apply()
        sharedSesssionPrefs?.edit()?.putString(USER_LAST_NAME, userLastName)?.apply()
        sharedSesssionPrefs?.edit()?.putString(USER_EMAIL, userEmail)?.apply()
        sharedSesssionPrefs?.edit()?.putString(USER_PASSWORD, userPasswd)?.apply()
        sharedSesssionPrefs?.edit()?.putString(USER_PHONE_NUMBER, userPhone)?.apply()
        sharedSesssionPrefs?.edit()?.putInt(DOMAIN_ID_KEY, domainId)?.apply()

        Log.d("MethodCalled", "User Url in Method: $userProfileUrl and token: $token")
    }

    private fun onLoginError(throwable: Throwable?) {
        if (throwable != null) {
            dialogProgressBar?.visibility = View.GONE
            Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
        }
    }


    private fun loginSuccessPopup() {

        val dialog = Dialog(this@ActivityLogin)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.request_sent_popup)


        val textView = dialog.findViewById<TextView>(R.id.request_sen)
        textView.setText(getString(R.string.login_success))


        // Show the dialog
        dialog.show()


    }


    private fun showProgressBar() {

            dialogProgressBar?.visibility = View.VISIBLE

    }

    private fun dialogNoInternet() {
        val dialog = Dialog(this@ActivityLogin)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(R.layout.no_internet_dialog)


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
    override fun onDestroy() {
        super.onDestroy()
        if (::loginDisposable.isInitialized && !loginDisposable.isDisposed) {
            loginDisposable.dispose()
        }
    }
}
