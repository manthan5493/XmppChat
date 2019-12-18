package com.openfire.xmppchat

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService


class OnClearFromRecentService : Service() {
    private var mNotificationManager: NotificationManager? = null
    val notification: Notification
        get() {
            val builder = NotificationCompat.Builder(this, "Chat")
                .setContentText(getString(R.string.app_name))
                .setContentTitle("Fetching")
                .setOngoing(true)
                .setPriority(NotificationManagerCompat.IMPORTANCE_HIGH)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setWhen(System.currentTimeMillis())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder.setChannelId("Chat")
            }

            return builder.build()
        }
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.e("taskService", "Service Started")

        val notificationIntent = Intent(this, MainActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            notificationIntent, 0
        )
        mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager?

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.app_name)
            val mChannel =
                NotificationChannel("Chat", name, NotificationManager.IMPORTANCE_DEFAULT)
            mNotificationManager!!.createNotificationChannel(mChannel)
        }

        startForeground(12345678, notification)
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("taskService", "Service Destroyed")
    }


    override fun onTaskRemoved(rootIntent: Intent) {
        Log.e("taskService", "END")
        //Code here
    }
}

