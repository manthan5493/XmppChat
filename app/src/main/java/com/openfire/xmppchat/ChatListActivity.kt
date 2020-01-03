package com.openfire.xmppchat

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.openfire.xmppchat.utils.Utils
import kotlinx.android.synthetic.main.activity_chat_list.*
import org.jivesoftware.smack.SmackException
import org.jivesoftware.smack.XMPPException
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smack.roster.RosterEntry
import org.jivesoftware.smack.roster.RosterListener
import org.jivesoftware.smack.roster.packet.RosterPacket
import org.jivesoftware.smackx.bookmarks.BookmarkManager
import org.jivesoftware.smackx.muc.MultiUserChatException
import org.jivesoftware.smackx.offline.OfflineMessageManager
import org.jivesoftware.smackx.xdata.FormField
import org.jxmpp.jid.Jid
import org.jxmpp.jid.impl.JidCreate
import org.jxmpp.jid.parts.Domainpart
import org.jxmpp.jid.parts.Localpart
import org.jxmpp.jid.parts.Resourcepart


class ChatListActivity : AppCompatActivity(), RosterAdapter.RoasterClickListener,
    GroupAdapter.GroupClickListener {

    var rosterLists: ArrayList<RosterEntry> = ArrayList()
    var groupList: ArrayList<GroupInfo> = ArrayList()

    lateinit var adapter: RosterAdapter
    lateinit var adapterGroup: GroupAdapter
    lateinit var bookmarkManager: BookmarkManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        ServerPingWithAlarmManager.onCreate(this@ChatListActivity)
//        ServerPingWithAlarmManager.getInstanceFor(Config.conn1!!).isEnabled = true;


        bookmarkManager = BookmarkManager.getBookmarkManager(Config.conn1)
        /* with(bookmarkManager.bookmarkedConferences) {
             forEach {
                 bookmarkManager.removeBookmarkedConference(it.jid)
             }
         }*/
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
        Config.multiUserChatManager!!.addInvitationListener { conn, room, inviter, reason, password, message, invitation ->
            //            Toast.makeText(this, "Invitation", Toast.LENGTH_SHORT).show()
            if (room.isJoined) {
                return@addInvitationListener
            }
            try {
                try {
                    val mucEnterConfiguration =
                        room.getEnterConfigurationBuilder(Resourcepart.from(Config.loginName))
                            .requestNoHistory()
                            .build()

                    room.join(mucEnterConfiguration)
                } catch (e: SmackException.NoResponseException) {
                    e.printStackTrace()
                } catch (e: SmackException.NotConnectedException) {
                    e.printStackTrace()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                } catch (e: MultiUserChatException.NotAMucServiceException) {
                    e.printStackTrace()
                }

                Log.e("MUC", "join room successfully");
                getBuddies()

            } catch (e: XMPPException) {
                e.printStackTrace();
                Log.e("MUC", "join room failed!")
            }
        }
        Config.conn1!!.setParsingExceptionCallback { stanzaData ->
            Log.e("MUC", "Parcing" + stanzaData.content)
            stanzaData.parsingException.printStackTrace()
        }
        setContentView(R.layout.activity_chat_list)
        val offlineMessageManager = OfflineMessageManager(Config.conn1)
        val map = offlineMessageManager.messages.groupBy { it.from }

        adapter = RosterAdapter(rosterLists, map)
        adapterGroup = GroupAdapter(groupList)
        imgAddPerson.setOnClickListener {
            /*
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
                        dialog.show()*/

            val builder = AlertDialog.Builder(this/*, R.style.fullScreenDialog*/)
            val dialogView = layoutInflater.inflate(R.layout.dialog_create_entity, null)
            builder.setView(dialogView)

            val txtTitle = dialogView.findViewById<TextView>(R.id.txtTitle)
            val etNew = dialogView.findViewById<EditText>(R.id.etNew)
            val btnAddNew = dialogView.findViewById<Button>(R.id.btnAddNew)
            txtTitle.text = "Start New Conversion"
            val dialog = builder.create()
            dialog.show()
            btnAddNew.setOnClickListener {
                dialog.dismiss()
                if (etNew.text.isNotEmpty())
                    openNewChat(etNew.text.toString())
            }
        }

        imgAddGroup.setOnClickListener {

          /*  val dialog = Dialog(this)
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
            btn.text = "Create Group"
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
                    createNewGroup(editText.text.toString())
            }
            dialog.setContentView(main)
            dialog.show()*/


            val builder = AlertDialog.Builder(this/*, R.style.fullScreenDialog*/)
            val dialogView = layoutInflater.inflate(R.layout.dialog_create_entity, null)
            builder.setView(dialogView)

            val txtTitle = dialogView.findViewById<TextView>(R.id.txtTitle)
            val etNew = dialogView.findViewById<EditText>(R.id.etNew)
            val btnAddNew = dialogView.findViewById<Button>(R.id.btnAddNew)
            txtTitle.text = "Create Group"
            val dialog = builder.create()
            dialog.show()
            btnAddNew.setOnClickListener {
                dialog.dismiss()
                if (etNew.text.isNotEmpty())
                    createNewGroup(etNew.text.toString())
            }
        }
        adapter.setRoasterListener(this)
        rvRoasterList.adapter = adapter
        adapterGroup.setGroupListener(this)
        rvRoasterGroup.adapter = adapterGroup
        getBuddies()
        val presence = Presence(Presence.Type.available)
        Config.conn1!!.sendStanza(presence)
        (application as ChatApp).startPresenceUpdate = true
    }

    private fun createNewGroup(groupName: String) {

        if (!Config.roster!!.isLoaded) {
            Config.roster?.reloadAndWait()
            Log.e("Roster :", "Reload and wait")
        }
        groupName.replace(" ", "_")
        groupName.plus(System.currentTimeMillis() / 1000)
        val mucJid = JidCreate.entityBareFrom(
            Localpart.from(groupName)
            , Domainpart.from(Config.openfire_host_server_CONFERENCE_SERVICE)
        )

        val multiUserChat = Config.multiUserChatManager!!.getMultiUserChat(mucJid)
        val mucEnterConfiguration =
            multiUserChat.getEnterConfigurationBuilder(Resourcepart.from(Config.loginName))
                .requestNoHistory()
                .build()

        multiUserChat.join(mucEnterConfiguration)

        bookmarkManager.addBookmarkedConference(
            groupName,
            mucJid,
            true,
            Resourcepart.from(Config.loginName),
            ""
        )

        multiUserChat.grantOwnership(Config.conn1!!.user)


        val form = multiUserChat.configurationForm
        val submitForm = form.createAnswerForm();
        for (field in form.getFields().iterator()) {
            if (FormField.Type.hidden != field.type && field.variable != null) {
                submitForm.setDefaultAnswer(field.getVariable());
            }
        }
        submitForm.setAnswer("muc#roomconfig_roomname", groupName)
        submitForm.setAnswer("muc#roomconfig_roomdesc", "$groupName created from Android")
        submitForm.setAnswer("muc#roomconfig_publicroom", true)
//        submitForm.setAnswer("muc#roomconfig_persistentroom", "1")
        submitForm.setAnswer("x-muc#roomconfig_canchangenick", true)
        submitForm.setAnswer("x-muc#roomconfig_registration", true)
        submitForm.getField("muc#roomconfig_persistentroom").addValue("1")
        submitForm.getField("muc#roomconfig_membersonly").addValue("1")


        multiUserChat.sendConfigurationForm(submitForm)

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

    override fun onGroupClick(entry: GroupInfo) {

        val intent = Intent(this, GroupChatActivity::class.java)
        intent.putExtra("group", entry.roomId.asUnescapedString())
        startActivity(intent)
/*
        val intent = Intent(this, GroupDetailActivity::class.java)
        intent.putExtra("group", entry.roomId.asUnescapedString())
        startActivity(intent)*/
    }

    @Throws(
        SmackException.NotLoggedInException::class,
        InterruptedException::class,
        SmackException.NotConnectedException::class
    )
    fun getBuddies() {
        groupList.clear()

        rosterLists.clear()
        if (!Config.roster!!.isLoaded) {
            Config.roster?.reloadAndWait()
            Log.e("Roster :", "Reload and wait")
        }

        val entries = Config.roster?.entries
        val rosterLists = arrayListOf<RosterEntry>()

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
        val listGroup = arrayListOf<GroupInfo>()
        for (entry in Config.multiUserChatManager!!.joinedRooms) {
            val room = Config.multiUserChatManager!!.getRoomInfo(entry)
            listGroup.add(GroupInfo(entry, room))

            val bookMarked = bookmarkManager.bookmarkedConferences.firstOrNull { it.jid == entry }
            if (bookMarked == null) {
                bookmarkManager.addBookmarkedConference(
                    room.name,
                    room.room,
                    true,
                    Resourcepart.from(Config.loginName),
                    ""
                )
            }

        }
        adapterGroup = GroupAdapter(listGroup)
        adapterGroup.setGroupListener(this)

        val offlineMessageManager = OfflineMessageManager(Config.conn1)
        val map = offlineMessageManager.messages.groupBy { it.from }

        adapter = RosterAdapter(rosterLists, map)
        adapter.setRoasterListener(this)

        runOnUiThread {
            rvRoasterGroup.adapter = adapterGroup
            rvRoasterList.adapter = adapter
            adapter.notifyDataSetChanged()
//            adapterGroup.notifyDataSetChanged()
        }

    }

    /* List Room in Directory - TRUE
     Make Room Moderated - FALSE
     Make Room Members-only - TRUE
     Allow Occupants to invite Others - TRUE
     Allow Occupants to change Subject - FALSE
     Only login with registered nickname - FALSE
     Allow Occupants to change nicknames - TRUE
     Allow Users to register with the room - TRUE
     Log Room Conversations - TRUE*/
}






