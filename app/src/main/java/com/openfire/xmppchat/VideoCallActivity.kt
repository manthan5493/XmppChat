package com.openfire.xmppchat

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_video_call.*
import org.jitsi.meet.sdk.*
import java.net.MalformedURLException
import java.net.URL


class VideoCallActivity : JitsiMeetActivity() {


    lateinit var jitsiMeetView: JitsiMeetView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_call)
        initMeetingConfig()

        jitsiMeetView = JitsiMeetView(this@VideoCallActivity)
        jitsiMeetView.listener = object : JitsiMeetViewListener {
            override fun onConferenceTerminated(p0: MutableMap<String, Any>?) {
                jitsiMeetView.leave()
                finish()
            }

            override fun onConferenceJoined(p0: MutableMap<String, Any>?) {
            }

            override fun onConferenceWillJoin(p0: MutableMap<String, Any>?) {
            }
        }

        val options = JitsiMeetConferenceOptions.Builder()
            .setRoom(intent.getStringExtra("room_name"))
            .setAudioMuted(false)
            .setVideoMuted(false)
            .setAudioOnly(false)
            .setWelcomePageEnabled(false)
            .build()
        jitsiMeetView.join(options)

        frame.addView(jitsiMeetView)

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
            /* .setAudioMuted(false)
             .setVideoMuted(false)*/
            .setWelcomePageEnabled(false)
            .build()
        JitsiMeet.setDefaultConferenceOptions(defaultOptions)

    }
}
