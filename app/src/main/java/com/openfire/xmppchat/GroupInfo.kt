package com.openfire.xmppchat

import org.jivesoftware.smackx.muc.RoomInfo
import org.jxmpp.jid.EntityBareJid

data class GroupInfo(
    val roomId: EntityBareJid,
    val roomInfo: RoomInfo
)