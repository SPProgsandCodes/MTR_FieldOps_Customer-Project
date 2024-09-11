package com.mtr.fieldopscust.DashboardScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mtr.fieldopscust.network.request.UserResponse
import com.mtr.fieldopscust.network.request.UserReviewsResponse

class ViewModelDashboard: ViewModel() {

    private val _userDetails = MutableLiveData<UserResponse?>()
    val userDetails: MutableLiveData<UserResponse?> get() = _userDetails

    private val _userReviews = MutableLiveData<UserReviewsResponse?>()
    val userReviews: LiveData<UserReviewsResponse?> get() = _userReviews

    private val _userDashboard = MutableLiveData<ModelUserDashboard?>()
    val userDashboard: LiveData<ModelUserDashboard?> get() = _userDashboard

    var FLAG = false
    var totalMoneySpent: String = "0"
    var userID: Int = 0

    fun setUserDetails(userResponse: UserResponse) {
        _userDetails.value = userResponse
    }

    fun setUserReviews(userReviewsResponse: UserReviewsResponse) {
        _userReviews.value = userReviewsResponse
    }

    fun setUserDashboard(modelUserDashboard: ModelUserDashboard) {
        _userDashboard.value = modelUserDashboard
    }

    // Method to clear the data
    fun clearData() {
        _userDetails.value = null
        _userReviews.value = null
        _userDashboard.value = null

        FLAG = false
        totalMoneySpent = "0"
        userID = 0
    }
}