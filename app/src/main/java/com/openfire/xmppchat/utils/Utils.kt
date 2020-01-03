package com.openfire.xmppchat.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.openfire.xmppchat.R


object Utils {
    var dialog: AlertDialog? = null

    //Dialog
    fun showProgress(context: Context) {
        if (dialog != null ) {
            hideProgress()
        }
        val builder = AlertDialog.Builder(context/*, R.style.fullScreenDialog*/)
        builder.setView(R.layout.dialog_progress)
        builder.setCancelable(false)
        dialog = builder.create()
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.show()
    }

    fun hideProgress() {
        if (dialog != null ) {
            dialog!!.dismiss()
            dialog = null
        }
    }
}