package com.openfire.xmppchat

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.JobIntentService
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat.getSystemService
import com.openfire.xmppchat.utils.Utils
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smackx.iqregister.AccountManager
import org.jxmpp.jid.parts.Localpart


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

//        startForeground(12345678, notification)
        return START_STICKY
    }

    override fun onDestroy() {
        disconnect()
        Log.e("taskService", "Service Destroyed")
        super.onDestroy()
    }

    private fun disconnect() {
        val dcThread =
            @SuppressLint("StaticFieldLeak")
            object : AsyncTask<Void, Void, Unit>() {
                override fun doInBackground(vararg params: Void?): Unit? {
                    val presence = Presence(Presence.Type.unavailable)
                    Config.conn1?.sendStanza(presence)
                    return Config.conn1?.disconnect()
                }
            }
        if (Config.conn1 != null) {
            dcThread.execute()
        }
    }


    override fun onTaskRemoved(rootIntent: Intent) {
        disconnect()
        Log.e("taskService", "END")
        //Code here
    }
}

