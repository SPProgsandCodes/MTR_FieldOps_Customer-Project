package com.mtr.fieldopscust.DashboardScreen

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.PorterDuff
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
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.mtr.fieldopscust.BookingHistoryScreen.FragmentBookingHistory
import com.mtr.fieldopscust.LoginScreen.ActivityLogin
import com.mtr.fieldopscust.NetworkUtil
import com.mtr.fieldopscust.NotificationFragment.FragmentNotification
import com.mtr.fieldopscust.R
import com.mtr.fieldopscust.RequestHistoryScreen.FragmentRequestHistory
import com.mtr.fieldopscust.RequestServiceScreen.ActivityRequestServicePage
import com.mtr.fieldopscust.RequestServiceScreen.FragmentRequestServicePage
import com.mtr.fieldopscust.Reviews.AdapterReviewsRecyclerView
import com.mtr.fieldopscust.Utils.ApplicationHelper
import com.mtr.fieldopscust.Utils.Constants.Companion.DOMAIN_ID_KEY
import com.mtr.fieldopscust.Utils.Constants.Companion.IS_LOGIN
import com.mtr.fieldopscust.Utils.Constants.Companion.LOGIN_PREFS
import com.mtr.fieldopscust.Utils.Constants.Companion.TOKEN_KEY
import com.mtr.fieldopscust.Utils.Constants.Companion.USER_FIRST_NAME
import com.mtr.fieldopscust.Utils.Constants.Companion.USER_GET_PROFILE_PIC
import com.mtr.fieldopscust.Utils.Constants.Companion.USER_ID_KEY
import com.mtr.fieldopscust.Utils.SessionPreferences
import com.mtr.fieldopscust.databinding.FragmentHomePageBinding
import com.mtr.fieldopscust.network.request.Reviews
import com.mtr.fieldopscust.network.request.UserResponse
import com.mtr.fieldopscust.network.request.UserReviewsResponse
import com.mtr.fieldopsemp.network.ApiClientProxy
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import java.net.SocketTimeoutException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit


class FragmentHomePage : Fragment(), AdapterCategories.OnServiceCategoriesClickListener {
    lateinit var binding: FragmentHomePageBinding
    private lateinit var sharedViewModel: ViewModelDashboard
    private var sharedSesssionPrefs: SessionPreferences? = null
    private var sharedPreferences: SharedPreferences? = null
    private var token: String? = null
    private var categoriesList: List<Category>? = null
    private var categoriesAdapter: AdapterCategories? = null
    private var reviewsAdapter: AdapterReviewsRecyclerView? = null
    private var userFirstName: String? = null
    private var userName: String? = null
    private var disposable: Disposable? = null
    private var profileUrl: String? = null
    private lateinit var totalMoneySpent: String
    private var servicePageIntent: Intent? = null
    private var DOMAIN_ID: Int? = null
    private var dialogProgressBar: ProgressBar? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomePageBinding.inflate(inflater, container, false)
        sharedSesssionPrefs = ApplicationHelper.getAppController()?.loginPrefs(LOGIN_PREFS)
        sharedViewModel = ViewModelProvider(requireActivity()).get(ViewModelDashboard::class.java)
        activity?.let {
            val sharedPreferences = it.getSharedPreferences(TOKEN_KEY, Context.MODE_PRIVATE)
            token = sharedPreferences.getString(TOKEN_KEY, null)
            sharedViewModel.userID = sharedPreferences.getInt(USER_ID_KEY, 0)
            userFirstName = sharedPreferences.getString(USER_FIRST_NAME, null)
            profileUrl = sharedPreferences.getString(USER_GET_PROFILE_PIC, null)
            DOMAIN_ID = sharedPreferences.getInt(DOMAIN_ID_KEY, 1)
        }


        totalMoneySpent = "0"

        binding.profileNameTxt.text = null
        binding.ratingBar.rating = 0f
        dialogProgressBar = binding.loadCategories




        binding.ratingBar.progressDrawable.setColorFilter(
            resources.getColor(android.R.color.white, null),
            PorterDuff.Mode.SRC_IN)

        return binding.getRoot()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imgButtonAlert.setOnClickListener {
            notificationButton()
        }

        binding.homePageSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null) {
                    if (s.isEmpty()){
                        binding.layoutServices.visibility = View.VISIBLE
                        binding.layoutTotalMoneySpent.visibility = View.VISIBLE
                        binding.line25.visibility = View.VISIBLE
                        binding.frameRequestHistory.visibility = View.VISIBLE
                        binding.frameBookingHistory.visibility = View.VISIBLE
                        filter(s.toString())
                    } else {
                        binding.layoutServices.visibility = View.GONE
                        binding.layoutTotalMoneySpent.visibility = View.GONE
                        binding.line25.visibility = View.GONE
                        binding.frameRequestHistory.visibility = View.GONE
                        binding.frameBookingHistory.visibility = View.GONE
                        filter(s.toString())
                    }
                }

            }

            override fun afterTextChanged(s: Editable?) {}
        })

        if (profileUrl!=null){
//            Toast.makeText(requireContext(), "Loading Image.. ", Toast.LENGTH_SHORT).show()
            Log.d("TAG", "profile Home Page: $profileUrl")
            Glide.with(requireContext())
                .load(profileUrl)
                .placeholder(R.drawable.placeholder)
                .into(binding.dashboardProfileImage)
        } else {
            Toast.makeText(requireContext(), "Null Profile Url", Toast.LENGTH_SHORT).show()
            Glide.with(requireContext())
                .load(R.drawable.menface)
                .into(binding.dashboardProfileImage)
        }

        if (userFirstName!=null){
            binding.profileNameTxt.text = userFirstName
//            Toast.makeText(activity, "User First Name: $userFirstName", Toast.LENGTH_SHORT).show()
        } else {
            binding.profileNameTxt.text = "Mitchell"
        }

        sharedViewModel = ViewModelProvider(requireActivity()).get(ViewModelDashboard::class.java)
        if (checkNetworkConnection()){
            setupObservers()
            if (sharedViewModel.userDashboard.value == null) {
                Log.d("TAG", "onViewCreated: ${sharedViewModel.userDashboard.value}" )
                getUserDashboard()
            } else {
                Log.d("TAG", "onViewCreated: ${sharedViewModel.userDashboard.value}" )
                sharedViewModel.userDashboard.value?.let { onUserDashboardSuccess(it) }
            }
            if (sharedViewModel.userDetails.value == null) {
                fetchUserDetails(sharedViewModel.userID)
            } else {
//                sharedViewModel.userDetails.value?.let { onUserDetailsSuccess(it) }
            }
            if (sharedViewModel.userReviews.value == null) {
                fetchUserReviews(sharedViewModel.userID)
            } else {
                sharedViewModel.userReviews.value?.let { onUserReviewsSuccess(it) }
            }
        } else {
            dialogNoInternet()
        }


        // Get the current date
        val calendar = Calendar.getInstance()
        val dayOfWeek = SimpleDateFormat("EEEE", Locale.getDefault()).format(calendar.time)
        val month = SimpleDateFormat("MMMM", Locale.getDefault()).format(calendar.time)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val year = calendar.get(Calendar.YEAR)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)


        val greetingMessage = getGreetingMessage(hour)
        binding.txtGreetMsg.text = greetingMessage
        // Format the date
        val formattedDate = "$dayOfWeek, $month ${getDayWithSuffix(day)}, $year"
        binding.mondayMarc.text = formattedDate



        binding.imgViewEye.setImageResource(R.drawable.vector_passwd_eye)
        binding.sampleAmount.text = "$" + "--------"

        binding.imgViewEye.setOnClickListener(View.OnClickListener {
            if (sharedViewModel.FLAG) {
                binding.imgViewEye.setImageResource(R.drawable.vector_passwd_eye)
                binding.sampleAmount.text = "$--------"
                sharedViewModel.FLAG = false
            } else {
                binding.imgViewEye.setImageResource(R.drawable.vector_eye_visible)
                binding.sampleAmount.text = "$${sharedViewModel.totalMoneySpent}"
                sharedViewModel.FLAG = true
            }
        })

        binding.frameRequestHistory.setOnClickListener(View.OnClickListener {
            val fragmentManager: FragmentManager = parentFragmentManager
            val transaction: FragmentTransaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, FragmentRequestHistory())
            transaction.addToBackStack(null)
            transaction.commit()
        })

        binding.frameBookingHistory.setOnClickListener(View.OnClickListener {
            val fragmentManager: FragmentManager = parentFragmentManager
            val transaction: FragmentTransaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, FragmentBookingHistory())
            transaction.addToBackStack(null)
            transaction.commit()
//            val intent = Intent(activity, ActivityBookingHistory::class.java)
//            startActivity(intent)
        })


        servicePageIntent = Intent(activity, ActivityRequestServicePage::class.java)
        binding.frame154.setOnClickListener(View.OnClickListener {
            val fragmentManager: FragmentManager = parentFragmentManager
            val transaction: FragmentTransaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, FragmentRequestServicePage())
            transaction.addToBackStack(null)
            transaction.commit()
//            startActivity(servicePageIntent!!)

        })


        binding.logoutIconHomepage.setOnClickListener {
            showSignOutPopup()
        }
    }


    // For Fetching User Dashboard Categories
    private fun getUserDashboard() {
        val bearerToken = "bearer $token"
        dialogProgressBar?.visibility = View.VISIBLE
        binding.txtLoadingDetailsCategories.visibility = View.VISIBLE
        disposable = ApiClientProxy.getUserDashboard(bearerToken, DOMAIN_ID!!)
            .retryWhen { error ->
                error.zipWith(Observable.range(1, 3)) { error, retryCount ->
                    if (error is SocketTimeoutException && retryCount < 3) {
                        Observable.timer(2, TimeUnit.SECONDS) // Wait for 2 seconds before retrying
                    } else {
                        Observable.error<Throwable>(error)
                    }
                }.flatMap { it }
            }
            .subscribe(this::onUserDashboardSuccess, this::onUserDashboardError)

    }
    private fun onUserDashboardSuccess(modelUserDashboard: ModelUserDashboard) {
        sharedViewModel.setUserDashboard(modelUserDashboard)
        dialogProgressBar?.visibility = View.GONE
        binding.txtLoadingDetailsCategories.visibility = View.GONE
        if (modelUserDashboard.isSuccess) {
            categoriesList = modelUserDashboard.result.categories
            categoriesAdapter = AdapterCategories(categoriesList!!, this)
            binding.rvCategories.layoutManager = GridLayoutManager(activity, 2)
            binding.rvCategories.adapter = categoriesAdapter
            binding.ratingBar.rating = modelUserDashboard.result.averageRating.toFloat()
            binding.txt20.text = modelUserDashboard.result.numberOfServicesRequested.toString()
            sharedViewModel.totalMoneySpent = modelUserDashboard.result.totalMoneySpent.toString()
            binding.averageRating.text = modelUserDashboard.result.averageRating.toString()
            servicePageIntent?.putStringArrayListExtra("categories_list", ArrayList(categoriesList!!.map { it.name }))

        } else {
            dialogProgressBar?.visibility = View.GONE
            binding.txtLoadingDetailsCategories.visibility = View.GONE
            if (isAdded) {
                Toast.makeText(requireContext(), modelUserDashboard.message, Toast.LENGTH_SHORT).show()
            }
            binding.sampleAmount.text = "$0"
        }
    }
    private fun onUserDashboardError(throwable: Throwable?) {
        if (throwable != null) {
            dialogProgressBar?.visibility = View.GONE
            binding.txtLoadingDetailsCategories.visibility = View.GONE
            if (isAdded) {
                Toast.makeText(requireContext(), "Error Fetching Categories", Toast.LENGTH_SHORT).show()
            }
        }
    }


    // For Fetching User Details
    private fun fetchUserDetails(userId: Int) {
        val bearerToken = "bearer $token"
        disposable = ApiClientProxy.getUserDetails(userId, bearerToken, DOMAIN_ID!!)
            .retryWhen { error ->
                error.zipWith(Observable.range(1, 3)) { error, retryCount ->
                    if (error is SocketTimeoutException && retryCount < 3) {
                        Observable.timer(2, TimeUnit.SECONDS) // Wait for 2 seconds before retrying
                    } else {
                        Observable.error<Throwable>(error)
                    }
                }.flatMap { it }
            }.subscribe(this::onUserDetailsSuccess, this::onUserDetailsError)
    }
    private fun onUserDetailsSuccess(userResponse: UserResponse) {
        sharedViewModel.setUserDetails(userResponse)
        if (userResponse.isSuccess) {
            if (userFirstName == null){
                binding.profileNameTxt.text = userResponse.result.firstName
            } else {
                binding.profileNameTxt.text = userFirstName
            }

            if (userResponse.result.profileUrl.isEmpty()){
                binding.dashboardProfileImage.setImageResource(R.drawable.menface)
            } else {
                profileUrl = userResponse.result.profileUrl
                sharedPreferences?.edit()?.putString(USER_GET_PROFILE_PIC, profileUrl)?.apply()
                Glide.with(requireContext())
                    .load(userResponse.result.profileUrl)
                    .placeholder(R.drawable.placeholder)
                    .into(binding.dashboardProfileImage)
            }


        } else {
            binding.dashboardProfileImage.setImageResource(R.drawable.menface)
            binding.profileNameTxt.text = "Mitchell"
            Toast.makeText(requireContext(), userResponse.message, Toast.LENGTH_SHORT).show()
        }
    }
    private fun onUserDetailsError(throwable: Throwable?) {
        if (throwable != null) {
            Toast.makeText(requireContext(), "Error Fetching User Details", Toast.LENGTH_SHORT).show()
            binding.profileNameTxt.text = "Mitchell"
        }
    }


    // For Fetching Reviews
    private fun fetchUserReviews(userId: Int) {
        val bearerToken = "bearer $token"
        binding.loadReviews.visibility = View.VISIBLE
        binding.txtLoadingDetailsReviews.visibility = View.VISIBLE
        disposable = ApiClientProxy.getUserReviews(userId, bearerToken, DOMAIN_ID!!)
            .retryWhen { error ->
                error.zipWith(Observable.range(1, 3)) { error, retryCount ->
                    if (error is SocketTimeoutException && retryCount < 3) {
                        Observable.timer(2, TimeUnit.SECONDS) // Wait for 2 seconds before retrying
                    } else {
                        Observable.error<Throwable>(error)
                    }
                }.flatMap { it }
            }
            .subscribe(this::onUserReviewsSuccess, this::onUserReviewsError)
    }
    private fun onUserReviewsSuccess(userReviewsResponse: UserReviewsResponse) {
        sharedViewModel.setUserReviews(userReviewsResponse)
        if (userReviewsResponse.isSuccess) {
            binding.loadReviews.visibility = View.GONE
            binding.txtLoadingDetailsReviews.visibility = View.GONE
            val reviews: List<Reviews> = userReviewsResponse.result
            reviewsAdapter = AdapterReviewsRecyclerView(reviews)
            binding.rvReviews.layoutManager = LinearLayoutManager(activity)
            binding.rvReviews.adapter = reviewsAdapter
            if (isAdded) {
              //  Toast.makeText(requireContext(), "Reviews Fetched Successfully", Toast.LENGTH_SHORT).show()
            }
        } else {
            binding.loadReviews.visibility = View.GONE
            binding.txtLoadingDetailsReviews.visibility = View.GONE
            if (isAdded) {
                Toast.makeText(requireContext(), "Error Fetching User Details", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun onUserReviewsError(throwable: Throwable?) {
        if (throwable != null) {
            binding.loadReviews.visibility = View.GONE
            binding.txtLoadingDetailsReviews.visibility = View.GONE
            Toast.makeText(requireActivity(), "${throwable.printStackTrace()}", Toast.LENGTH_SHORT).show()
        }
    }

    // Method for search
    private fun filter(query: String) {
        val filteredList =categoriesList!!.filter {
            it.name.contains(query, ignoreCase = true)
        }

        categoriesAdapter?.updateList(filteredList)
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
            dialog.dismiss()
            if (checkNetworkConnection()) {
                getUserDashboard()
                fetchUserDetails(sharedViewModel.userID)
                fetchUserReviews(sharedViewModel.userID)
            } else {
                dialogNoInternet()
            }
        }
        dialog.show()
    }

    //Other Methods for setting Names
    private fun getDayWithSuffix(day: Int): String {
        return when {
            day in 11..13 -> {
                "${day}th"
            }

            day % 10 == 1 -> {
                "${day}st"
            }

            day % 10 == 2 -> {
                "${day}nd"
            }

            day % 10 == 3 -> {
                "${day}rd"
            }

            else -> {
                "${day}th"
            }
        }
    }
    private fun getGreetingMessage(hour: Int): String {
        return when (hour) {
            in 5..11 -> {
                "Good Morning"
            }

            in 12..16 -> {
                "Good Afternoon"
            }

            in 17..20 -> {
                "Good Evening"
            }

            else -> {
                "Good Night"
            }
        }
    }
    private fun setupObservers() {
        sharedViewModel.userDetails.observe(viewLifecycleOwner, Observer { userResponse ->
            Log.d("FragmentHomePage", "User Details: $userResponse")
            if (userResponse != null) {
                if (userResponse.isSuccess) {
                    binding.profileNameTxt.text = userResponse.result.firstName
                } else {
                    binding.profileNameTxt.text = "Mitchell"
                    Toast.makeText(activity, userResponse.message, Toast.LENGTH_SHORT).show()
                }
            }
        })

        sharedViewModel.userReviews.observe(viewLifecycleOwner, Observer { userReviewsResponse ->

            Log.d("FragmentHomePage", "User Reviews: $userReviewsResponse")
            if (userReviewsResponse != null) {
                if (userReviewsResponse.isSuccess) {
                    binding.loadReviews.visibility = View.GONE
                    val reviews: List<Reviews> = userReviewsResponse.result
                    reviewsAdapter = AdapterReviewsRecyclerView(reviews)
                    binding.rvReviews.layoutManager = LinearLayoutManager(activity)
                    binding.rvReviews.adapter = reviewsAdapter
        //                Toast.makeText(activity, "Reviews Fetched Successfully", Toast.LENGTH_SHORT).show()
                } else {
                    binding.loadReviews.visibility = View.GONE
        //                Toast.makeText(activity, "Error Fetching User Reviews", Toast.LENGTH_SHORT).show()
                }
            }
        })

        sharedViewModel.userDashboard.observe(viewLifecycleOwner, Observer { modelUserDashboard ->
            Log.d("FragmentHomePage", "User Dashboard: $modelUserDashboard")
            dialogProgressBar?.visibility = View.GONE
            if (modelUserDashboard != null) {
                if (modelUserDashboard.isSuccess) {
                    val categories: List<Category> = modelUserDashboard.result.categories
                    categoriesAdapter = AdapterCategories(categories, this)
                    binding.rvCategories.layoutManager = GridLayoutManager(activity, 2)
                    binding.rvCategories.adapter = categoriesAdapter
                    binding.ratingBar.rating = modelUserDashboard.result.averageRating.toFloat()
                    binding.txt20.text = modelUserDashboard.result.numberOfServicesRequested.toString()
                    sharedViewModel.totalMoneySpent = modelUserDashboard.result.totalMoneySpent.toString()
                    binding.averageRating.text = modelUserDashboard.result.averageRating.toString()
                } else {
                    Toast.makeText(activity, modelUserDashboard.message, Toast.LENGTH_SHORT).show()
                    binding.sampleAmount.text = "$0"
                }
            }
        })
    } // For ViewModels
    private fun showSignOutPopup() {

        val dialog = Dialog(requireActivity(),android.R.style.Theme_Material_Dialog_Alert)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.log_out_dialog)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)

        dialog.findViewById<RelativeLayout>(R.id.btnOkay)?.setOnClickListener {
            logOutApp()
            dialog.dismiss()
        }

        dialog.findViewById<RelativeLayout>(R.id.btnCancel)?.setOnClickListener {
            dialog.dismiss()
        }
        // Show the dialog
        dialog.show()

    }
    private fun logOutApp() {
        if (checkNetworkConnection()) {
            sharedSesssionPrefs?.edit()?.putBoolean(IS_LOGIN, false)?.apply()
            val intent = Intent(activity, ActivityLogin::class.java)
            startActivity(intent)
            if (isAdded) {
                Toast.makeText(requireContext(), "Logout Successfully", Toast.LENGTH_SHORT).show()
            }
        } else {
            dialogNoInternet()
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
        binding.rvCategories.adapter = null
        binding.rvReviews.adapter = null
        binding.rvCategories.layoutManager = null
        binding.rvReviews.layoutManager = null
        categoriesAdapter = null
        reviewsAdapter = null
        dialogProgressBar = null

    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()

    }

    override fun onDetach() {
        super.onDetach()
        disposable?.dispose()
    }

    override fun onCategoriesClick(categoriesList: Category) {
        val fragment = FragmentRequestServicePage()
        val bundle = Bundle()
        bundle.putInt("category_Id", categoriesList.id)// Add your data
        fragment.arguments = bundle


        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

}
