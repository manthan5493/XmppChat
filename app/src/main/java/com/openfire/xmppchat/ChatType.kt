package com.openfire.xmppchat

enum class ChatType(val type: String, val viewType: Int) {
    CHAT("textMessage", 1),
    IMAGE("image", 2),
    AUDIO("audio", 3),
    VIDEO("video", 4),
    FILE("file", 5)
}