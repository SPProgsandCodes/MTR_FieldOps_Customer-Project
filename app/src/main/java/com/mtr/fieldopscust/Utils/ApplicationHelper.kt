package com.mtr.fieldopscust.Utils


import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.telephony.TelephonyManager
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.hbb20.CountryCodePicker
import com.mtr.fieldopscust.LoginScreen.ActivityLogin
import com.mtr.fieldopscust.NetworkUtil
import com.mtr.fieldopscust.R
import com.mtr.fieldopscust.Utils.Constants.Companion.IS_LOGIN
import java.io.ByteArrayOutputStream

import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Locale

object ApplicationHelper {
    private var appController: AppController? = null
    fun getAppController(): AppController? {
        return appController
    }

    fun setAppController(appController: AppController?) {
        this.appController = appController
    }

    fun getFileFromUri(uri: Uri, context: Context): File? {
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(uri, filePathColumn, null, null, null)
        cursor?.moveToFirst()

        val columnIndex = cursor?.getColumnIndex(filePathColumn[0])
        val filePath = columnIndex?.let { cursor.getString(it) }
        cursor?.close()

        return filePath?.let { File(it) }
    }

    fun resizeBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val aspectRatio = width.toFloat() / height.toFloat()

        val newWidth = if (width > height) maxWidth else (maxHeight * aspectRatio).toInt()
        val newHeight = if (height > width) maxHeight else (maxWidth / aspectRatio).toInt()

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    fun compressBitmap(bitmap: Bitmap, quality: Int): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        // Compress the bitmap with the given quality (0-100)
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }

    fun convertDate(inputDate: String): String {
        // Define the input date format
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSS", Locale.getDefault())

        // Define the output date format
        val outputFormat = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())

        // Parse the input date string to a Date object
        val date = inputFormat.parse(inputDate)

        // Format the Date object to the desired output format
        return outputFormat.format(date!!)
    }

    fun formatDateString(dateString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())

        val date: Date? = inputFormat.parse(dateString)

        return date?.let { outputFormat.format(it) } ?: dateString
    }


    fun formatDate(inputDate: String): String {
        // Define the input format based on the given date string
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

        // Define the desired output format
        val outputFormat = SimpleDateFormat("d'th' MMMM, yyyy", Locale.getDefault())


        // Parse the input date string to a Date object
        val date = inputFormat.parse(inputDate)

        // Format the Date object to the desired date format
        return date?.let {
            // Determine the day of the month
            val dayOfMonth = SimpleDateFormat("d", Locale.getDefault()).format(it).toInt()

            // Append the correct ordinal suffix
            val dayWithSuffix = when {
                dayOfMonth in 11..13 -> "$dayOfMonth" + "th"
                dayOfMonth % 10 == 1 -> "$dayOfMonth" + "st"
                dayOfMonth % 10 == 2 -> "$dayOfMonth" + "nd"
                dayOfMonth % 10 == 3 -> "$dayOfMonth" + "rd"
                else -> "$dayOfMonth" + "th"
            }

            // Replace "d'th'" with the correctly formatted day
            outputFormat.format(it).replace("d'th'", dayWithSuffix)
        } ?: " "
    }


    fun formatTime(inputTime: String): String { // For this Format "10:00 AM"
        // Define the input format based on the given time string
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

        // Define the desired output format
        val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

        // Parse the input time string to a Date object
        val date = inputFormat.parse(inputTime)

        // Format the Date object to the desired time format
        return date?.let {
            outputFormat.format(it)
        } ?: "Invalid time"
    }

    fun getBitmapFromUri(uri: Uri, context: Context): Bitmap {
        return MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
    }

    fun hideSystemUI(context: Activity){
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        context.window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    fun humanReadableTimeDifference(dateTime: LocalDateTime): String {
        val now = LocalDateTime.now(ZoneOffset.UTC)
        val durationSeconds = ChronoUnit.SECONDS.between(dateTime, now)
        val durationMinutes = ChronoUnit.MINUTES.between(dateTime, now)
        return when {
            durationSeconds < 60 -> when {
                durationSeconds < 10 -> "just now"
                else -> "$durationSeconds seconds ago"
            }
            durationMinutes < 60 -> "$durationMinutes minutes ago"
            durationMinutes < 1440 -> "${durationMinutes / 60} hours ago"
            durationMinutes < 10080 -> "${durationMinutes / 1440} days ago"
            else -> dateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        }
    }


    fun getCountryCode(context: Context): String {
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        // First, try to get country code from SIM card
        val simCountryIso = telephonyManager.simCountryIso.uppercase(Locale.getDefault())

        // If no SIM, get country code from the network
        val networkCountryIso = telephonyManager.networkCountryIso.uppercase(Locale.getDefault())

        // Fallback to the device's default locale country code
        val localeCountryIso = Locale.getDefault().country

        // Prioritize SIM country, then network country, and then default locale
        return when {
            simCountryIso.isNotEmpty() -> simCountryIso
            networkCountryIso.isNotEmpty() -> networkCountryIso
            else -> localeCountryIso
        }
    }

    fun getCountryCodeFromIso(iso: String): String {
        val phoneNumberUtil = PhoneNumberUtil.getInstance()
        val countryCode = phoneNumberUtil.getCountryCodeForRegion(iso)
        return "+$countryCode"
    }



}