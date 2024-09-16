package com.mtr.fieldopscust

import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.mtr.fieldopscust.DashboardScreen.FragmentHomePage
import com.mtr.fieldopscust.DashboardScreen.ViewModelDashboard
import com.mtr.fieldopscust.MessagesScreen.FragmentMessages
import com.mtr.fieldopscust.PaymentsScreen.FragmentPaymentMethods
import com.mtr.fieldopscust.ProfileSettingsScreen.FragmentProfileSettings
import com.mtr.fieldopscust.Utils.ApplicationHelper.hideSystemUI
import com.mtr.fieldopscust.databinding.ActivityDashboardBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ActivityDashboard : AppCompatActivity() {
    lateinit var binding: ActivityDashboardBinding
    private lateinit var sharedViewModel: ViewModelDashboard
    private var backPressedTime: Long = 0
    private lateinit var backToast: Toast
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        hideSystemUI(this@ActivityDashboard)
        coroutineScope.launch {
            delay(2000)
            hideSystemUI(this@ActivityDashboard)
        }

        binding = ActivityDashboardBinding.inflate(getLayoutInflater())

        sharedViewModel = ViewModelProvider(this).get(ViewModelDashboard::class.java)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(binding.getRoot())
        val textView: TextView = findViewById<TextView>(R.id.home)
        loadFragment(FragmentHomePage())

        val navHome: LinearLayout = findViewById<LinearLayout>(R.id.bottom_nav_home)
        val navMessages: LinearLayout = findViewById<LinearLayout>(R.id.bottom_nav_message)
        val navPayments: LinearLayout = findViewById<LinearLayout>(R.id.bottom_nav_payments)
        val navSettings: LinearLayout = findViewById<LinearLayout>(R.id.bottom_nav_settings)
        findViewById<View>(R.id.home_nav_option).setBackgroundResource(R.drawable.nav_bar_focussed_bg)
        val typeface: Typeface? = ResourcesCompat.getFont(this, R.font.inter_bold)
        textView.setTypeface(typeface)

        val imageView: ImageView = findViewById<ImageView>(R.id.img_home_nav)
        val drawable: Drawable = getResources().getDrawable(R.drawable.btn_home_navigation_focussed)
        imageView.setImageDrawable(drawable)

        navHome.setOnClickListener(View.OnClickListener {
            findViewById<View>(R.id.home_nav_option).setBackgroundResource(R.drawable.nav_bar_focussed_bg)
            findViewById<View>(R.id.msg_nav_option).setBackgroundResource(R.drawable.nav_bar_unfocused)
            findViewById<View>(R.id.payment_nav_option).setBackgroundResource(R.drawable.nav_bar_unfocused)
            findViewById<View>(R.id.settings_nav_option).setBackgroundResource(R.drawable.nav_bar_unfocused)

            var textView: TextView? = null
            var typeface: Typeface? = null

            textView = findViewById<TextView>(R.id.home)
            textView.setTextColor(getResources().getColor(R.color.btn_color))
            typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.inter_bold)
            textView!!.setTypeface(typeface)

            textView = findViewById<TextView>(R.id.txt_message)
            textView.setTextColor(getResources().getColor(R.color.text_unfocussed))
            typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.inter)
            textView.setTypeface(typeface)

            textView = findViewById<TextView>(R.id.payment)
            textView.setTextColor(getResources().getColor(R.color.text_unfocussed))
            typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.inter)
            textView.setTypeface(typeface)

            textView = findViewById<TextView>(R.id.txt_settings)
            typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.inter)
            textView.setTypeface(typeface)
            textView.setTextColor(getResources().getColor(R.color.text_unfocussed))

            var imageView: ImageView = findViewById<ImageView>(R.id.img_home_nav)
            var drawable: Drawable =
                getResources().getDrawable(R.drawable.btn_home_navigation_focussed)
            imageView.setImageDrawable(drawable)



            imageView = findViewById<ImageView>(R.id.img_message_nav)
            drawable = getResources().getDrawable(R.drawable.vector_message_nav_bar_unfoccussed)
            imageView.setImageDrawable(drawable)

            imageView = findViewById<ImageView>(R.id.img_payment_nav)
            drawable = getResources().getDrawable(R.drawable.vector_payment_nav_bar_unfoccused)
            imageView.setImageDrawable(drawable)

            imageView = findViewById<ImageView>(R.id.img_settings_nav)
            drawable = getResources().getDrawable(R.drawable.vector_settings_nav_bar_unfoccussed)
            imageView.setImageDrawable(drawable)
            loadFragment(FragmentHomePage())
        })

        navMessages.setOnClickListener(View.OnClickListener {
            findViewById<View>(R.id.msg_nav_option).setBackgroundResource(R.drawable.nav_bar_focussed_bg)
            findViewById<View>(R.id.home_nav_option).setBackgroundResource(R.drawable.nav_bar_unfocused)
            findViewById<View>(R.id.payment_nav_option).setBackgroundResource(R.drawable.nav_bar_unfocused)
            findViewById<View>(R.id.settings_nav_option).setBackgroundResource(R.drawable.nav_bar_unfocused)

            var textView: TextView? = null
            var typeface: Typeface? = null

            textView = findViewById<TextView>(R.id.home)
            textView!!.setTextColor(getResources().getColor(R.color.text_unfocussed))
            typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.inter)
            textView!!.setTypeface(typeface)

            textView = findViewById<TextView>(R.id.txt_message)
            textView!!.setTextColor(getResources().getColor(R.color.btn_color))
            typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.inter_bold)
            textView!!.setTypeface(typeface)

            textView = findViewById<TextView>(R.id.payment)
            textView!!.setTextColor(getResources().getColor(R.color.text_unfocussed))
            typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.inter)
            textView!!.setTypeface(typeface)

            textView = findViewById<TextView>(R.id.txt_settings)
            textView!!.setTextColor(getResources().getColor(R.color.text_unfocussed))
            typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.inter)
            textView!!.setTypeface(typeface)

            var imageView: ImageView = findViewById<ImageView>(R.id.img_home_nav)
            var drawable: Drawable =
                getResources().getDrawable(R.drawable.btn_home_navigation_unfocussed)
            imageView.setImageDrawable(drawable)


            imageView = findViewById<ImageView>(R.id.img_message_nav)
            drawable = getResources().getDrawable(R.drawable.vector_message_nav_bar_foccussed)
            imageView.setImageDrawable(drawable)

            imageView = findViewById<ImageView>(R.id.img_payment_nav)
            drawable = getResources().getDrawable(R.drawable.vector_payment_nav_bar_unfoccused)
            imageView.setImageDrawable(drawable)

            imageView = findViewById<ImageView>(R.id.img_settings_nav)
            drawable = getResources().getDrawable(R.drawable.vector_settings_nav_bar_unfoccussed)
            imageView.setImageDrawable(drawable)
            loadFragment(FragmentMessages())
        })

        navPayments.setOnClickListener(View.OnClickListener {
            findViewById<View>(R.id.msg_nav_option).setBackgroundResource(R.drawable.nav_bar_unfocused)
            findViewById<View>(R.id.home_nav_option).setBackgroundResource(R.drawable.nav_bar_unfocused)
            findViewById<View>(R.id.payment_nav_option).setBackgroundResource(R.drawable.nav_bar_focussed_bg)
            findViewById<View>(R.id.settings_nav_option).setBackgroundResource(R.drawable.nav_bar_unfocused)

            var textView: TextView? = null
            var typeface: Typeface? = null

            textView = findViewById<TextView>(R.id.home)
            textView!!.setTextColor(getResources().getColor(R.color.text_unfocussed))
            typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.inter)
            textView!!.setTypeface(typeface)

            textView = findViewById<TextView>(R.id.txt_message)
            textView!!.setTextColor(getResources().getColor(R.color.text_unfocussed))
            typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.inter)
            textView!!.setTypeface(typeface)

            textView = findViewById<TextView>(R.id.payment)
            textView!!.setTextColor(getResources().getColor(R.color.btn_color))
            typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.inter_bold)
            textView!!.setTypeface(typeface)

            textView = findViewById<TextView>(R.id.txt_settings)
            textView!!.setTextColor(getResources().getColor(R.color.text_unfocussed))
            typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.inter)
            textView!!.setTypeface(typeface)


            var imageView: ImageView = findViewById<ImageView>(R.id.img_home_nav)
            var drawable: Drawable =
                getResources().getDrawable(R.drawable.btn_home_navigation_unfocussed)
            imageView.setImageDrawable(drawable)


            imageView = findViewById<ImageView>(R.id.img_message_nav)
            drawable = getResources().getDrawable(R.drawable.vector_message_nav_bar_unfoccussed)
            imageView.setImageDrawable(drawable)

            imageView = findViewById<ImageView>(R.id.img_payment_nav)
            drawable = getResources().getDrawable(R.drawable.vector_payment_nav_bar_foccused)
            imageView.setImageDrawable(drawable)

            imageView = findViewById<ImageView>(R.id.img_settings_nav)
            drawable = getResources().getDrawable(R.drawable.vector_settings_nav_bar_unfoccussed)
            imageView.setImageDrawable(drawable)
            loadFragment(FragmentPaymentMethods())
        })

        navSettings.setOnClickListener(View.OnClickListener {
            findViewById<View>(R.id.msg_nav_option).setBackgroundResource(R.drawable.nav_bar_unfocused)
            findViewById<View>(R.id.home_nav_option).setBackgroundResource(R.drawable.nav_bar_unfocused)
            findViewById<View>(R.id.payment_nav_option).setBackgroundResource(R.drawable.nav_bar_unfocused)
            findViewById<View>(R.id.settings_nav_option).setBackgroundResource(R.drawable.nav_bar_focussed_bg)
            var textView: TextView? = findViewById<TextView>(R.id.home)
            textView?.setTextColor(getResources().getColor(R.color.text_unfocussed))
            var typeface: Typeface? = ResourcesCompat.getFont(getApplicationContext(), R.font.inter)
            textView!!.setTypeface(typeface)

            textView = findViewById<TextView>(R.id.txt_message)
            typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.inter)
            textView!!.setTypeface(typeface)
            textView!!.setTextColor(getResources().getColor(R.color.text_unfocussed))

            textView = findViewById<TextView>(R.id.payment)
            typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.inter)
            textView!!.setTypeface(typeface)
            textView!!.setTextColor(getResources().getColor(R.color.text_unfocussed))
            typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.inter)
            textView!!.setTypeface(typeface)

            textView = findViewById<TextView>(R.id.txt_settings)
            textView!!.setTextColor(getResources().getColor(R.color.btn_color))

            typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.inter_bold)
            textView!!.setTypeface(typeface)

            var imageView: ImageView = findViewById<ImageView>(R.id.img_home_nav)
            var drawable: Drawable =
                getResources().getDrawable(R.drawable.btn_home_navigation_unfocussed)
            imageView.setImageDrawable(drawable)


            imageView = findViewById<ImageView>(R.id.img_message_nav)
            drawable = getResources().getDrawable(R.drawable.vector_message_nav_bar_unfoccussed)
            imageView.setImageDrawable(drawable)

            imageView = findViewById<ImageView>(R.id.img_payment_nav)
            drawable = getResources().getDrawable(R.drawable.vector_payment_nav_bar_unfoccused)
            imageView.setImageDrawable(drawable)

            imageView = findViewById<ImageView>(R.id.img_settings_nav)
            drawable = getResources().getDrawable(R.drawable.vector_settings_nav_bar_foccussed)
            imageView.setImageDrawable(drawable)
            loadFragment(FragmentProfileSettings())
        })
    }

    override fun onBackPressed() {
        val fragmentManager = supportFragmentManager
        if (fragmentManager.backStackEntryCount > 1) {
            fragmentManager.popBackStack()
            // Update bottom navigation to reflect the current fragment
            val currentFragment = fragmentManager.findFragmentById(R.id.fragment_container)
            updateBottomNavigation(currentFragment)
        } else {
            // Handle exit case with double back press
            if (backPressedTime + 2000 > System.currentTimeMillis()) {
                backToast.cancel()
                finishAffinity()
                super.onBackPressed()
                return
            } else {
                backToast = Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT)
                backToast.show()
            }
            backPressedTime = System.currentTimeMillis()
        }
    }
    private fun loadFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = getSupportFragmentManager()
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()

        updateBottomNavigation(fragment)
    }

    fun updateBottomNavigation(fragment: Fragment?) {
        when (fragment) {
            is FragmentHomePage -> {
                findViewById<View>(R.id.home_nav_option).setBackgroundResource(R.drawable.nav_bar_focussed_bg)
                findViewById<View>(R.id.msg_nav_option).setBackgroundResource(R.drawable.nav_bar_unfocused)
                findViewById<View>(R.id.payment_nav_option).setBackgroundResource(R.drawable.nav_bar_unfocused)
                findViewById<View>(R.id.settings_nav_option).setBackgroundResource(R.drawable.nav_bar_unfocused)
            }
            is FragmentMessages -> {
                findViewById<View>(R.id.msg_nav_option).setBackgroundResource(R.drawable.nav_bar_focussed_bg)
                findViewById<View>(R.id.home_nav_option).setBackgroundResource(R.drawable.nav_bar_unfocused)
                findViewById<View>(R.id.payment_nav_option).setBackgroundResource(R.drawable.nav_bar_unfocused)
                findViewById<View>(R.id.settings_nav_option).setBackgroundResource(R.drawable.nav_bar_unfocused)
            }
            is FragmentPaymentMethods -> {
                findViewById<View>(R.id.payment_nav_option).setBackgroundResource(R.drawable.nav_bar_focussed_bg)
                findViewById<View>(R.id.home_nav_option).setBackgroundResource(R.drawable.nav_bar_unfocused)
                findViewById<View>(R.id.msg_nav_option).setBackgroundResource(R.drawable.nav_bar_unfocused)
                findViewById<View>(R.id.settings_nav_option).setBackgroundResource(R.drawable.nav_bar_unfocused)
            }
            is FragmentProfileSettings -> {
                findViewById<View>(R.id.settings_nav_option).setBackgroundResource(R.drawable.nav_bar_focussed_bg)
                findViewById<View>(R.id.home_nav_option).setBackgroundResource(R.drawable.nav_bar_unfocused)
                findViewById<View>(R.id.msg_nav_option).setBackgroundResource(R.drawable.nav_bar_unfocused)
                findViewById<View>(R.id.payment_nav_option).setBackgroundResource(R.drawable.nav_bar_unfocused)
            }
        }
    }






}