package com.openfire.xmppchat

import org.jivesoftware.smack.AbstractXMPPConnection
import org.jivesoftware.smack.roster.Roster
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import org.jivesoftware.smackx.muc.MultiUserChatManager


class Config {
    companion object {
        var config: XMPPTCPConnectionConfiguration? = null

        var conn1: AbstractXMPPConnection? = null
        var roster: Roster? = null
        var multiUserChatManager:MultiUserChatManager?=null

        var loginName: String? = null

        //        val openfire_host_server_IP = "xx.x.x.x.x.x.x." //(Example : 127.0.0.1)
        val openfire_host_server_HOST_DEMO = "192.168.11.171" //(Example : 127.0.0.1)
        val openfire_host_server_HOST_DEV =
            "openfire.brainvire.dev" //(Example : 127.0.0.1)


        val openfire_host_server_key =
            "--------------------" //(The key is required in cas you use REST API of openfire)

        val openfire_host_server_RESOURCE = "chat" //(Resource name of the server)
        //        val openfire_host_server_PORT = 5222 //This the port of chat server
        val openfire_host_server_PORT = 5222 //This the port of chat server
        val openfire_host_server_SERVICE_DEMO =
            "openfire.demoserver.com" //This is name of service that the server provides
        val openfire_host_server_SERVICE_DEV =
            "openfire.brainvire.dev" //This is name of service that the server provides
        val openfire_host_server_HOST = openfire_host_server_HOST_DEV
        val openfire_host_server_SERVICE = openfire_host_server_SERVICE_DEV
        val API_END_POINT = "https://openfire.brainvire.dev/"
        val CHAT_END_POINT = "openfire.brainvire.dev"
        val openfire_host_server_CONFERENCE_SERVICE = "conference.openfire.brainvire.dev"

    }
}