package com.mtr.fieldopscust.PaymentsHistory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
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

class FragmentPaymentHistory : Fragment() {
    private lateinit var binding: ActivityPaymentHistoryBinding
    private var paymentHistoryDisposable: Disposable? = null
    private var token: String? = null
    private var DOMAIN_ID: Int? = null
    private var sharedSessionPrefs: SessionPreferences? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ActivityPaymentHistoryBinding.inflate(inflater, container, false)
        hideSystemUI(requireActivity())

        val coroutineScope = CoroutineScope(Dispatchers.Main)
        coroutineScope.launch {
            delay(2000)
            hideSystemUI(requireActivity())
        }

        sharedSessionPrefs = ApplicationHelper.getAppController()?.loginPrefs(LOGIN_PREFS)
        token = sharedSessionPrefs?.getString(TOKEN_KEY, null)
        DOMAIN_ID = sharedSessionPrefs?.getInt(DOMAIN_ID_KEY, 1)

        if (token != null) {
            fetchPaymentHistory(DOMAIN_ID!!, token!!)
        }

        binding.imgButtonAlertPaymentHistPage.setOnClickListener {
            notificationButton()
        }

        binding.imageViewBackButton.setOnClickListener {
            onClickBackButton()
        }

        return binding.root
    }

    private fun fetchPaymentHistory(domainId: Int, token: String) {
        val bearerToken = "bearer $token"
        binding.progressBarPaymentHistActivity.visibility = View.VISIBLE
        binding.loadingDetailsTxtPaymentHistActivity.visibility = View.VISIBLE

        paymentHistoryDisposable = ApiClientProxy.getTransactionHistory(domainId, bearerToken)
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
            if (getTransactionHistoryResponse.isSuccess) {
                Toast.makeText(requireContext(), "Transaction History fetched successfully", Toast.LENGTH_SHORT).show()
                val transactionHistoryList: List<History> = getTransactionHistoryResponse.result
                binding.rvPaymentHistory.layoutManager = LinearLayoutManager(requireContext())
                if (transactionHistoryList.isNotEmpty()) {
                    binding.rvPaymentHistory.adapter = AdapterPaymentHistoryItems(transactionHistoryList)
                } else {
                    binding.loadingDetailsTxtPaymentHistActivity.text = EMPTY_TRANSACTION_HISTORY
                    binding.loadingDetailsTxtPaymentHistActivity.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), "No transaction history found", Toast.LENGTH_SHORT).show()
                }

            } else {
                binding.loadingDetailsTxtPaymentHistActivity.text = EMPTY_TRANSACTION_HISTORY
                binding.loadingDetailsTxtPaymentHistActivity.visibility = View.VISIBLE
                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onPaymentHistoryFailure(throwable: Throwable?) {
        binding.progressBarPaymentHistActivity.visibility = View.GONE
        binding.loadingDetailsTxtPaymentHistActivity.text = EMPTY_TRANSACTION_HISTORY
        binding.loadingDetailsTxtPaymentHistActivity.visibility = View.VISIBLE
        Toast.makeText(requireContext(), "Transaction History fetch failed", Toast.LENGTH_SHORT).show()
    }

    private fun notificationButton() {
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, FragmentNotification())
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun onClickBackButton() {
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager

        if (fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStack()
        } else {
            requireActivity().onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        paymentHistoryDisposable?.dispose()
    }
}
