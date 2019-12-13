package com.openfire.xmppchat

import org.jivesoftware.smack.AbstractXMPPConnection
import org.jivesoftware.smack.roster.Roster
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration


class Config {
    companion object {
        var config: XMPPTCPConnectionConfiguration? = null

        var conn1: AbstractXMPPConnection? = null
        var roster: Roster?=null

        var loginName: String? = null

        //        val openfire_host_server_IP = "xx.x.x.x.x.x.x." //(Example : 127.0.0.1)
        val openfire_host_server_IP = "192.168.11.171" //(Example : 127.0.0.1)

        val openfire_host_server_key =
            "--------------------" //(The key is required in cas you use REST API of openfire)
        val openfire_host_server_RESOURCE = "chat" //(Resource name of the server)
        //        val openfire_host_server_PORT = 5222 //This the port of chat server
        val openfire_host_server_PORT = 5222 //This the port of chat server
        val openfire_host_server_SERVICE =
            "openfire.demoserver.com" //This is name of service that the server provides
        val openfire_host_server_CONFERENCE_SERVICE = "conference.//////"
    }
}