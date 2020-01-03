package com.openfire.xmppchat

import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context


class ServiceStarter : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if(intent.action.equals("android.permission.RECEIVE_BOOT_COMPLETED")) {
            val i = Intent(context, OnClearFromRecentService::class.java)
            context.startService(i)
        }
    }
}