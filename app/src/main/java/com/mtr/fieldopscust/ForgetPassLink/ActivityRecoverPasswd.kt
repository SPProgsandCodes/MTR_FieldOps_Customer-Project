package com.mtr.fieldopscust.ForgetPassLink

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mtr.fieldopscust.ResetPassword.ActivityForgotPasswdReset
import com.mtr.fieldopscust.R
import com.mtr.fieldopscust.databinding.ActivityRecoverPasswdBinding
import com.mtr.fieldopscust.network.request.ForgetPassLinkResponse
import com.mtr.fieldopsemp.network.ApiClientProxy
import io.reactivex.disposables.Disposable
import android.net.Uri
import android.view.Window
import android.widget.Button
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import com.mtr.fieldopscust.NetworkUtil
import com.mtr.fieldopscust.Utils.ApplicationHelper.hideSystemUI
import io.reactivex.Observable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

class ActivityRecoverPasswd : AppCompatActivity() {
    lateinit var binding: ActivityRecoverPasswdBinding

    private var dialogProgressBar: ProgressBar?=null
    private var resetPassLinkDisposable: Disposable? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRecoverPasswdBinding.inflate(getLayoutInflater())
        setContentView(binding.getRoot())
        this.enableEdgeToEdge()
        hideSystemUI(this@ActivityRecoverPasswd)
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        coroutineScope.launch {
            delay(2000)
            hideSystemUI(this@ActivityRecoverPasswd)
        }
        dialogProgressBar = findViewById(R.id.resetLinkProgressBar)

        binding.btnResetPasswdLink.setOnClickListener(View.OnClickListener {

            val emailLink = binding.editTextEmail.text.toString()
            if (emailLink.isEmpty()) {
                binding.resetPassw.error = "This Field is required"
            } else {
                checkNetworkConnection(emailLink)
            }
        })

    }

    private fun onSuccess(forgetPassLinkResponse: ForgetPassLinkResponse) {
        if (forgetPassLinkResponse.isSuccess) {
            dialogProgressBar?.visibility = View.GONE
            Toast.makeText(this, "Link sent successfully", Toast.LENGTH_SHORT).show()
            showPopup()
        } else {
            dialogProgressBar?.visibility = View.GONE
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
        }

    }

    private fun onError(throwable: Throwable?) {
        if (throwable != null) {
            dialogProgressBar?.visibility = View.GONE
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showPopup() {
        val inflater: LayoutInflater = getLayoutInflater()
        val view: View = inflater.inflate(R.layout.recover_passwd_popup, null)

        val builder = AlertDialog.Builder(this@ActivityRecoverPasswd)
        builder.setView(view)

        val dialog = builder.create()
        dialog.show()

        view.findViewById<View>(R.id.frame_155).setOnClickListener {
//            val intent: Intent =
//                Intent(this@ActivityRecoverPasswd, ActivityForgotPasswdReset::class.java)
//            startActivity(intent)
            dialog.dismiss()
        }
    }

    private fun checkNetworkConnection(emailLink: String) {
        if (NetworkUtil().isNetworkAvailable(this)) {
            authenticateCredentials(emailLink)
            showProgressBar()
        } else {
            dialogNoInternet()
        }
    }

    // Method that authenticates the user Credentials
    private fun authenticateCredentials(emailLink: String) {
        resetPassLinkDisposable = ApiClientProxy.forgetpasslink(emailLink)
            .retryWhen { error ->
                error.zipWith(Observable.range(1, 3)) { error, retryCount ->
                    if (error is SocketTimeoutException && retryCount < 3) {
                        Observable.timer(2, TimeUnit.SECONDS) // Wait for 2 seconds before retrying
                    } else {
                        Observable.error<Throwable>(error)
                    }
                }.flatMap { it }
            }
            .subscribe(this::onSuccess, this::onError)
    }
    private fun showProgressBar(){
        dialogProgressBar?.visibility = View.VISIBLE
    }

    private fun dialogNoInternet() {
        val dialog = Dialog(this@ActivityRecoverPasswd)
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



