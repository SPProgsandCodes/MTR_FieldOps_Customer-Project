package com.mtr.fieldopscust

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mtr.fieldopscust.LoginScreen.ActivityLogin
import com.mtr.fieldopscust.Utils.ApplicationHelper
import com.mtr.fieldopscust.Utils.ApplicationHelper.hideSystemUI
import com.mtr.fieldopscust.Utils.Constants.Companion.IS_LOGIN
import com.mtr.fieldopscust.Utils.Constants.Companion.LOGIN_PREFS
import com.mtr.fieldopscust.Utils.SessionPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreen : AppCompatActivity() {

    private var sharedSessionPreferences: SessionPreferences? = null
    private var isLoggedIn = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        hideSystemUI(this@SplashScreen)
        coroutineScope.launch {
            delay(2000)
            hideSystemUI(this@SplashScreen)
        }
        sharedSessionPreferences = ApplicationHelper.getAppController()?.loginPrefs(LOGIN_PREFS)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById<View>(R.id.main)) { v: View, insets: WindowInsetsCompat ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
            insets
        }


        if (NetworkUtil().isNetworkAvailable(this)){
            proceedToNextActivity()
        } else {
            dialogNoInternet()
        }

    }

    private fun proceedToNextActivity() {
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        coroutineScope.launch {
            delay(3000)
//            var sharedPref: SharedPreferences = getSharedPreferences("LOGIN", Context.MODE_PRIVATE)
//            var isLoggedIn = sharedPref.getBoolean("LOGIN", false)
           isLoggedIn = sharedSessionPreferences!!.getBoolean(IS_LOGIN, false)
            Log.d("isLoggedin", isLoggedIn.toString())
            if (isLoggedIn){
                val intent = Intent(this@SplashScreen, ActivityDashboard::class.java)
                startActivity(intent)
                finish()
            } else{
                val intent = Intent(this@SplashScreen, ActivityLogin::class.java)
                startActivity(intent)
                finish()
            }

        }
    }

    private fun dialogNoInternet() {
        val dialog = Dialog(this@SplashScreen)
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
//            if (NetworkUtil().isNetworkAvailable(this)){
//                dialog.dismiss()
//                Toast.makeText(this, "Internet Connection Retrieved", Toast.LENGTH_SHORT).show()
//                proceedToNextActivity()
//            } else {
//                Toast.makeText(this, "Please Check your Internet Connection", Toast.LENGTH_SHORT).show()
//            }
            dialog.dismiss()

        })
        dialog.show()
    }



}