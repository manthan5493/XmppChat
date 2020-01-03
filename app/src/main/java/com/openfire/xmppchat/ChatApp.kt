package com.openfire.xmppchat

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent


class ChatApp : Application(), LifecycleObserver {

    var startPresenceUpdate = false

    override fun onCreate() {
        super.onCreate()
//        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    /*  @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
     fun onAppBackgrounded() {
         if (startPresenceUpdate) {
             val presence = Presence(Presence.Type.unavailable)
             Config.conn1!!.sendStanza(presence)
         }
     }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
     fun onAppForegrounded() {
         if (startPresenceUpdate) {
             val presence = Presence(Presence.Type.available)
             Config.conn1!!.sendStanza(presence)
             val bookmarkManager = BookmarkManager.getBookmarkManager(Config.conn1)

             for (conference in bookmarkManager.bookmarkedConferences) {
                 val mucChat = Config.multiUserChatManager!!.getMultiUserChat(conference.jid)

                 *//*    val presence =
                        mucChat.getOccupantPresence(JidCreate.entityFullFrom(mucChat.room))
                    presence.mode = Presence.Mode.available
                    Config.conn1!!.sendStanza(presence)*//*

                val mucEnterConfiguration =
                    mucChat.getEnterConfigurationBuilder(Resourcepart.from(Config.loginName))
                        .requestNoHistory()
                        .build()

                mucChat.join(mucEnterConfiguration)
            }
        }
    }

*/
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        Toast.makeText(applicationContext, "OnDestroy", Toast.LENGTH_SHORT).show()
    }
}