package com.mtr.fieldopscust.Utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.widget.ProgressBar
import com.mtr.fieldopscust.R

class GlobalProgressBar {

    companion object {
        var dialog: Dialog? = null

        fun show(context: Context) {
            // Dismiss existing dialog if any
            dismiss()

            // Inflate the progress bar layout
            val view = LayoutInflater.from(context).inflate(R.layout.progress_dialog, null)

            // Create and configure the dialog
            dialog = Dialog(context).apply {
                setContentView(view)
                setCancelable(false) // Prevent dismissing by tapping outside
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }

            dialog?.show()
        }

        fun dismiss() {
            dialog?.dismiss()
            dialog = null
        }
    }
}