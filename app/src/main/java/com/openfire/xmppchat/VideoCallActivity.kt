package com.openfire.xmppchat

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.android.synthetic.main.activity_video_call.*
import org.jitsi.meet.sdk.*
import org.jivesoftware.smack.chat2.ChatManager
import java.net.MalformedURLException
import java.net.URL


class VideoCallActivity : JitsiMeetActivity() {

    /*    var incomingChatListener: IncomingChatMessageListener =
            IncomingChatMessageListener { from, message, chat ->
                if (message != null) {
                    if (message.subject == ChatType.CALL_DISCONNECT.type) {
                        jitsiMeetView.leave()
                        finish()
                    }
                }
            }*/
    lateinit var chatManager: ChatManager
    lateinit var jitsiMeetView: JitsiMeetView

    var receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (intent != null) {
                val callDcRoom = intent.getStringExtra("disconnected_room")
                jitsiMeetView.leave()
                jitsiMeetView.dispose()
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_call)
        val lbm = LocalBroadcastManager.getInstance(this)
        lbm.registerReceiver(receiver, IntentFilter("call_dc_receiver"))

        initMeetingConfig()
        chatManager = ChatManager.getInstanceFor(Config.conn1)

//        chatManager.addIncomingListener(incomingChatListener)
        jitsiMeetView = JitsiMeetView(this@VideoCallActivity)
        jitsiMeetView.listener = object : JitsiMeetViewListener {
            override fun onConferenceTerminated(p0: MutableMap<String, Any>?) {
                if (isDestroyed || isFinishing) {
                    return
                }

                jitsiMeetView.leave()
                jitsiMeetView.dispose()
                Toast.makeText(
                    this@VideoCallActivity,
                    "onConferenceTerminated",
                    Toast.LENGTH_SHORT
                ).show()
//                if(callingActivity!=null){
                setResult(Activity.RESULT_OK, intent)
//                }
                finish()
            }

            override fun onConferenceJoined(p0: MutableMap<String, Any>?) {
                Toast.makeText(this@VideoCallActivity, "onConferenceJoined", Toast.LENGTH_SHORT)
                    .show()

            }

            override fun onConferenceWillJoin(p0: MutableMap<String, Any>?) {
                Toast.makeText(this@VideoCallActivity, "onConferenceWillJoin", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        val userInfo = JitsiMeetUserInfo()
        userInfo.displayName = Config.loginName
        val options = JitsiMeetConferenceOptions.Builder()
            .setRoom(intent.getStringExtra("room_name"))
            .setUserInfo(userInfo)
            .setAudioMuted(false)
            .setVideoMuted(false)
            .setAudioOnly(false)
            .setFeatureFlag("enableUserRolesBasedOnToken", false)
            .setWelcomePageEnabled(false)
            .build()
        jitsiMeetView.join(options)

        frame.addView(jitsiMeetView)

    }

    override fun onPause() {
        super.onPause()
        JitsiMeetActivityDelegate.onHostPause(this)
    }

    override fun onResume() {
        super.onResume()
        JitsiMeetActivityDelegate.onHostResume(this)

    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            val lbm = LocalBroadcastManager.getInstance(this)
            lbm.unregisterReceiver(receiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        JitsiMeetActivityDelegate.onHostDestroy(this)
//        chatManager.removeIncomingListener(incomingChatListener)
    }

    override fun onConferenceTerminated(data: MutableMap<String, Any>?) {
        super.onConferenceTerminated(data)
        Toast.makeText(this@VideoCallActivity, "onConferenceTerminated Main", Toast.LENGTH_SHORT)
            .show()
    }

    private fun initMeetingConfig() {
        // Initialize default options for Jitsi Meet conferences.
        // Initialize default options for Jitsi Meet conferences.
        val serverURL = try {
            URL("https://openfire.brainvire.dev:7443/ofmeet/")
            /*serverURL = new URL("https://meet.jit.si/");*/
        } catch (e: MalformedURLException) {
            e.printStackTrace()
            throw RuntimeException("Invalid server URL!")
        }

        val defaultOptions = JitsiMeetConferenceOptions.Builder()
            .setServerURL(serverURL)
//            .setToken("abcabc")
//            .setFeatureFlag("userRolesBasedOnToken.enabled", false)
            /* .setAudioMuted(false)
             .setVideoMuted(false)*/
            .setFeatureFlag("chat.enabled", false).setFeatureFlag("pip.enabled", false)
            .setWelcomePageEnabled(false)
            .build()
        JitsiMeet.setDefaultConferenceOptions(defaultOptions)

    }
}
