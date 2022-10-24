package io.taptalk.TapTalk.Interface

interface TapCoreRoomListInterface {
    fun onChatRoomDeleted(roomID : String)

    fun onChatRoomMuted(roomID : String, expiryTime : Long)

    fun onChatRoomUnmuted(roomID : String)

    fun onChatRoomPinned(roomID : String)

    fun onChatRoomUnpinned(roomID : String)
}