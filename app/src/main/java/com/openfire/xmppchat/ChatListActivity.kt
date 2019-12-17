package com.openfire.xmppchat

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_chat_list.*
import org.jivesoftware.smack.SmackException
import org.jivesoftware.smack.StanzaListener
import org.jivesoftware.smack.filter.StanzaFilter
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smack.packet.Stanza
import org.jivesoftware.smack.roster.Roster
import org.jivesoftware.smack.roster.RosterEntry
import org.jivesoftware.smack.roster.RosterListener
import org.jivesoftware.smack.roster.packet.RosterPacket
import org.jivesoftware.smackx.muc.MultiUserChatManager
import org.jivesoftware.smackx.offline.OfflineMessageManager
import org.jxmpp.jid.Jid
import org.jxmpp.jid.impl.JidCreate
import org.jxmpp.jid.parts.Domainpart
import org.jxmpp.jid.parts.Localpart
import org.jxmpp.jid.parts.Resourcepart


class ChatListActivity : AppCompatActivity(), RosterAdapter.RoasterClickListener {

    var rosterLists: ArrayList<RosterEntry> = ArrayList()
    lateinit var adapter: RosterAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Config.roster = Roster.getInstanceFor(Config.conn1)
        Config.roster!!.subscriptionMode = Roster.SubscriptionMode.accept_all
        Config.conn1!!.addStanzaInterceptor(object : StanzaListener {
            override fun processStanza(packet: Stanza?) {
                if (packet is Presence) {
                    Toast.makeText(
                        this@ChatListActivity,
                        "PRESENCES" + packet.from,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }, object : StanzaFilter {
            override fun accept(stanza: Stanza?): Boolean {
                return stanza is Presence
            }
        })

        Config.roster!!.addRosterListener(object : RosterListener {
            override fun entriesDeleted(addresses: MutableCollection<Jid>?) {
                getBuddies()
            }

            override fun presenceChanged(presence: Presence?) {
                getBuddies()
            }

            override fun entriesUpdated(addresses: MutableCollection<Jid>?) {
                getBuddies()
            }

            override fun entriesAdded(addresses: MutableCollection<Jid>?) {
                getBuddies()
            }

        })
        setContentView(R.layout.activity_chat_list)
        val offlineMessageManager = OfflineMessageManager(Config.conn1)
        val map = offlineMessageManager.messages.groupBy { it.from }
        val presence = Presence(Presence.Type.available)
        Config.conn1!!.sendStanza(presence)
        adapter = RosterAdapter(rosterLists, map)
        imgAddPerson.setOnClickListener {
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            val main = LinearLayout(this)
            main.orientation = LinearLayout.VERTICAL
            val editText = EditText(this)
            val editParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            editParams.topMargin = 40
            editText.layoutParams = editParams
            val btn = Button(this)
            btn.text = "Chat"
            val btnParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            btnParams.topMargin = 40
            btnParams.bottomMargin = 40

            main.addView(editText)
            main.addView(btn)
            btn.setOnClickListener {
                dialog.dismiss()
                if (editText.text.isNotEmpty())
                    openNewChat(editText.text.toString())
            }
            dialog.setContentView(main)
            dialog.show()
        }

        imgAddGroup.setOnClickListener {
            if (!Config.roster!!.isLoaded) {
                Config.roster?.reloadAndWait()
                Log.e("Roster :", "Reload and wait")
            }

            val mucJid = JidCreate.entityBareFrom(
                Localpart.from("TEST_MUC")
                , Domainpart.from(Config.openfire_host_server_CONFERENCE_SERVICE)
            )
            val multiUserChatManager = MultiUserChatManager.getInstanceFor(Config.conn1)

            val multiUserChat = multiUserChatManager.getMultiUserChat(mucJid)
            val mucEnterConfiguration =
                multiUserChat?.getEnterConfigurationBuilder(Resourcepart.from(Config.loginName))!!
                    .requestNoHistory()
                    .build()

            if (!multiUserChat.isJoined) {
                multiUserChat.join(mucEnterConfiguration)
            }

        }
        adapter.setRoasterListener(this)
        rvRoasterList.adapter = adapter

    }


    override fun onResume() {
        super.onResume()
        getBuddies()
    }

    private fun openNewChat(newUser: String) {


        Config.roster?.createEntry(
            JidCreate.bareFrom(newUser + "@" + Config.openfire_host_server_SERVICE),
            newUser + "@" + Config.openfire_host_server_SERVICE,
            null
        )


        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("user", newUser + "@" + Config.openfire_host_server_SERVICE)
        startActivity(intent)
    }

    override fun onClick(
        entry: RosterEntry
    ) {

        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("user", entry.jid.asUnescapedString())
        startActivity(intent)
    }

    @Throws(
        SmackException.NotLoggedInException::class,
        InterruptedException::class,
        SmackException.NotConnectedException::class
    )
    fun getBuddies(): List<RosterEntry> {
        rosterLists.clear()
        if (!Config.roster!!.isLoaded) {
            Config.roster?.reloadAndWait()
            Log.e("Roster :", "Reload and wait")
        }
        val entries = Config.roster?.entries
        Log.e("Size of Roster :", "" + entries?.size)
        if (entries != null) {
            for (entry in entries) {
                rosterLists.add(entry)
                if (entry.type == RosterPacket.ItemType.from) {
                    Config.roster?.createEntry(
                        entry.jid,
                        entry.jid.asUnescapedString(),
                        null
                    )
                }
            }
        }
        runOnUiThread {
            adapter.notifyDataSetChanged()
        }


//        val presence = roster.getAllPresences(Config.loginName + "@" + Config.openfire_host_server_SERVICE)
//
//        adapter.setPresence(presence)
        return rosterLists
    }
}






