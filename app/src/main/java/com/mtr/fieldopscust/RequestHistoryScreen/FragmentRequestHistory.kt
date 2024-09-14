package com.mtr.fieldopscust.RequestHistoryScreen

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mtr.fieldopscust.DialogRequestStatus.AdapterRequestStatusDialog
import com.mtr.fieldopscust.DialogRequestStatus.ModelRequestStatusDialog
import com.mtr.fieldopscust.NotificationFragment.FragmentNotification
import com.mtr.fieldopscust.R
import com.mtr.fieldopscust.Utils.ApplicationHelper
import com.mtr.fieldopscust.Utils.ApplicationHelper.hideSystemUI
import com.mtr.fieldopscust.Utils.Constants.Companion.DOMAIN_ID_KEY
import com.mtr.fieldopscust.Utils.Constants.Companion.LOGIN_PREFS
import com.mtr.fieldopscust.Utils.Constants.Companion.NO_REQUEST_HISTORY
import com.mtr.fieldopscust.Utils.Constants.Companion.TOKEN_KEY
import com.mtr.fieldopscust.Utils.Constants.Companion.USER_ID_KEY
import com.mtr.fieldopscust.Utils.SessionPreferences
import com.mtr.fieldopscust.databinding.ActivityRequestHistoryPageBinding
import com.mtr.fieldopscust.network.request.Job
import com.mtr.fieldopscust.network.request.RequestHistoryResponse
import com.mtr.fieldopscust.network.request.User
import com.mtr.fieldopsemp.network.ApiClientProxy
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

class FragmentRequestHistory : Fragment(), AdapterRequestHistory.onViewStatusClickListener {

    private lateinit var binding: ActivityRequestHistoryPageBinding
    private var rv: RecyclerView? = null
    private var disposable: Disposable? = null
    private var sharedSesssionPrefs: SessionPreferences? = null
    private var userId: Int? = null
    private var accessToken: String? = null
    private var taskId: Int? = 0
    private var DOMAIN_ID: Int? = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ActivityRequestHistoryPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Handle edge-to-edge layout and window insets
        ViewCompat.setOnApplyWindowInsetsListener(
            binding.root
        ) { v: View, insets: WindowInsetsCompat ->
            val systemBars: Insets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sharedSesssionPrefs = ApplicationHelper.getAppController()?.loginPrefs(LOGIN_PREFS)
        DOMAIN_ID = sharedSesssionPrefs?.getInt(DOMAIN_ID_KEY, 1)

        if (sharedSesssionPrefs != null) {
            userId = sharedSesssionPrefs?.getInt(USER_ID_KEY, 0)
            accessToken = sharedSesssionPrefs?.getString(TOKEN_KEY, null)
        }

        binding.imageViewBackButton.setOnClickListener {
            onClickBackButton()
        }

        binding.imgButtonAlertReqHistPg.setOnClickListener{
            notificationButton()
        }

        // Fetch request history using the userId, accessToken, taskId, and DOMAIN_ID
        fetchRequestHistory(userId!!, accessToken!!, taskId!!, DOMAIN_ID!!)
    }

    private fun fetchRequestHistory(userId: Int, accessToken: String, taskId: Int, domainId: Int) {
        binding.progressBarRequestHistory.visibility = View.VISIBLE
        binding.txtLoadingDetails.visibility = View.VISIBLE

        val bearerToken = "bearer $accessToken"
        disposable = ApiClientProxy.getRequestHistory(userId, taskId, bearerToken, domainId)
            .retryWhen { error ->
                error.zipWith(Observable.range(1, 3)) { error, retryCount ->
                    if (error is SocketTimeoutException && retryCount < 3) {
                        Observable.timer(2, TimeUnit.SECONDS) // Wait for 2 seconds before retrying
                    } else {
                        Observable.error<Throwable>(error)
                    }
                }.flatMap { it }
            }.subscribe(this::onRequestHistorySuccess, this::onRequestHistoryFailure)
    }

    private fun onRequestHistorySuccess(requestHistoryResponse: RequestHistoryResponse) {
        if (requestHistoryResponse.isSuccess) {
            binding.progressBarRequestHistory.visibility = View.GONE
            binding.txtLoadingDetails.visibility = View.GONE
            val requestHistory: List<Job> = requestHistoryResponse.result
            if (requestHistory.isNotEmpty()) {
                binding.rvRequestHistory.layoutManager = LinearLayoutManager(requireContext())
                binding.rvRequestHistory.adapter = AdapterRequestHistory(requestHistory, this)
                Toast.makeText(requireContext(), "Request History Fetched Successfully", Toast.LENGTH_SHORT).show()

            } else {
                binding.progressBarRequestHistory.visibility = View.GONE
                binding.txtLoadingDetails.text = NO_REQUEST_HISTORY
                binding.txtLoadingDetails.visibility = View.VISIBLE
                Toast.makeText(requireContext(), "No Request History Found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onRequestHistoryFailure(throwable: Throwable?) {
        binding.progressBarRequestHistory.visibility = View.GONE
        binding.txtLoadingDetails.text = NO_REQUEST_HISTORY
        binding.txtLoadingDetails.visibility = View.VISIBLE
        Toast.makeText(requireContext(), "Request History Fetch Failed", Toast.LENGTH_SHORT).show()
    }

    private fun showDialog(taskId: Int, viewedBy: User, job: Job) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.fragment_request_status_bottom_sheet_dialog)

        val list: MutableList<ModelRequestStatusDialog> = ArrayList()

        if (viewedBy.name.isEmpty()) {
            list.add(
                ModelRequestStatusDialog(
                    "",
                    "",
                    "Not Viewed",
                    "Your Request has not been viewed Yet",
                    viewedBy.name,
                    false,
                    false,
                    false,
                    true,
                    false,
                    viewedBy.profileUrl!!
                )
            )
        } else {
            list.add(
                ModelRequestStatusDialog(
                    job.viewedTime!!,
                    job.viewedTime,
                    "Viewed",
                    "Request has been viewed by",
                    viewedBy.name,
                    false,
                    false,
                    false,
                    true,
                    true,
                    viewedBy.profileUrl!!
                )
            )
        }

        if (job.assignedTo.name.isEmpty()) {
            list.add(
                ModelRequestStatusDialog(
                    "",
                    "",
                    "Not Assigned",
                    "Your Request has not been Assigned",
                    viewedBy.name,
                    false,
                    false,
                    true,
                    true,
                    false,
                    job.assignedTo.profileUrl!!
                )
            )
        } else {
            val assignedTime: String = if (job.assignedTime.isNullOrEmpty()){
                ""
            } else {
                job.assignedTime
            }
            list.add(
                ModelRequestStatusDialog(
                    assignedTime,
                    assignedTime,
                    "Assigned",
                    "Your Request has been assigned to",
                    job.assignedTo.name,
                    false,
                    false,
                    false,
                    false,
                    true,
                    job.assignedTo.profileUrl!!
                )
            )
        }

        Log.d("TAG", "Status: ${job.status}")
        val workCompleteTime: String
        val workStartTime: String
        if (job.status == "Completed"){

            workCompleteTime = if (job.workCompleteTime.isNullOrEmpty()){
                ""
            } else {
                job.workCompleteTime
            }

            workStartTime = if (job.workStartTime.isNullOrEmpty()){
                ""
            } else {
                job.workStartTime.toString()
            }

            list.add(
                ModelRequestStatusDialog(
                    workStartTime,
                    workStartTime,
                    "In Progress",
                    job.assignedTo.name,
                    "Has Started Working",
                    true,
                    false,
                    false,
                    false,
                    true,
                    job.assignedTo.profileUrl
                )
            )
            list.add(
                ModelRequestStatusDialog(
                    workCompleteTime,
                    workCompleteTime,
                    "Completed",
                    "Your Request has been completed by",
                    job.assignedTo.name,
                    false,
                    true,
                    false,
                    false,
                    true,
                    job.assignedTo.profileUrl
                )
            )
        } else if (job.status == "Inprogress") {
            workStartTime = if (job.workStartTime.isNullOrEmpty()){
                ""
            } else {
                job.workStartTime.toString()
            }
            list.add(
                ModelRequestStatusDialog(
                    workStartTime,
                    workStartTime,
                    "In Progress",
                    job.assignedTo.name,
                    "Has Started Working",
                    true,
                    true,
                    false,
                    false,
                    true,
                    job.assignedTo.profileUrl
                )
            )

        }



        rv = dialog.findViewById(R.id.request_status_rv)
        rv!!.layoutManager = LinearLayoutManager(requireContext())
        rv!!.adapter = AdapterRequestStatusDialog(requireContext(), list)

        dialog.show()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window?.setGravity(Gravity.BOTTOM)
    }

    private fun onClickBackButton() {
        val fragmentManager: FragmentManager = parentFragmentManager

        if (fragmentManager.backStackEntryCount > 0) {
            // Navigate to the previous fragment
            fragmentManager.popBackStack()
        } else {
            // If no fragments in the back stack, close the fragment or handle accordingly
            requireActivity().onBackPressed()
        }
    }

    private fun notificationButton(){
        val fragmentManager: FragmentManager = parentFragmentManager
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, FragmentNotification())
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposable?.dispose()
    }

//    override fun onRequestHistClick(job: Job) {
//        showDialog(job.id, job.viewedBy, job)
//        Log.d("TAG", "Task ID ${job.id}")
//    }

    override fun onViewStatusClick(job: Job) {
        showDialog(job.id, job.viewedBy, job)
        Log.d("TAG", "Task ID ${job.id}")
    }

}

