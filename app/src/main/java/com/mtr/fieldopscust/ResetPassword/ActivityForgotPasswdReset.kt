package com.mtr.fieldopscust.ResetPassword

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mtr.fieldopscust.R
import android.content.Intent
import android.net.Uri
import android.view.Window
import android.view.WindowManager
import com.mtr.fieldopscust.Utils.ApplicationHelper.hideSystemUI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ActivityForgotPasswdReset : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        hideSystemUI(this@ActivityForgotPasswdReset)
        coroutineScope.launch {
            delay(2000)
            hideSystemUI(this@ActivityForgotPasswdReset)
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_forgot_passwd_reset)
        try {
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v: View, insets: WindowInsetsCompat ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
        // ATTENTION: This was auto-generated to handle app links.
        val appLinkIntent: Intent = intent
        val appLinkAction: String? = appLinkIntent.action
        val appLinkData: Uri? = appLinkIntent.data
    }
}