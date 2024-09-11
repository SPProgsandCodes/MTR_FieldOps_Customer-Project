package com.mtr.fieldopscust.Utils

interface Constants {
    companion object {
        const val LOGIN_PREFS = "MTRFieldOpsCustomerPrefs"
        const val IS_LOGIN = "ISLOGIN"
        const val USERNAME = "userName"
        const val PASSWORD = "password"
        const val FILE_REQUEST_KEY = "MTRFieldOpsCustomerPrefsFileRequest"
        const val TOKEN_KEY = "MTRFieldOpsCustomerPrefs"
        const val USER_ID_KEY = "MTRFieldOpsCustomerPrefs1"
        const val USER_FIRST_NAME = "MTRFieldOpsCustomerPrefsFirstName"
        const val USER_MIDDLE_NAME = "MTRFieldOpsCustomerPrefsMiddleName"
        const val USER_LAST_NAME = "MTRFieldOpsCustomerPrefsLastName"
        const val USER_PHONE_NUMBER = "MTRFieldOpsCustomerPrefsPhoneNumber"
        const val DOMAIN_ID_KEY = "domainId"
        const val USER_EMAIL = "MTRFieldOpsCustomerPrefsEmailId"
        const val USER_PASSWORD = "MTRFieldOpsCustomerPrefsPassword"
        const val USER_GET_PROFILE_PIC = "MTRFieldOpsCustomerPrefsProfilePic"
        const val USER_PROFILE_RATINGS = "MTRFieldOpsCustomerPrefsRatings"
        const val USER_SERVICES_REQUESTED = "MTRFieldOpsCustomerPrefsServicesRequested"
        const val USER_TOTAL_MONEY_SPENT = "MTRFieldOpsCustomerPrefsTotalMoneySpent"

        // For Intents
        const val INTENT_MESSAGE_SEND_TO_USER_ID = "MTRFieldOpsCustomerPrefsSendTo"
        const val INTENT_MESSAGE_SEND_USER_FULL_NAME = "MTRFieldOpsCustomerPrefsFullName"
        const val PICK_FILE_REQUEST = 1
        const val TASK_STATUS_CODE = 0

        // For Empty Responses
        const val EMPTY_TRANSACTION_HISTORY = "No transaction history found"
        const val NO_BOOKING_HISTORY = "No Booking history found"
        const val NO_REQUEST_HISTORY = "No Requests found"
        const val NO_NEW_MESSAGES = "No new messages"
        const val NO_NEW_NOTIFICATIONS = "No new notifications"


    }
}