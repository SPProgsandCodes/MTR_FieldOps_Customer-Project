package com.mtr.fieldopscust.Reviews

class ModelReviews(var rName: String, var rDate: String, var rReview: String, var rImage: Int) {
    fun getrName(): String {
        return rName
    }

    fun setrName(rName: String) {
        this.rName = rName
    }

    fun getrDate(): String {
        return rDate
    }

    fun setrDate(rDate: String) {
        this.rDate = rDate
    }

    fun getrReview(): String {
        return rReview
    }

    fun setrReview(rReview: String) {
        this.rReview = rReview
    }

    fun getrImage(): Int {
        return rImage
    }

    fun setrImage(rImage: Int) {
        this.rImage = rImage
    }
}
