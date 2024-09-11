package com.mtr.fieldopscust.RequestHistoryScreen

class ModelRequestHistory(
    var vName: String,
    var vDate: String,
    var vPrice: String,
    var vImage: Int
) {
    fun getvName(): String {
        return vName
    }

    fun setvName(vName: String) {
        this.vName = vName
    }

    fun getvDate(): String {
        return vDate
    }

    fun setvDate(vDate: String) {
        this.vDate = vDate
    }

    fun getvPrice(): String {
        return vPrice
    }

    fun setvPrice(vPrice: String) {
        this.vPrice = vPrice
    }

    fun getvImage(): Int {
        return vImage
    }

    fun setvImage(vImage: Int) {
        this.vImage = vImage
    }
}