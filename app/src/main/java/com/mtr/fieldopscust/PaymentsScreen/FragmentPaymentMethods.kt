package com.mtr.fieldopscust.PaymentsScreen


import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.mtr.fieldopscust.LoginScreen.ActivityLogin
import com.mtr.fieldopscust.NetworkUtil
import com.mtr.fieldopscust.NotificationFragment.FragmentNotification
import com.mtr.fieldopscust.PaymentsHistory.AdapterPaymentHistoryItems
import com.mtr.fieldopscust.PaymentsHistory.FragmentPaymentHistory
import com.mtr.fieldopscust.R
import com.mtr.fieldopscust.Utils.ApplicationHelper
import com.mtr.fieldopscust.Utils.Constants.Companion.DOMAIN_ID_KEY
import com.mtr.fieldopscust.Utils.Constants.Companion.EMPTY_TRANSACTION_HISTORY
import com.mtr.fieldopscust.Utils.Constants.Companion.IS_LOGIN
import com.mtr.fieldopscust.Utils.Constants.Companion.LOGIN_PREFS
import com.mtr.fieldopscust.Utils.Constants.Companion.TOKEN_KEY
import com.mtr.fieldopscust.Utils.Constants.Companion.USER_ID_KEY
import com.mtr.fieldopscust.Utils.ObjectUtil
import com.mtr.fieldopscust.databinding.FragmentPaymentMethodsBinding
import com.mtr.fieldopscust.network.request.GetTransactionHistoryResponse
import com.mtr.fieldopscust.network.request.History
import com.mtr.fieldopscust.network.request.PaymentIntentResponse
import com.mtr.fieldopscust.network.request.PaymentUpdateResponse
import com.mtr.fieldopscust.network.request.WalletBalanceResponse
import com.mtr.fieldopsemp.network.ApiClientProxy
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit


class FragmentPaymentMethods : Fragment() {
    lateinit var binding: FragmentPaymentMethodsBinding
    private var sharedSessionPrefs: SharedPreferences? = null
    private var token: String? = null
    private var userId: Int? = null
    private var DOMAIN_ID: Int? = null
    private var paymentsScreenDisposable: Disposable? = null
    private var paymentIntentClient: String? = null
    private var intentId: String? = null
    private var Addamo: String? = null
    private var paymentSheet: PaymentSheet? = null
    private var configuration: PaymentSheet.CustomerConfiguration? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentPaymentMethodsBinding.inflate(inflater, container, false)
        sharedSessionPrefs = ApplicationHelper.getAppController()?.loginPrefs(LOGIN_PREFS)
        token = sharedSessionPrefs?.getString(TOKEN_KEY, null)
        userId = sharedSessionPrefs?.getInt(USER_ID_KEY, 0)
        DOMAIN_ID = sharedSessionPrefs?.getInt(DOMAIN_ID_KEY, 1)

        paymentSheet = PaymentSheet(
            this
        ) { paymentSheetResult: PaymentSheetResult? ->
            this.onPaymentSheetResult(
                paymentSheetResult!!
            )
        }

        if (token != null && checkNetworkConnection()) {
            fetchTransactions(DOMAIN_ID!!, token!!)
            fetchWalletBalance()
        }else{
            dialogNoInternet()
        }



        binding.imgButtonAlertPaymentMethodPg.setOnClickListener {
            if (checkNetworkConnection()){
                notificationButton()
            } else {
                dialogNoInternet()
            }

        }

        binding.btnAddMoney.setOnClickListener {
            if (checkNetworkConnection()){
                addMoney()
            } else {
                dialogNoInternet()
            }

        }

        return binding.getRoot()
    }

    private fun createPayIntent(userId: Int?, token: String?, amount: Int?, currency: String?) {
        val bearerToken = "bearer $token"
        paymentsScreenDisposable =
            ApiClientProxy.createPaymentIntent(DOMAIN_ID!!, bearerToken, amount!!, currency!!) .retryWhen { error ->
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
            }.subscribe(this::onCreatePayIntentSuccess, this::onCreatePayIntentFailure)
    }

    private fun onCreatePayIntentSuccess(paymentIntentResponse: PaymentIntentResponse?) {
        if (paymentIntentResponse!!.isSuccess) {
            paymentIntentClient = paymentIntentResponse.result.paymentIntent
            val publishableKey = paymentIntentResponse.result.publishableKey
            val customerId = paymentIntentResponse.result.customer
            val ephemeralKeySecret = paymentIntentResponse.result.ephemeralKey
            intentId = paymentIntentResponse.result.intentId
            // Set the CustomerConfiguration here
            configuration = PaymentSheet.CustomerConfiguration(
                customerId,
                ephemeralKeySecret
            )
            Log.d("Payment", "PaymentIntentClient: $paymentIntentClient")
            Log.d("Payment", "CustomerId: ${configuration?.id}")

            PaymentConfiguration.init(requireContext(), publishableKey)

            payWithCard()

        } else {
            Toast.makeText(requireContext(), "Payment Intent Unsuccessful", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun onCreatePayIntentFailure(throwable: Throwable?) {
        Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
    }

    private fun addMoney() {
        try {
            val dialog1 = Dialog(requireContext())
            dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog1.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog1.setContentView(R.layout.dialog_add_credit)
            dialog1.setCancelable(false)

            val tvContinue = dialog1.findViewById<TextView>(R.id.tv_done)
            val tvCancel = dialog1.findViewById<TextView>(R.id.tv_cancel)
            val edAmount = dialog1.findViewById<EditText>(R.id.edAmount)

            edAmount.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    charSequence: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    // This method is called before the text is changed.
                }

                override fun onTextChanged(
                    charSequence: CharSequence,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    // This method is called during the text changing process.
                }

                override fun afterTextChanged(editable: Editable) {
                    // This method is called after the text has changed.

                    val inputText = editable.toString()

                    // Check if the input contains a digit
                    if (!containsDigit(inputText)) {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.please_enter_amount_in_valid_format),
                            Toast.LENGTH_SHORT
                        ).show()
                        edAmount.text.clear()
                    }
                }
            })

            tvCancel.setOnClickListener {
                dialog1.dismiss()
            }

            tvContinue.setOnClickListener {
                if (ObjectUtil.isEmptyStr(ObjectUtil.getTextFromView(edAmount))) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.please_enter_amount),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    dialog1.dismiss()

                    val amountStr = edAmount.text.toString()
                    val amount = amountStr.toDouble()
                    val amountWithFormat = String.format("%.2f", amount)
                    Addamo = amountWithFormat
                    val amountClear = amountWithFormat.replace(".", "")
                    val stripeAmount = amountClear.toInt()
                    createPayIntent(userId, token, stripeAmount, "usd")
                }
            }

            dialog1.show()

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun payWithCard() {
        paymentIntentClient?.let {
            paymentSheet!!.presentWithPaymentIntent(it, PaymentSheet.Configuration("Field Ops Customer", configuration))
        }
    }

    private fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
        when (paymentSheetResult) {
            is PaymentSheetResult.Completed -> {
                // Payment succeeded
                Toast.makeText(requireContext(), "Payment successful!", Toast.LENGTH_LONG).show()
                val bearerToken = "bearer $token"
                fetchTransactions(DOMAIN_ID!!, token!!)
                updatePaymentStatus()
            }

            is PaymentSheetResult.Canceled -> {
                // Payment was canceled
                Toast.makeText(requireContext(), "Payment canceled!", Toast.LENGTH_LONG).show()
            }

            is PaymentSheetResult.Failed -> {
                // Payment failed, handle the error
                Toast.makeText(
                    requireContext(),
                    "Payment failed",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun updatePaymentStatus() {
        val bearerToken = "bearer $token"
        paymentsScreenDisposable = ApiClientProxy.updatePaymentStatus(userId!!, bearerToken,0, intentId!!) .retryWhen { error ->
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
        }.subscribe(this::onUpdatePaymentStatusSuccess, this::onUpdatePaymentStatusError)
    }

    private fun onUpdatePaymentStatusError(throwable: Throwable?) {
        Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
    }

    private fun onUpdatePaymentStatusSuccess(paymentUpdateResponse: PaymentUpdateResponse?) {
        if (paymentUpdateResponse!!.isSuccess){
            fetchWalletBalance()
            //refreshFragment()
        }
    }

    private fun fetchWalletBalance() {
        val bearerToken = "bearer $token"
        paymentsScreenDisposable = ApiClientProxy.getWalletBalance( DOMAIN_ID!!,"usd" , bearerToken)
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
            }.subscribe(
            this::onFetchWalletBalanceSuccess, this::onFetchWalletBalanceError)
    }

    private fun onFetchWalletBalanceError(throwable: Throwable?) {
        Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
    }

    private fun onFetchWalletBalanceSuccess(walletBalanceResponse: WalletBalanceResponse?) {
        if (walletBalanceResponse!!.isSuccess){
            if (walletBalanceResponse.result==0){
                binding.tvWalletBalance.text = "$0.00"
            }else{
                binding.tvWalletBalance.text = "$"+walletBalanceResponse.result.toString()
            }

        }
    }

    private fun containsDigit(text: String): Boolean {
        for (c in text.toCharArray()) {
            if (Character.isDigit(c)) {
                return true
            }
        }
        return false
    }

    private fun fetchTransactions(domainId: Int, token: String) {
        val bearerToken = "bearer $token"
        paymentsScreenDisposable = ApiClientProxy.getTransactionHistory(domainId, bearerToken)
            .retryWhen { error ->
                error.zipWith(Observable.range(1, 3)) { error, retryCount ->
                    if (error is SocketTimeoutException && retryCount < 3) {
                        Observable.timer(2, TimeUnit.SECONDS) // Wait for 2 seconds before retrying
                    } else {
                        Observable.error<Throwable>(error)
                    }
                }.flatMap { it }
            }.subscribe(this::onFetchTransactionsSuccess, this::onFetchTransactionsError)
    }

    private fun onFetchTransactionsSuccess(getTransactionHistoryResponse: GetTransactionHistoryResponse?) {
        binding.progressBarPaymentHistFragment.visibility = View.GONE
        binding.loadingDetailsTxtPaymentHistFragment.visibility = View.GONE
        if (getTransactionHistoryResponse != null) {
            if (getTransactionHistoryResponse.isSuccess) {
                binding.progressBarPaymentHistFragment.visibility = View.GONE
                binding.loadingDetailsTxtPaymentHistFragment.visibility = View.GONE
                val transactionHistoryList: List<History> = getTransactionHistoryResponse.result
                if (isAdded) {

                    binding.rvPaymentMethodHistory.layoutManager =
                        LinearLayoutManager(requireContext())
                }

                if (transactionHistoryList.isNotEmpty()) {
                    binding.rvPaymentMethodHistory.adapter =
                        AdapterPaymentHistoryItems(transactionHistoryList)

                } else {
                    binding.progressBarPaymentHistFragment.visibility = View.GONE
                    binding.loadingDetailsTxtPaymentHistFragment.text = EMPTY_TRANSACTION_HISTORY
                    binding.loadingDetailsTxtPaymentHistFragment.visibility = View.VISIBLE

                }

            } else {
                binding.progressBarPaymentHistFragment.visibility = View.GONE
                binding.loadingDetailsTxtPaymentHistFragment.text = EMPTY_TRANSACTION_HISTORY
                binding.loadingDetailsTxtPaymentHistFragment.visibility = View.VISIBLE
            }
        }
    }

    private fun onFetchTransactionsError(throwable: Throwable?) {
        if (isAdded) {
            binding.progressBarPaymentHistFragment.visibility = View.GONE
            binding.loadingDetailsTxtPaymentHistFragment.text = EMPTY_TRANSACTION_HISTORY
            binding.loadingDetailsTxtPaymentHistFragment.visibility = View.VISIBLE
            Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewAll.setOnClickListener {

            val fragmentManager: FragmentManager = parentFragmentManager
            val transaction: FragmentTransaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, FragmentPaymentHistory())
            transaction.addToBackStack(null)
            transaction.commit()

        }

    }

    private fun notificationButton() {
        val fragmentManager: FragmentManager = parentFragmentManager
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, FragmentNotification())
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        paymentsScreenDisposable?.dispose()
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

            sharedSessionPrefs?.edit()?.putBoolean(IS_LOGIN, false)?.apply()
            val intent = Intent(activity, ActivityLogin::class.java)
            startActivity(intent)
            if (isAdded) {
                Toast.makeText(requireContext(), "Logout Successfully", Toast.LENGTH_SHORT).show()
            }


    }


}
