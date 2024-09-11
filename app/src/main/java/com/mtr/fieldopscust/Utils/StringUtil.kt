package com.mtr.fieldopscust.Utils

object StringUtil {

    fun getStringForID(id: Int): String {
        return ApplicationHelper.getAppController()!!.resources.getString(id)

    }
}