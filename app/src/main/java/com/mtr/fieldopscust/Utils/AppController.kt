package com.mtr.fieldopscust.Utils

import android.content.Context
import android.support.multidex.MultiDex
import android.support.multidex.MultiDexApplication
import com.mtr.fieldopscust.DashboardScreen.FragmentHomePage

class AppController : MultiDexApplication() {
    private var sharedSessionPreferences: SessionPreferences? = null

    fun loginPrefs(pref: String?): SessionPreferences?{
        if (sharedSessionPreferences == null){
            sharedSessionPreferences = SessionPreferences(baseContext, baseContext.getSharedPreferences(pref, Context.MODE_PRIVATE))
        }
        return sharedSessionPreferences
    }

    fun setUserDataPrefs(pref: String?): SessionPreferences?{
        if (sharedSessionPreferences == null){
            sharedSessionPreferences = SessionPreferences(baseContext, baseContext.getSharedPreferences(pref, Context.MODE_PRIVATE))
        }
        return sharedSessionPreferences
    }

    override fun onCreate() {
        super.onCreate()

        ApplicationHelper.setAppController(this)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    fun getResourceId(resourceName: String?, defType: String?): Int {
        return getResourceId(resourceName, defType, this.packageName)
    }

    fun getResourceId(resourceName: String?, defType: String?, packageName: String?): Int {
        return if ((!resourceName?.let { ObjectUtil.isNumber(it) }!! && ObjectUtil.isNonEmptyStr(resourceName))) this.resources.getIdentifier(
            resourceName,
            defType,
            packageName
        ) else 0
    }




}