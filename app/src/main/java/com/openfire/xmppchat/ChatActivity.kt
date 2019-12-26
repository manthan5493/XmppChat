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
import kotlinx.android.synthetic.main.activity_chat.*
import okhttp3.*
import org.jitsi.meet.sdk.JitsiMeet
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import org.jitsi.meet.sdk.JitsiMeetUserInfo
import org.jivesoftware.smack.SmackException
import org.jivesoftware.smack.XMPPException
import org.jivesoftware.smack.chat.ChatManagerListener
import org.jivesoftware.smack.chat2.Chat
import org.jivesoftware.smack.chat2.ChatManager
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smackx.httpfileupload.HttpFileUploadManager
import org.jivesoftware.smackx.iqlast.LastActivityManager
import org.jivesoftware.smackx.offline.OfflineMessageManager
import org.jivesoftware.smackx.offline.packet.OfflineMessageInfo
import org.jxmpp.jid.impl.JidCreate
import java.io.File
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL


class ChatActivity : AppCompatActivity() {
    lateinit var chatManager: ChatManager
    lateinit var currentChat: Chat
    lateinit var sendTo: String
    lateinit var adapter: ChatAdapter
    var messages = ArrayList<Message>()
    var offlineMessage = listOf<Message>()
    val listener =
        ChatManagerListener { chat, createdLocally ->

            chat.addMessageListener { chat, message ->
                if (message != null) {
                    Log.e("Message Received : ", message.body)
                    Log.e("Message ID : ", message.stanzaId)
                    Log.e("Message TO : ", message.to.asUnescapedString())
                    if (message.from.split("/")[0] == chat.participant.split("/")[0]) {
                        runOnUiThread {
                            Toast.makeText(this, "MSG::" + message.body, Toast.LENGTH_SHORT)
                                .show()
                            messages.add(message)
                            rvMessage.scrollToPosition(messages.size - 1)
                            adapter.notifyItemInserted(messages.size)
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this, "NEW::" + message.body, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
                Log.w("app", chat.toString())
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        Log.e("CURRENT ", "USER" + Config.conn1!!.user)
        getBundleData()

        chatManager = ChatManager.getInstanceFor(Config.conn1)
        adapter = ChatAdapter(messages, Config.conn1!!.user.asEntityBareJid())
        rvMessage.adapter = adapter
        currentChat =
            chatManager.chatWith(JidCreate.entityBareFrom(sendTo) /*+ "/" + Config.openfire_host_server_RESOURCE*/)


        initMeetingConfig()
        setAction()
        setMsgListener()
        val lManager = LastActivityManager.getInstanceFor(Config.conn1)
        if (lManager.isLastActivitySupported(JidCreate.entityBareFrom(sendTo))) {
            val lastActivity = lManager.getLastActivity(JidCreate.entityBareFrom(sendTo))

            if (lastActivity.lastActivity > 0)
                Toast.makeText(
                    this,
                    getAgo(lastActivity.lastActivity),
                    Toast.LENGTH_SHORT
                ).show()
        }
    }

    private fun getAgo(second: Long): String {

        val convTime: String
        val suffix = "ago"
        when {
            second < 60 -> {
                convTime = "$second Seconds $suffix"
            }
            second < 60 * 60 -> {
                convTime = "" + second / 60 + " Minutes " + suffix
            }
            second < 60 * 60 * 24 -> {
                convTime = "" + second / (60 * 60) + " Hours " + suffix;
            } /*else if (day >= 7) {
                if (day > 360) {
                    convTime = (day / 30) + " Years " + suffix;
                } else if (day > 30) {
                    convTime = (day / 360) + " Months " + suffix;
                } else {
                    convTime = (day / 7) + " Week " + suffix;
                }
            }*/
            second < 60 * 60 * 24 * 7 -> {
                convTime = "" + second / (60 * 60 * 24) + " Days " + suffix;
            }
            else -> {
                convTime = "long time ago"
            }
        }
        return convTime
    }

    private fun getBundleData() {

        sendTo = intent.getStringExtra("user")!!

        /* val listType = object : TypeToken<List<Message>>() {}.type;
         if (intent.hasExtra("offline")) {
             offlineMessage = Gson().fromJson(intent.getStringExtra("offline"), listType)
         }*/
        val offlineMessageManager = OfflineMessageManager(Config.conn1)
        offlineMessage = offlineMessageManager.messages.filter {
            it.from.contains(sendTo)
        }
        val nodeToBeDelete = arrayListOf<String>()

        for (offline in offlineMessage) {
            nodeToBeDelete.add(
                offline.getExtension<OfflineMessageInfo>(
                    "offline",
                    "http://jabber.org/protocol/offline"
                ).node
            )
        }
        offlineMessageManager.deleteMessages(nodeToBeDelete)
        messages.addAll(offlineMessage)
    }

    private fun setMsgListener() {
        if (::chatManager.isInitialized) {
            chatManager.addIncomingListener { from, message, chat ->
                if (message != null) {
                    Log.e("Message Received : ", message.body)
//                    Log.e("Message ID : ", message.stanzaId)
                    Log.e("Message TO : ", message.to.asUnescapedString())
                    if (message.from.asBareJid()/*.split("/")[0]*/ == chat.xmppAddressOfChatPartner/*.split("/")[0]*/) {
                        runOnUiThread {
                            Toast.makeText(this, "MSG::" + message.body, Toast.LENGTH_SHORT)
                                .show()
                            messages.add(message)
                            rvMessage.scrollToPosition(messages.size - 1)
                            adapter.notifyItemInserted(messages.size)
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this, "NEW::" + message.body, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
                Log.w("app", chat.toString())
            }
//            chatManager.addChatListener(listener)
        } else {
            Log.e("Else Part ", "Connection Null")
        }
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
            .setWelcomePageEnabled(false)
            .build()
        JitsiMeet.setDefaultConferenceOptions(defaultOptions)
    }

    private fun setAction() {
        ivSend.setOnClickListener {
            if (etMsg.text.isNullOrBlank()) {
                return@setOnClickListener
            }
            val msg = Message()
            msg.type = Message.Type.chat;
            msg.body = etMsg.text.toString()
            msg.from = Config.conn1?.user
            msg.subject=ChatType.CHAT.type
            msg.to = currentChat.xmppAddressOfChatPartner
//            val extTypeOfChat = DefaultExtensionElement(
//                "typeofchat", "urn:xmpp:exttypeofchat"
//            )
//            val extTypeOfChat = StandardExtensionElement.builder(
//                "typeofchat", "urn:xmpp:exttypeofchat"
//            ).setText(ChatType.CHAT.type).build()

//            extTypeOfChat.setValue("typeofchat", ChatType.CHAT.type)
//            msg.addExtension(extTypeOfChat)
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


        btnCall.setOnClickListener {
            val opponent = sendTo.substring(0, sendTo.indexOf("@"))
            val text = Config.loginName.plus("_${opponent}")
            Log.v("@@@ROOM ID:::", text)

            // Build options object for joining the conference. The SDK will merge the default
            // one we set earlier and this one when joining.
            // Build options object for joining the conference. The SDK will merge the default
            // one we set earlier and this one when joining.

            val userInfo = JitsiMeetUserInfo()
            userInfo.displayName = "Akash Moradiya"
            userInfo.avatar = URL("https://static2.clutch.co/s3fs-public/logos/brainvire_png_logo_-_copy.png")
            val options = JitsiMeetConferenceOptions.Builder()
                .setRoom(text)
                .setUserInfo(userInfo)
                .setFeatureFlag("pip.enabled",false)
                .setFeatureFlag("chat.enabled", false)
                .setWelcomePageEnabled(false)
                .build()

            // Launch the new activity with the given options. The launch() method takes care
            // of creating the required Intent and passing the options.
            // Launch the new activity with the given options. The launch() method takes care
            // of creating the required Intent and passing the options.
            JitsiMeetActivity.launch(this, options)
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
                    Toast.makeText(this@ChatActivity, "onFailure", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: Call, response: Response) {
                    val msg = Message()

                    msg.type = Message.Type.chat
                    msg.body = slot.putUrl.toURI().toASCIIString()
                    msg.from = Config.conn1?.user
                    msg.to = currentChat.xmppAddressOfChatPartner
                    msg.subject=ChatType.IMAGE.type
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
                return currentChat.send(msgBody)
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
