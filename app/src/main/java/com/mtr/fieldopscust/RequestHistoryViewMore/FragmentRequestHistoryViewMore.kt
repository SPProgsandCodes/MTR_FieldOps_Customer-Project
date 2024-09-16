package com.mtr.fieldopscust.RequestHistoryViewMore
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.Insets
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mtr.fieldopscust.ChatScreen.ChatActivity
import com.mtr.fieldopscust.DialogRequestStatus.AdapterRequestStatusDialog
import com.mtr.fieldopscust.DialogRequestStatus.ModelRequestStatusDialog
import com.mtr.fieldopscust.LoginScreen.ActivityLogin
import com.mtr.fieldopscust.NetworkUtil
import com.mtr.fieldopscust.NotificationFragment.FragmentNotification
import com.mtr.fieldopscust.R
import com.mtr.fieldopscust.Reviews.AdapterReviewsRecyclerView
import com.mtr.fieldopscust.Utils.Constants.Companion.INTENT_MESSAGE_SEND_USER_FULL_NAME
import com.mtr.fieldopscust.Utils.Constants.Companion.IS_LOGIN
import com.mtr.fieldopscust.databinding.ActivityRequestHistoryViewMoreBinding
import com.mtr.fieldopscust.network.request.Job
import com.mtr.fieldopscust.network.request.Reviews
import com.mtr.fieldopscust.network.request.User

class FragmentRequestHistoryViewMore : Fragment() {

    private var _binding: ActivityRequestHistoryViewMoreBinding? = null
    private val binding get() = _binding!!
    private lateinit var rv: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
//        rv = binding.rvRequestHistoryViewMoreReviews
        _binding = ActivityRequestHistoryViewMoreBinding.inflate(inflater, container, false)
        binding.imageViewBackButton.setOnClickListener{
            onClickBackButton()
        }

        binding.imgButtonAlertReqHistViewMorePg.setOnClickListener{

                notificationButton()


        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(
            binding.main,
            OnApplyWindowInsetsListener { v: View, insets: WindowInsetsCompat ->
                val systemBars: Insets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        )

        val bundle = arguments
        if (bundle != null) {
            val serviceName = bundle.getString("service_name")
            val viewStatus = bundle.getString("view_status")
            val status = bundle.getString("status")

            val updatedAt = bundle.getString("updated_at")
            val serviceDescription = bundle.getString("service_description")
            val documents = bundle.getString("documents")
            val reviewsList = bundle.getStringArrayList("reviews_list")

            // For Service Provider
            val profileUrl = bundle.getString("service_provider_profile_url")
            val name = bundle.getString("service_provider_name")
            val id = bundle.getInt("service_provider_id")
            val email = bundle.getString("service_provider_email")

            // For Status Bottom Sheet
            val assignedToName = if (bundle.getString("assigned_to_name").isNullOrEmpty()){
                ""
            } else {
                bundle.getString("assigned_to_name")
            }

            val assignedDate = if(bundle.getString("assigned_date").isNullOrEmpty()){
                ""
            } else {
                bundle.getString("assigned_date")
            }

            val viewedByName = if (bundle.getString("viewedBy_to_name").isNullOrEmpty()){
                ""
            } else {
                bundle.getString("viewedBy_to_name")
            }

            val viewedDate = if (bundle.getString("viewed_date").isNullOrEmpty()){
                ""
            } else {
                bundle.getString("viewed_date")
            }

            val profileUrlChatUser = if (bundle.getString("profile_url").isNullOrEmpty()){
                ""
            } else {
                bundle.getString("profile_url")
            }

            val workStartTime = if (bundle.getString("work_start_time").isNullOrEmpty()){
                ""
            } else {
                bundle.getString("work_start_time")
            }

            val workCompletedTime = if (bundle.getString("work_complete_time").isNullOrEmpty()){
                ""
            } else {
                bundle.getString("work_complete_time")
            }

            binding.btnViewStatusReqHist.setOnClickListener {
                showDialog(assignedToName!!, assignedDate!!, viewedByName!!, viewedDate!!, status!!, viewStatus!!, updatedAt!!, profileUrlChatUser!!, workStartTime!!, workCompletedTime!!)
            }

            // For Reviews
            val serviceProviderReviewsList = bundle.getStringArrayList("service_provider_reviews")
//            Example setup for RecyclerView (if reviews list is used)
//            var items: MutableList<Reviews> = ArrayList()
//            items = serviceProviderReviewsList!! // Add review items as needed

//            binding.rvRequestHistoryViewMoreReviews.layoutManager = LinearLayoutManager(requireContext())
//            binding.rvRequestHistoryViewMoreReviews.adapter = AdapterReviewsRecyclerView(items)

            Log.d("TAG", "Service Provider Name: $name")

            binding.txtHomeElectrician.text = serviceName
            if (status == "Requested" || status == "Completed"){
                binding.btnStatus.text = status
                binding.btnStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
                binding.btnStatus.background = ContextCompat.getDrawable(requireContext(), R.drawable.request_history_btn_bg)
            } else if (status == "Inprogress"){
                binding.btnStatus.text = "Ongoing"
                binding.btnStatus.background = ContextCompat.getDrawable(requireContext(), R.drawable.text_booking_hist_yellow_bg)
            } else {
                binding.btnStatus.text = status
            }

            if (serviceDescription.isNullOrEmpty()){
                binding.txtNoDescription.visibility = View.VISIBLE
            } else {
                binding.txtNoDescription.visibility = View.GONE
                binding.txtDummyDescription.text = serviceDescription
            }

            binding.combinedButton.setOnClickListener{
                val intent = Intent(requireContext(), ChatActivity::class.java)
                intent.putExtra(INTENT_MESSAGE_SEND_USER_FULL_NAME, name)
                intent.putExtra("profile_url", profileUrl)
                startActivity(intent)
            }

            

            binding.txtDummyDescription.text = serviceDescription

            Glide.with(requireContext())
                .load(documents)
                .placeholder(R.drawable.placeholder)
                .into(binding.imageViewServiceImage)

            if (reviewsList.isNullOrEmpty()) {
                binding.idTxtNoReviews.visibility = View.VISIBLE
            } else {
                Toast.makeText(requireContext(), "Reviews Fetched", Toast.LENGTH_SHORT).show()
            }

            if (name.isNullOrEmpty() && email.isNullOrEmpty()) {
                binding.relLayoutServiceProvider.visibility = View.GONE
            } else {
                loadServiceProviderDetails(profileUrl, name, id, email)
            }

        }

//
    }

    private fun loadServiceProviderDetails(

        profileUrl: String?, name: String? = "-------", id: Int?, email: String? = "-------"
    ) {
        binding.relLayoutServiceProvider.visibility = View.VISIBLE
        binding.leslieAlex.text = name
        binding.kenziLawso.text = email

        Glide.with(requireContext())
            .load(profileUrl)
            .error(R.drawable.dummy_profile)
            .placeholder(R.drawable.dummy_profile)
            .into(binding.imgProfilePic)
    }

    private fun onClickBackButton() {
        val fragmentManager: FragmentManager = parentFragmentManager

        if (fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStack()
        } else {
            requireActivity().onBackPressed()
        }
    }

    private fun showDialog(assignedToName: String, assignedDate: String, viewedByName: String, viewedDate: String, status: String, viewStatus: String, updatedAt: String, profileUrl: String, workStartTime: String, workCompleteTime: String) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.fragment_request_status_bottom_sheet_dialog)

        val list: MutableList<ModelRequestStatusDialog> = ArrayList()

        if (viewedByName.isEmpty()) {
            list.add(
                ModelRequestStatusDialog(
                    "",
                    "",
                    "Not Viewed",
                    "Your Request has not been viewed Yet",
                    viewedByName,
                    false,
                    false,
                    false,
                    true,
                    false,
                    profileUrl
                )
            )
        } else {
            list.add(
                ModelRequestStatusDialog(
                    viewedDate,
                    viewedDate,
                    "Viewed",
                    "Request has been viewed by",
                    viewedByName,
                    false,
                    false,
                    false,
                    true,
                    true,
                    profileUrl
                )
            )
        }

        if (assignedToName.isEmpty()) {
            list.add(
                ModelRequestStatusDialog(
                    "",
                    "",
                    "Not Assigned",
                    "Your Request has not been Assigned",
                    assignedToName,
                    false,
                    false,
                    true,
                    true,
                    false,
                    profileUrl
                )
            )
        } else {
            list.add(
                ModelRequestStatusDialog(
                    assignedDate,
                    assignedDate,
                    "Assigned",
                    "Your Request has been assigned to",
                    assignedToName,
                    false,
                    false,
                    false,
                    false,
                    true,
                    profileUrl
                )
            )
        }

        Log.d("TAG", "Status: ${status}")

        if(status == "Completed"){
            list.add(
                ModelRequestStatusDialog(
                    workStartTime,
                    workStartTime,
                    "In Progress",
                    assignedToName,
                    "Has Started Working",
                    true,
                    false,
                    false,
                    false,
                    true,
                    profileUrl
                )
            )
            list.add(
                ModelRequestStatusDialog(
                    workCompleteTime,
                    workCompleteTime,
                    "Completed",
                    "Your Request has been completed by",
                    assignedToName,
                    false,
                    true,
                    false,
                    false,
                    true,
                    profileUrl
                )
            )
        } else if (status == "Inprogress") {
            list.add(
                ModelRequestStatusDialog(
                    workStartTime,
                    workStartTime,
                    "In Progress",
                    assignedToName,
                    "Has Started Working",
                    true,
                    true,
                    false,
                    false,
                    true,
                    profileUrl
                )
            )
        }

        rv = dialog.findViewById(R.id.request_status_rv)
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = AdapterRequestStatusDialog(requireContext(), list)

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Prevent memory leaks

    }




}
