package com.openfire.xmppchat

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.features.ReturnMode
import com.esafirm.imagepicker.model.Image
import kotlinx.android.synthetic.main.activity_group_chat.*
import okhttp3.*
import org.jivesoftware.smack.SmackException
import org.jivesoftware.smack.XMPPException
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smackx.httpfileupload.HttpFileUploadManager
import org.jivesoftware.smackx.muc.MultiUserChat
import org.jxmpp.jid.impl.JidCreate
import java.io.File
import java.io.IOException
import org.jivesoftware.smackx.muc.DiscussionHistory
import com.dropbox.core.v2.teamlog.ActorLogInfo.app
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import org.jivesoftware.smackx.mam.MamManager
import org.jivesoftware.smackx.muc.packet.MUCInitialPresence
import org.jxmpp.jid.parts.Resourcepart


class GroupChatActivity : AppCompatActivity() {

    lateinit var adapter: ChatAdapter
    var messages = ArrayList<Message>()
    var groupId = ""
    lateinit var mucChat: MultiUserChat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_chat)
        groupId = intent.getStringExtra("group")
        mucChat =
            Config.multiUserChatManager!!.getMultiUserChat(JidCreate.entityBareFrom(groupId))


        mucChat.addMessageListener { message ->
            if (message != null) {
                Log.e("Message Received : ", message.body)
                Log.e("Message ID : ", message.stanzaId)
                Log.e("Message TO : ", message.to.asUnescapedString())
                if (message.from.resourceOrEmpty != mucChat.nickname) {
                    runOnUiThread {
                        Toast.makeText(this, "MSG::" + message.body, Toast.LENGTH_SHORT)
                            .show()
                        messages.add(message)
                        rvMessage.scrollToPosition(messages.size - 1)
                        adapter.notifyItemInserted(messages.size)
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this, "Self::" + message.body, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }

        adapter = ChatAdapter(messages, Config.conn1!!.user.asEntityBareJid(), true)
        rvMessage.adapter = adapter
        setAction()

        /*val mamManager = MamManager.getInstanceFor(mucChat)
        //enable it for fetching messages
        mamManager.enableMamForAllMessages()*/
//        mamManager.queryMostRecentPage(mucChat.room,20)
    }

    private fun setAction() {
        btnDetail.setOnClickListener {
            val intent = Intent(this, GroupDetailActivity::class.java)
            intent.putExtra("group", mucChat.room.asUnescapedString())
            startActivity(intent)
        }
        ivSend.setOnClickListener {
            if (etMsg.text.isNullOrBlank()) {
                return@setOnClickListener
            }
            val msg = Message()
            msg.type = Message.Type.groupchat
            msg.body = etMsg.text.toString()
//            msg.from = Config.conn1?.user
//            msg.to = mucChat.room
            msg.subject = ChatType.CHAT.type

            sendMessage(msg)
        }
        etMsg.addTextChangedListener {
            ivSend.isEnabled == !it.isNullOrBlank()
        }

        ivAttachment.setOnClickListener {
            ImagePicker.create(this)
                .returnMode(ReturnMode.ALL) // set whether pick and / or camera action should return immediate result or not.
                .folderMode(true) // folder mode (false by default)
                .toolbarFolderTitle("Select Image") // folder selection title
                .toolbarImageTitle("Tap to select") // image selection title
                .toolbarArrowColor(Color.BLACK) // Toolbar 'up' arrow color
                .includeVideo(false) // Show video on image picker
                .single() // single mode
                .showCamera(true) // show camera or not (true by default)
                .imageDirectory("Camera") // directory name for captured image  ("Camera" folder by default)
                .start()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            val image = ImagePicker.getFirstImageOrNull(data)
            if (image != null)
                sendImage(image)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun sendImage(image: Image) {
        val file = File(image.path)
        val manager = HttpFileUploadManager.getInstanceFor(Config.conn1)

        try {
            val slot = manager.requestSlot(file.name, file.length())
            val client = UnsafeOkHttpClient.getUnsafeOkHttpClient()
            val request = Request.Builder()
                .url(slot.putUrl)
                .put(RequestBody.create(MediaType.parse("application/octet-stream"), file))
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this@GroupChatActivity, "onFailure", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: Call, response: Response) {
                    val msg = Message()

                    msg.type = Message.Type.groupchat
                    msg.body = slot.putUrl.toURI().toASCIIString()
//                    msg.from = Config.conn1?.user
//                    msg.to = mucChat.room
                    msg.subject = ChatType.IMAGE.type
//                    val extTypeOfChat = StandardExtensionElement.builder(
//                        "typeofchat", "urn:xmpp:exttypeofchat"
//                    ).setText(ChatType.IMAGE.type).build()
//                    msg.addExtension(extTypeOfChat)
                    sendMessage(msg)
                }
            })

        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: XMPPException.XMPPErrorException) {
            e.printStackTrace()
        } catch (e: SmackException) {
            e.printStackTrace()
        }

    }


    private fun sendMessage(msgBody: Message) {
        val chatThread = @SuppressLint("StaticFieldLeak")
        object : AsyncTask<Void, Void, Unit>() {
            override fun doInBackground(vararg params: Void?): Unit? {
                return mucChat.sendMessage(msgBody)
            }

            override fun onPostExecute(result: Unit?) {
                super.onPostExecute(result)
                messages.add(msgBody)
                adapter.notifyItemInserted(messages.size)
                rvMessage.scrollToPosition(messages.size - 1)
                etMsg.setText("")
            }
        }
        chatThread.execute()

    }
}
