package com.openfire.xmppchat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_group_detail.*
import org.jivesoftware.smackx.muc.MultiUserChat
import org.jivesoftware.smackx.muc.packet.MUCAdmin
import org.jxmpp.jid.impl.JidCreate

class GroupDetailActivity : AppCompatActivity() {

    var groupId = ""
    lateinit var room: MultiUserChat
    lateinit var adapter: GroupUserAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_detail)
        groupId = intent.getStringExtra("group")
        room =
            Config.multiUserChatManager!!.getMultiUserChat(JidCreate.entityBareFrom(groupId))
        adapter = GroupUserAdapter(room.participants)
        rvGroupParticipants.adapter = adapter

    }
}
