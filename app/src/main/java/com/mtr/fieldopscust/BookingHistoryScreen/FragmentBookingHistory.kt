package com.mtr.fieldopscust.BookingHistoryScreen

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.Toast
import androidx.core.graphics.Insets
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mtr.fieldopscust.NotificationFragment.FragmentNotification
import com.mtr.fieldopscust.R
import com.mtr.fieldopscust.RequestHistoryViewMore.FragmentRequestHistoryViewMore
import com.mtr.fieldopscust.Utils.ApplicationHelper
import com.mtr.fieldopscust.Utils.Constants.Companion.DOMAIN_ID_KEY
import com.mtr.fieldopscust.Utils.Constants.Companion.LOGIN_PREFS
import com.mtr.fieldopscust.Utils.Constants.Companion.NO_BOOKING_HISTORY
import com.mtr.fieldopscust.Utils.Constants.Companion.TASK_STATUS_CODE
import com.mtr.fieldopscust.Utils.Constants.Companion.TOKEN_KEY
import com.mtr.fieldopscust.Utils.Constants.Companion.USER_ID_KEY
import com.mtr.fieldopscust.Utils.SessionPreferences
import com.mtr.fieldopscust.databinding.ActivityBookingHistoryBinding
import com.mtr.fieldopscust.network.request.Job
import com.mtr.fieldopscust.network.request.RequestHistoryResponse
import com.mtr.fieldopsemp.network.ApiClientProxy
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

class FragmentBookingHistory : Fragment(), AdapterBookingHistory.OnBookingHistoryClickListener {

    private var _binding: ActivityBookingHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var bookingHistoryList: List<Job>
    private lateinit var adapterBookingHistory: AdapterBookingHistory
    private var rvBookingHistory: RecyclerView? = null
    private var sharedSessionPreferences: SessionPreferences? = null
    private var userId: Int? = null
    private var DOMAIN_ID: Int? = null
    private var userToken: String? = null
    private var disposable: Disposable? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = ActivityBookingHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val coroutineScope = CoroutineScope(Dispatchers.Main)

        coroutineScope.launch {
            delay(2000)
            hideSystemUI()
        }

        binding.imageViewBackButtonBookingHistory.setOnClickListener {
            onClickBackButton()
        }

        binding.imageViewFilterStatus.setOnClickListener {
            showDialog()
        }

        binding.imgButtonAlertBookingHistPg.setOnClickListener{
            notificationButton()
        }

        binding.editTextSearchForBooking.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        sharedSessionPreferences = ApplicationHelper.getAppController()?.loginPrefs(LOGIN_PREFS)
        sharedSessionPreferences?.let {
            userToken = it.getString(TOKEN_KEY, null)
            userId = it.getInt(USER_ID_KEY, 0)
            DOMAIN_ID = it.getInt(DOMAIN_ID_KEY, 1)
        }

        ViewCompat.setOnApplyWindowInsetsListener(
            binding.main, OnApplyWindowInsetsListener { v: View, insets: WindowInsetsCompat ->
                val systemBars: Insets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        )

        fetchBookingHistory(userId, userToken, TASK_STATUS_CODE, DOMAIN_ID!!)
    }

    private fun fetchBookingHistory(userId: Int?, userToken: String?, taskStatusId: Int?, domainId: Int) {
        val bearerToken = "bearer $userToken"
        binding.progressBarBookingHistory.visibility = View.VISIBLE
        binding.txtLoadingDetailsBookingHistory.visibility = View.VISIBLE

        disposable = ApiClientProxy.getBookingHistory(userId!!, taskStatusId!!, bearerToken, domainId)
            .retryWhen { error ->
                error.zipWith(Observable.range(1, 3)) { error, retryCount ->
                    if (error is SocketTimeoutException && retryCount < 3) {
                        Observable.timer(2, TimeUnit.SECONDS) // Wait for 2 seconds before retrying
                    } else {
                        Observable.error<Throwable>(error)
                    }
                }.flatMap { it }
            }.subscribe(this::onFetchBookingHistorySuccess, this::onFetchBookingHistoryFailure)
    }

    private fun onFetchBookingHistorySuccess(bookingHistoryResponse: RequestHistoryResponse) {
        binding.progressBarBookingHistory.visibility = View.GONE
        binding.txtLoadingDetailsBookingHistory.visibility = View.GONE

        if (bookingHistoryResponse.isSuccess) {
            bookingHistoryList = bookingHistoryResponse.result
            if (bookingHistoryList.isNotEmpty()) {
                rvBookingHistory = binding.rvBookingHistory
                rvBookingHistory?.layoutManager = LinearLayoutManager(requireContext())
                adapterBookingHistory = AdapterBookingHistory(bookingHistoryList, this)
                rvBookingHistory?.adapter = adapterBookingHistory
                Toast.makeText(requireContext(), "Booking History fetched successfully", Toast.LENGTH_SHORT).show()
            } else {
                binding.txtLoadingDetailsBookingHistory.text = NO_BOOKING_HISTORY
                binding.txtLoadingDetailsBookingHistory.visibility = View.VISIBLE
                (rvBookingHistory?.adapter as? AdapterBookingHistory)?.updateData(bookingHistoryList)
            }
        } else {
            binding.txtLoadingDetailsBookingHistory.text = NO_BOOKING_HISTORY
            binding.txtLoadingDetailsBookingHistory.visibility = View.VISIBLE
            Toast.makeText(requireContext(), "Booking History fetch failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onClickBackButton() {
        val fragmentManager: FragmentManager = parentFragmentManager

        if (fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStack()
        } else {
            requireActivity().onBackPressed()
        }
    }

    private fun filter(query: String) {
        if (bookingHistoryList.isEmpty()){
            return
        } else {
            val filteredList =bookingHistoryList.filter {
                it.name.contains(query, ignoreCase = true)
            }
            adapterBookingHistory.updateList(filteredList)
        }

    }

    private fun filterStatus(query: String) {
        if(query.isEmpty()){
            if (bookingHistoryList.isEmpty()){
                return
            } else {
                binding.txtLoadingDetailsBookingHistory.visibility = View.GONE
                adapterBookingHistory.updateList(bookingHistoryList)
                return
            }
        } else {
            if (bookingHistoryList.isEmpty()){
                return
            } else {
                val filteredList =bookingHistoryList.filter {
                    it.status.contains(query, ignoreCase = true)
                }
                if (filteredList.isEmpty()){
                    binding.txtLoadingDetailsBookingHistory.text = NO_BOOKING_HISTORY
                    binding.txtLoadingDetailsBookingHistory.visibility = View.VISIBLE
                    adapterBookingHistory.updateList(filteredList)
                } else {
                    binding.txtLoadingDetailsBookingHistory.visibility = View.GONE
                    adapterBookingHistory.updateList(filteredList)
                }
            }
        }
    }

    private fun onFetchBookingHistoryFailure(throwable: Throwable?) {
        binding.progressBarBookingHistory.visibility = View.GONE
        binding.txtLoadingDetailsBookingHistory.visibility = View.GONE
        Toast.makeText(requireContext(), "Error Occurred", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposable?.dispose()
        _binding = null
    }

    override fun onHistoryClick(job: Job) {
        val reviewsList = ArrayList(job.assignedTo.reviews)
        val bundle = Bundle().apply {
            putString("service_name", job.name)
            putString("view_status", job.viewStatus)
            putString("status", job.status)
            putString("updated_at", job.updatedAt)
            putString("service_description", job.description)
            putString("documents", job.documents)
//            putStringArrayList("reviews_list", reviewsList)

            // For Service Provider
            putInt("service_provider_id", job.assignedTo.id)
            putString("service_provider_name", job.assignedTo.name)
            putString("service_provider_email", job.assignedTo.email)
            putString("service_provider_profile_url", job.assignedTo.profileUrl)

            // For Status Bottom Sheet Status
            // Work Start Time
            putString("work_start_time", job.workStartTime)
            // Work Complete Time
            putString("work_complete_time", job.workCompleteTime)

            // Assigned To Details
            putString("profile_url", job.assignedTo.profileUrl)
            putString("assigned_to_name", job.assignedTo.name)
            putString("assigned_date", job.assignedTo.createdAt)

            // Viewed By Details
            putString("viewed_date", job.viewedTime)
            putString("viewedBy_to_name", job.viewedBy.name)

            // For Reviews

        }

        val fragment = FragmentRequestHistoryViewMore()
        fragment.arguments = bundle

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    fun showDialog() {
        val dialog = Dialog(requireContext()).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.dialog_select_status)
        }

        dialog.findViewById<View>(R.id.txt_all_status).setOnClickListener {
            filterStatus("")
//            fetchBookingHistory(userId, userToken, 0, DOMAIN_ID)
            dialog.dismiss()
        }

        dialog.findViewById<View>(R.id.txt_status_created).setOnClickListener {
            filterStatus("Requested")
//            fetchBookingHistory(userId, userToken, 1, DOMAIN_ID)
            dialog.dismiss()
        }

        dialog.findViewById<View>(R.id.txt_status_pending).setOnClickListener {
            filterStatus("Pending")
//            fetchBookingHistory(userId, userToken, 2, DOMAIN_ID)
            dialog.dismiss()
        }

        dialog.findViewById<View>(R.id.txt_status_InProgress).setOnClickListener {
            filterStatus("Inprogress")
//            fetchBookingHistory(userId, userToken, 3, DOMAIN_ID)
            dialog.dismiss()
        }

        dialog.findViewById<View>(R.id.txt_status_Completed).setOnClickListener {
            filterStatus("Completed")
//            fetchBookingHistory(userId, userToken, 4, DOMAIN_ID)
            dialog.dismiss()
        }

        dialog.findViewById<View>(R.id.close_button).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window?.setGravity(Gravity.BOTTOM)
    }

    private fun notificationButton(){
        val fragmentManager: FragmentManager = parentFragmentManager
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, FragmentNotification())
        transaction.addToBackStack(null)
        transaction.commit()
    }


    private fun hideSystemUI() {
        // Hide system UI logic here for fragments, if needed.
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        disposable?.dispose()
    }
}
