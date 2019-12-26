package com.openfire.xmppchat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_group_detail.*
import org.jivesoftware.smackx.muc.Affiliate
import org.jivesoftware.smackx.muc.MultiUserChat
import org.jivesoftware.smackx.muc.packet.MUCAdmin
import org.jxmpp.jid.impl.JidCreate

class GroupDetailActivity : AppCompatActivity() {

    var groupId = ""
    lateinit var mucChat: MultiUserChat
    lateinit var adapter: GroupUserAdapter
    val groupMembers = arrayListOf<Affiliate>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_detail)
        groupId = intent.getStringExtra("group")
        mucChat =
            Config.multiUserChatManager!!.getMultiUserChat(JidCreate.entityBareFrom(groupId))
        groupMembers.addAll(mucChat.owners)
        groupMembers.addAll(mucChat.members)

        adapter = GroupUserAdapter(groupMembers)
        rvGroupParticipants.adapter = adapter

    }
}
