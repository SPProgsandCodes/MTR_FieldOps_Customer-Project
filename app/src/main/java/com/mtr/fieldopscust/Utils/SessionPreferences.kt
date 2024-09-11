package com.mtr.fieldopscust.Utils

import android.content.Context
import android.content.SharedPreferences
import com.mtr.fieldopscust.R

class SessionPreferences : SharedPreferences {
    private var sharedPref: SharedPreferences
    private var context: Context

    constructor(context: Context, delegate: SharedPreferences) {
        this.sharedPref = delegate
        this.context = context
    }

    constructor(context: Context) {
        this.sharedPref = context.getSharedPreferences(
            StringUtil.getStringForID(R.string.app_name),
            Context.MODE_PRIVATE
        )
        this.context = context
    }

    override fun getAll(): Map<String, *> {
        return sharedPref.all
    }

    override fun edit(): Editor {
        return Editor()
    }

    override fun getBoolean(key: String, defValue: Boolean): Boolean {
        return sharedPref.getBoolean(key, defValue)
    }

    override fun getFloat(key: String, defValue: Float): Float {
        return sharedPref.getFloat(key, defValue)
    }

    override fun getInt(key: String, defValue: Int): Int {
        return sharedPref.getInt(key, defValue)
    }

    override fun getLong(key: String, defValue: Long): Long {
        return sharedPref.getLong(key, defValue)
    }

    override fun getString(key: String, defValue: String?): String? {
        return sharedPref.getString(key, defValue)
    }

    override fun getStringSet(key: String, defValues: Set<String>?): Set<String>? {
        return sharedPref.getStringSet(key, defValues)
    }

    override fun contains(s: String): Boolean {
        return sharedPref.contains(s)
    }

    override fun registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener: SharedPreferences.OnSharedPreferenceChangeListener) {
        sharedPref.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener)
    }

    override fun unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener: SharedPreferences.OnSharedPreferenceChangeListener) {
        sharedPref.unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener)
    }

    open inner class Editor : SharedPreferences.Editor {
        private var editor: SharedPreferences.Editor = sharedPref.edit()

        override fun putBoolean(key: String, value: Boolean): Editor {
            editor.putBoolean(key, value)
            return this
        }

        override fun putFloat(key: String, value: Float): Editor {
            editor.putFloat(key, value)
            return this
        }

        override fun putInt(key: String, value: Int): Editor {
            editor.putInt(key, value)
            return this
        }

        override fun putLong(key: String, value: Long): Editor {
            editor.putLong(key, value)
            return this
        }

        override fun putString(key: String, value: String?): Editor {
            editor.putString(key, value)
            return this
        }

        override fun putStringSet(key: String, values: Set<String>?): Editor {
            editor.putStringSet(key, values)
            return this
        }

        override fun apply() {
            editor.apply()
        }

        override fun clear(): Editor {
            editor.clear()
            return this
        }

        override fun commit(): Boolean {
            return editor.commit()
        }

        override fun remove(s: String): Editor {
            editor.remove(s)
            return this
        }
    }
}