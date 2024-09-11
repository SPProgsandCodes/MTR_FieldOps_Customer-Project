package com.mtr.fieldopscust.Utils

import android.view.View
import android.widget.TextView


object ObjectUtil {
        fun isEmpty(obj: Any?): Boolean {
            if (obj == null) {
                return true
            }
            if (obj is String) {
                return isEmptyStr(obj as String?)
            }
            if (obj is CharSequence) {
                return obj.length == 0
            }
//            if (obj is Map<, >) {
//                return obj.size == 0
//            }
            if (obj is Collection<*>) {
                return obj.isEmpty()
            }
            if (obj.javaClass.isArray) {
                return (obj as Array<Any?>).size == 0
            }
            return false
        }

        fun isEmptyStr(str: String?): Boolean {
            return str == null || str.trim { it <= ' ' }.length == 0 || str.equals(
                "null",
                ignoreCase = true
            )
        }

        fun isNonEmptyStr(str: String?): Boolean {
            return !isEmptyStr(str)
        }

        fun isNumber(v: Any): Boolean {
            if (isEmpty(v)) {
                return false
            }
            val cs = v as CharSequence
            val length = cs.length
            for (i in 0 until length) {
                if (!Character.isDigit(cs[i])) {
                    return false
                }
            }
            return true
        }

    fun getTextFromView(view: View?): String {
        return if ((view != null && view is TextView)) view.text.toString()
            .trim { it <= ' ' } else ""
    }

    }