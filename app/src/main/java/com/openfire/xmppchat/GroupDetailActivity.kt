package com.openfire.xmppchat

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_group_detail.*
import org.jivesoftware.smack.packet.Presence
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
        setAction()
    }

    /*override fun onResume() {
        super.onResume()
        val presence =
            mucChat.getOccupantPresence(JidCreate.entityFullFrom(mucChat.room))
        presence.mode = Presence.Mode.available
        Config.conn1!!.sendStanza(presence)
    }*/

    private fun setAction() {
        btnAddNew.setOnClickListener {

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
            btn.text = "Add New"
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
                    inviteNew(editText.text.toString())
            }
            dialog.setContentView(main)
            dialog.show()
        }
    }

    private fun inviteNew(username: String) {
        val userJID =
            JidCreate.entityBareFrom(username + "@" + Config.openfire_host_server_SERVICE_DEV)
        try {
//                multiUserChat.changeAvailabilityStatus()
            mucChat.invite(userJID, "Welcome")
//                multiUserChat.outcasts.
            mucChat.grantMembership(userJID)
//                multiUserChat.grantModerator(Resourcepart.from(userJID.localpart.asUnescapedString()))

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
