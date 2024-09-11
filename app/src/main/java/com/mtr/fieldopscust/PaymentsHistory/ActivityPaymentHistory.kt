package com.mtr.fieldopscust.PaymentsHistory

import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.mtr.fieldopscust.NotificationFragment.FragmentNotification
import com.mtr.fieldopscust.R
import com.mtr.fieldopscust.Utils.ApplicationHelper
import com.mtr.fieldopscust.Utils.ApplicationHelper.hideSystemUI
import com.mtr.fieldopscust.Utils.Constants.Companion.DOMAIN_ID_KEY
import com.mtr.fieldopscust.Utils.Constants.Companion.EMPTY_TRANSACTION_HISTORY
import com.mtr.fieldopscust.Utils.Constants.Companion.LOGIN_PREFS
import com.mtr.fieldopscust.Utils.Constants.Companion.TOKEN_KEY
import com.mtr.fieldopscust.Utils.SessionPreferences
import com.mtr.fieldopscust.databinding.ActivityPaymentHistoryBinding
import com.mtr.fieldopscust.network.request.GetTransactionHistoryResponse
import com.mtr.fieldopscust.network.request.History
import com.mtr.fieldopsemp.network.ApiClientProxy
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

class ActivityPaymentHistory : AppCompatActivity() {
    lateinit var binding: ActivityPaymentHistoryBinding
    private var disposable: Disposable?=null
    private var token: String?=null
    private var DOMAIN_ID: Int?=null
    private var sharedSessionPrefs: SessionPreferences?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding = ActivityPaymentHistoryBinding.inflate(getLayoutInflater())
        setContentView(binding.getRoot())
        hideSystemUI(this@ActivityPaymentHistory)
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        coroutineScope.launch {
            delay(2000)
            hideSystemUI(this@ActivityPaymentHistory)
        }
        sharedSessionPrefs = ApplicationHelper.getAppController()?.loginPrefs(LOGIN_PREFS)
        token = sharedSessionPrefs?.getString(TOKEN_KEY, null)
        DOMAIN_ID = sharedSessionPrefs?.getInt(DOMAIN_ID_KEY, 1)
        if (token != null){
            fetchPaymentHistory(DOMAIN_ID!!, token!!)
        }

        binding.imgButtonAlertPaymentHistPage.setOnClickListener{
            notificationButton()
        }
        binding.imageViewBackButton.setOnClickListener{
            onClickBackButton()
        }

    }

    private fun fetchPaymentHistory(domainId: Int, token: String) {
        val bearerToken = "bearer $token"
        binding.progressBarPaymentHistActivity.visibility = View.VISIBLE
        binding.loadingDetailsTxtPaymentHistActivity.visibility = View.VISIBLE

        disposable = ApiClientProxy.getTransactionHistory(domainId, bearerToken)
            .retryWhen { error ->
                error.zipWith(Observable.range(1, 3)) { error, retryCount ->
                    if (error is SocketTimeoutException && retryCount < 3) {
                        Observable.timer(2, TimeUnit.SECONDS) // Wait for 2 seconds before retrying
                    } else {
                        Observable.error<Throwable>(error)
                    }
                }.flatMap { it }
            }
            .subscribe(this::onPaymentHistorySuccess, this::onPaymentHistoryFailure)
    }

    private fun onPaymentHistorySuccess(getTransactionHistoryResponse: GetTransactionHistoryResponse?) {
        binding.progressBarPaymentHistActivity.visibility = View.GONE
        binding.loadingDetailsTxtPaymentHistActivity.visibility = View.GONE
        if (getTransactionHistoryResponse != null) {
            if (getTransactionHistoryResponse.isSuccess){
                binding.progressBarPaymentHistActivity.visibility = View.GONE
                binding.loadingDetailsTxtPaymentHistActivity.visibility = View.GONE
                Toast.makeText(this, "Transaction History fetched successfully", Toast.LENGTH_SHORT).show()
                var transactionHistoryList: List<History> = getTransactionHistoryResponse.result
                binding.rvPaymentHistory.layoutManager = LinearLayoutManager(this)
                if(transactionHistoryList.isNotEmpty()){
                    binding.rvPaymentHistory.adapter = AdapterPaymentHistoryItems(transactionHistoryList)

                } else {
                    binding.progressBarPaymentHistActivity.visibility = View.GONE
                    binding.loadingDetailsTxtPaymentHistActivity.text = EMPTY_TRANSACTION_HISTORY
                    binding.loadingDetailsTxtPaymentHistActivity.visibility = View.VISIBLE
                    Toast.makeText(this, "No transaction history found", Toast.LENGTH_SHORT).show()
                }

            } else{
                binding.progressBarPaymentHistActivity.visibility = View.GONE
                binding.loadingDetailsTxtPaymentHistActivity.text = EMPTY_TRANSACTION_HISTORY
                binding.loadingDetailsTxtPaymentHistActivity.visibility = View.VISIBLE
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun onPaymentHistoryFailure(throwable: Throwable?) {
        binding.progressBarPaymentHistActivity.visibility = View.GONE
        binding.loadingDetailsTxtPaymentHistActivity.text = EMPTY_TRANSACTION_HISTORY
        binding.loadingDetailsTxtPaymentHistActivity.visibility = View.VISIBLE
        Toast.makeText(this, "Transaction History fetch failed", Toast.LENGTH_SHORT).show()
    }

    private fun notificationButton(){
        val fragmentManager: FragmentManager = supportFragmentManager
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, FragmentNotification())
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun onClickBackButton() {
        val fragmentManager: FragmentManager = supportFragmentManager

        if (fragmentManager.backStackEntryCount > 0) {
            // Navigate to the previous fragment
            fragmentManager.popBackStack()
        } else {
            // If no fragments in the back stack, close the fragment or handle accordingly
            this.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }

}