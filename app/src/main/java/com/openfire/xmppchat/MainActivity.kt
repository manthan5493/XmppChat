package com.openfire.xmppchat

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import org.jivesoftware.smack.*
import org.jivesoftware.smack.filter.PresenceTypeFilter
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smack.provider.ProviderManager
import org.jivesoftware.smack.proxy.ProxyInfo
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import org.jivesoftware.smackx.address.provider.MultipleAddressesProvider
import org.jivesoftware.smackx.chatstates.packet.ChatStateExtension
import org.jivesoftware.smackx.commands.provider.AdHocCommandDataProvider
import org.jivesoftware.smackx.disco.provider.DiscoverInfoProvider
import org.jivesoftware.smackx.disco.provider.DiscoverItemsProvider
import org.jivesoftware.smackx.iqlast.packet.LastActivity
import org.jivesoftware.smackx.iqprivate.PrivateDataManager
import org.jivesoftware.smackx.iqregister.AccountManager
import org.jivesoftware.smackx.muc.packet.GroupChatInvitation
import org.jivesoftware.smackx.muc.provider.MUCAdminProvider
import org.jivesoftware.smackx.muc.provider.MUCOwnerProvider
import org.jivesoftware.smackx.muc.provider.MUCUserProvider
import org.jivesoftware.smackx.offline.packet.OfflineMessageInfo
import org.jivesoftware.smackx.offline.packet.OfflineMessageRequest
import org.jivesoftware.smackx.ping.PingManager
import org.jivesoftware.smackx.ping.provider.PingProvider
import org.jivesoftware.smackx.privacy.provider.PrivacyProvider
import org.jivesoftware.smackx.pubsub.provider.EventProvider
import org.jivesoftware.smackx.receipts.DeliveryReceiptRequest
import org.jivesoftware.smackx.search.UserSearch
import org.jivesoftware.smackx.search.UserSearchManager
import org.jivesoftware.smackx.sharedgroups.packet.SharedGroupsInfo
import org.jivesoftware.smackx.time.provider.TimeProvider
import org.jivesoftware.smackx.vcardtemp.provider.VCardProvider
import org.jivesoftware.smackx.xdata.provider.DataFormProvider
import org.jxmpp.jid.parts.Localpart
import org.minidns.dnsname.DnsName
import java.net.InetAddress
import javax.net.SocketFactory


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initialiseConnection()
        btnLogin.setOnClickListener {
            if (etUser.text.toString().isNotEmpty() && etPassword.text.toString().isNotEmpty())
                login(etUser.text.toString(), etPassword.text.toString())
        }
        btnRegister.setOnClickListener {
            if (etUser.text.toString().isNotEmpty() && etPassword.text.toString().isNotEmpty())
                register(etUser.text.toString(), etPassword.text.toString())
        }
//        startService(Intent(this, OnClearFromRecentService::class.java))
    }

    private fun register(userName: String, password: String) {
        val registerTHread = @SuppressLint("StaticFieldLeak")
        object : AsyncTask<Void, Void, String?>() {
            override fun doInBackground(vararg params: Void?): String? {
                try {
                    val accountManager = AccountManager.getInstance(Config.conn1)
                    val attributes = HashMap<String, String>(1)
                    attributes.put("name", userName)
                    accountManager.sensitiveOperationOverInsecureConnection(true)
                    accountManager.createAccount(Localpart.from(userName), password,attributes)

                    return ""
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    return ex.message
                }
            }

            override fun onPostExecute(result: String?) {
                super.onPostExecute(result)
                if (result != null) {
                    if (result.isEmpty()) {
                        login(userName, password)
                    } else {
                        Toast.makeText(this@MainActivity, result, Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Something went wrong", Toast.LENGTH_SHORT)
                        .show()
                }

            }
        }

        registerTHread.execute()
    }

    fun configure() {
        // Private Data Storage
        ProviderManager.addIQProvider(
            "query",
            "jabber:iq:private",
            PrivateDataManager.PrivateDataIQProvider()
        )

        ProviderManager.addIQProvider(
            "ping", "urn:xmpp:ping",
            PingProvider()
        )

        ProviderManager.addExtensionProvider(
            "request", "urn:xmpp:receipts",
            DeliveryReceiptRequest.Provider()
        )

        ProviderManager.addIQProvider("query", "jabber:iq:time", TimeProvider())

        // Message Events
        ProviderManager.addExtensionProvider(
            "x", "jabber:x:event",
            EventProvider()
        )

        // Chat State
        /*ProviderManager.addExtensionProvider(
            "active",
            "http://jabber.org/protocol/chatstates",
            ChatStateExtension.Provider()
        )

        ProviderManager.addExtensionProvider(
            "composing",
            "http://jabber.org/protocol/chatstates",
            ChatStateExtension.Provider()
        )

        ProviderManager.addExtensionProvider(
            "paused",
            "http://jabber.org/protocol/chatstates",
            ChatStateExtension.Provider()
        )

        ProviderManager.addExtensionProvider(
            "inactive",
            "http://jabber.org/protocol/chatstates",
            ChatStateExtension.Provider()
        )

        ProviderManager.addExtensionProvider(
            "gone",
            "http://jabber.org/protocol/chatstates",
            ChatStateExtension.Provider()
        )*/

        // Group Chat Invitations
        ProviderManager.addExtensionProvider(
            "x", "jabber:x:conference",
            GroupChatInvitation.Provider()
        )

        // Service Discovery # Items
        ProviderManager.addIQProvider(
            "query",
            "http://jabber.org/protocol/disco#items",
            DiscoverItemsProvider()
        )

        // Service Discovery # Info
        ProviderManager.addIQProvider(
            "query",
            "http://jabber.org/protocol/disco#info",
            DiscoverInfoProvider()
        )

        // Data Forms
        ProviderManager.addExtensionProvider(
            "x", "jabber:x:data",
            DataFormProvider()
        )

        // MUC User
        ProviderManager.addExtensionProvider(
            "x",
            "http://jabber.org/protocol/muc#user", MUCUserProvider()
        )

        // MUC Admin
        ProviderManager.addIQProvider(
            "query",
            "http://jabber.org/protocol/muc#admin", MUCAdminProvider()
        )

        // MUC Owner
        ProviderManager.addIQProvider(
            "query",
            "http://jabber.org/protocol/muc#owner", MUCOwnerProvider()
        )

        // Version
        try {
            ProviderManager.addIQProvider(
                "query", "jabber:iq:version",
                Class.forName("org.jivesoftware.smackx.packet.Version")
            )
        } catch (e: ClassNotFoundException) {
            // Not sure what's happening here.
        }

        // VCard
        ProviderManager.addIQProvider(
            "vCard", "vcard-temp",
            VCardProvider()
        )

        // Offline Message Requests
        ProviderManager.addIQProvider(
            "offline",
            "http://jabber.org/protocol/offline",
            OfflineMessageRequest.Provider()
        )

        // Offline Message Indicator
        ProviderManager.addExtensionProvider(
            "offline",
            "http://jabber.org/protocol/offline",
            OfflineMessageInfo.Provider()
        )

        // Last Activity
        ProviderManager.addIQProvider(
            "query", "jabber:iq:last",
            LastActivity.Provider()
        )

        // User Search
        ProviderManager.addIQProvider(
            "query", "jabber:iq:search",
            UserSearch.Provider()
        )

        // SharedGroupsInfo
        ProviderManager.addIQProvider(
            "sharedgroup",
            "http://www.jivesoftware.org/protocol/sharedgroup",
            SharedGroupsInfo.Provider()
        )

        // JEP-33: Extended Stanza Addressing
        ProviderManager.addExtensionProvider(
            "addresses",
            "http://jabber.org/protocol/address",
            MultipleAddressesProvider()
        )

        // Privacy
        ProviderManager.addIQProvider(
            "query", "jabber:iq:privacy",
            PrivacyProvider()
        )

        ProviderManager.addIQProvider(
            "command",
            "http://jabber.org/protocol/commands",
            AdHocCommandDataProvider()
        )
        ProviderManager.addExtensionProvider(
            "malformed-action",
            "http://jabber.org/protocol/commands",
            AdHocCommandDataProvider.MalformedActionError()
        )
        ProviderManager.addExtensionProvider(
            "bad-locale",
            "http://jabber.org/protocol/commands",
            AdHocCommandDataProvider.BadLocaleError()
        )
        ProviderManager.addExtensionProvider(
            "bad-payload",
            "http://jabber.org/protocol/commands",
            AdHocCommandDataProvider.BadPayloadError()
        )
        ProviderManager.addExtensionProvider(
            "bad-sessionid",
            "http://jabber.org/protocol/commands",
            AdHocCommandDataProvider.BadSessionIDError()
        )
        ProviderManager.addExtensionProvider(
            "session-expired",
            "http://jabber.org/protocol/commands",
            AdHocCommandDataProvider.SessionExpiredError()
        )
    }

    private fun login(userName: String, password: String) {
        val loginThread = @SuppressLint("StaticFieldLeak")
        object : AsyncTask<Void, Void, Boolean>() {
            override fun doInBackground(vararg params: Void?): Boolean {
                try {

                    Config.conn1!!.login(userName, password)

                    if (Config.conn1!!.isAuthenticated) {
                        Log.e("app", "Auth done")
                        return true

                    } else {
                        Log.e("User Not Authenticated", "Needs to Update Password")
                        return false

                    }
                } catch (ex: java.lang.Exception) {
                    ex.printStackTrace()
                    return false
                }
            }

            override fun onPostExecute(result: Boolean?) {
                super.onPostExecute(result)
                if (result == true) {
                    Config.loginName = userName
                    Toast.makeText(this@MainActivity, "Logged In", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@MainActivity, ChatListActivity::class.java))
                    ActivityCompat.finishAffinity(this@MainActivity)
                } else {
                    Toast.makeText(this@MainActivity, "Auth Fail", Toast.LENGTH_SHORT).show()
                }
            }
        }
        loginThread.execute()
    }


    private fun initialiseConnection() {
        SmackConfiguration.addDisabledSmackClass("org.jivesoftware.smack.util.dns.minidns.MiniDnsResolver")
        Config.config = XMPPTCPConnectionConfiguration.builder()
//            .setUsernameAndPassword("test3", "12345")
            .setHost(DnsName.from(Config.openfire_host_server_HOST))
            .setResource(Config.openfire_host_server_RESOURCE)
//            .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
            .setXmppDomain(Config.openfire_host_server_SERVICE)
            .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
//            .setSocketFactory(SocketFactory.getDefault())
            .setPort(Config.openfire_host_server_PORT)
            .setConnectTimeout(5000)
            .enableDefaultDebugger()
            .setSendPresence(false)
//            .setDebuggerEnabled(true) // to view what's happening in detail
            .build()

        val connectionThread = @SuppressLint("StaticFieldLeak")
        object : AsyncTask<Void, Void, Boolean>() {
            override fun doInBackground(vararg params: Void?): Boolean {
                val conn1 = XMPPTCPConnection(Config.config)
                conn1.replyTimeout = 5000
                conn1.addConnectionListener(object : ConnectionListener {
                    override fun connected(connection: XMPPConnection?) {
                        Log.e("CONNECTION", "connected")
                    }

                    override fun connectionClosed() {
                        Log.e("CONNECTION", "connectionClosed")

                    }

                    override fun connectionClosedOnError(e: java.lang.Exception?) {
                        e?.printStackTrace()
                        Log.e("CONNECTION", "connectionClosedOnError")

                    }

                    override fun authenticated(
                        connection: XMPPConnection?,
                        resumed: Boolean
                    ) {
                        Log.e("CONNECTION", "authenticated")

                    }

                })


//                conn1.packetReplyTimeout =
//                    5000 // If not connected within 5 seconds then conn1 will be disconnected
                try {
                    val connection = conn1.connect()
                    if (conn1.isConnected) {
                        Config.conn1 = connection
                        val reconnect = ReconnectionManager.getInstanceFor(Config.conn1)
                        reconnect.enableAutomaticReconnection()
                    }

                    val ping = PingManager.getInstanceFor(Config.conn1)
                    ping.pingInterval = 60
                    ping.pingServerIfNecessary()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e("app", e.toString())
                }
                return conn1.isConnected
            }

            override fun onPostExecute(result: Boolean?) {
                super.onPostExecute(result)
                if (result == true) {
                    configure()
                    btnLogin.isEnabled = true
                    btnRegister.isEnabled = true
                } else {
                    Toast.makeText(this@MainActivity, "Not Connected", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
        connectionThread.execute()
    }
}
